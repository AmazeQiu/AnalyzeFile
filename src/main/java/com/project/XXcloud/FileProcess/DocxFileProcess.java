package com.project.XXcloud.FileProcess;

import com.project.XXcloud.SparkSense.SparkSense;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DocxFileProcess implements FileProcess
{


    public void fileAnalyze(String email, String fileName, FileSystem fileSystem, String url)
    {
        InputStream fis=null;

        OutputStream fos=null;
        try
        {
            Path path=new Path(url);
            fis=fileSystem.open(path);
            XWPFDocument xwpfDocument=new XWPFDocument(fis);

            List<XWPFParagraph> paragraphs=xwpfDocument.getParagraphs();
            List<XWPFRun> xwpfRuns=new ArrayList<XWPFRun>();
            List<XWPFRun> tmpRuns=new ArrayList<XWPFRun>();
            List<String> originText=new ArrayList<String>();

            int num=paragraphs.size();
            int runNum=0;
            for(int i=0;i<num;++i)
            {
                tmpRuns=paragraphs.get(i).getRuns();
                runNum=tmpRuns.size();
                for(int j=0;j<runNum;++j)
                {
                    xwpfRuns.add(tmpRuns.get(i));
                    originText.add(tmpRuns.get(i).getText(0));
                }

            }
            Seq<String> tmpSeq = JavaConverters.asScalaIteratorConverter(originText.iterator()).asScala().toSeq();
            List<String> resultText= SparkSense.analyzeWordText(tmpSeq,num);
            for(int i=0;i<runNum;++i)
            {
                XWPFRun run=xwpfRuns.get(i);
                String content=run.getText(0);

                if(!(resultText.get(i).equals(content)))
                    run.setText(resultText.get(i));
            }


            fos=fileSystem.create(path);
            xwpfDocument.write(fos);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            try
            {
                fos.close();
                fis.close();
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }
}
