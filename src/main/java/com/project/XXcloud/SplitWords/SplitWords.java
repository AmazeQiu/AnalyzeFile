package com.project.XXcloud.SplitWords;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;

public class SplitWords
{
    private static String[] sensivesWords=SensiveWords.sensiveWords();
    public static String splitWords(String line)
    {
        //保存返回结果
        StringBuffer sb=new StringBuffer(line);

        //构建IK分词器，使用smart分词模式
        Analyzer analyzer = new IKAnalyzer(true);

        //获取Lucene的TokenStream对象
        TokenStream ts = null;
        try {
            ts = analyzer.tokenStream("myfield", new StringReader(line));
            //获取词元位置属性
            OffsetAttribute  offset = ts.addAttribute(OffsetAttribute.class);
            //获取词元文本属性
            CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
            //获取词元文本属性
            TypeAttribute type = ts.addAttribute(TypeAttribute.class);


            //重置TokenStream（重置StringReader）
            ts.reset();
            //迭代获取分词结果
            while (ts.incrementToken()) {
                //System.out.println(offset.startOffset() + " - " + offset.endOffset() + " : " + term.toString() + " | " + type.type());
                if(checkSensive(term.toString()))
                {
                    int num=offset.endOffset()-offset.startOffset();
                    StringBuffer s_replace=new StringBuffer();
                    for(int i=0;i<num;++i)
                    {
                        s_replace.append("*");
                    }
                    sb.replace(offset.startOffset(),offset.endOffset(),s_replace.toString());
                }
            }
            //关闭TokenStream（关闭StringReader）
            ts.end();   // Perform end-of-stream operations, e.g. set the final offset.
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //释放TokenStream的所有资源
            if(ts != null){
                try {
                    ts.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    private static boolean checkSensive(String word)
    {
        for(String s:sensivesWords)
        {
            if(s.equals(word))
                return true;
        }
        return false;
    }
}

