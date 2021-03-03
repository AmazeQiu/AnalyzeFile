package com.project.XXcloud.FileProcess;

import com.project.XXcloud.SparkSense.SparkSense;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageCheck
{
    /**
     * @function 检测图片中的文字有没有出现敏感词
     * @param path:图片文件的路径
     * @return 若图片文字有敏感内容则返回true，否则返回false
     * */
    public static boolean imageCheck(String path)
    {
        File imageFile = new File(path);
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        /*
        如果采用的不是使用包默认的testdata建议加上一下一行代码：
        instance.setDatapath("D:\\myworks\\BusinessManager\\WebRoot\\tessdata");
        */
//         ITesseract instance = new Tesseract1(); // JNA Direct Mapp
        instance.setLanguage("chi_sim");//添加中文字库

        try {
            String result = instance.doOCR(imageFile);
            String[] strings=result.split("\n");
            List<String> originText=new ArrayList<String>(Arrays.asList(strings));
            Seq<String> tmpSeq = JavaConverters.asScalaIteratorConverter(originText.iterator()).asScala().toSeq();
            List<String> resultText= SparkSense.analyzeWordText(tmpSeq,originText.size());
            System.out.println(result);
            for(int i=0;i<originText.size();++i)
            {
                if(!originText.get(i).equals(resultText.get(i)))
                {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        imageFile.delete();
        return false;
    }
}
