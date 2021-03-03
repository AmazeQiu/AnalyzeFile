package com.project.XXcloud.FileProcess;


import XMLUtil.XMLUtil;
import com.project.XXcloud.SparkSense.SparkSense;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DocFileProcess implements FileProcess
{


    public void fileAnalyze(String email, String fileName,FileSystem fileSystem,String url)
    {
        InputStream fis=null;
        Range range=null;
        OutputStream fos=null;
        try
        {
            String tmpPath=XMLUtil.getTemDir();
            Path path=new Path(url);
            fis=fileSystem.open(path);
            HWPFDocument hwpfDocument=new HWPFDocument(fis);
            range=hwpfDocument.getRange();

            List<String> originText=new ArrayList<String>();

            //处理文字
            int num=range.numParagraphs();
            for(int i=0;i<num;++i)
            {
                originText.add(range.getParagraph(i).text());
            }
            Seq<String> tmpSeq = JavaConverters.asScalaIteratorConverter(originText.iterator()).asScala().toSeq();
            List<String> resultText= SparkSense.analyzeWordText(tmpSeq,num);
            for(int i=0;i<num;++i)
            {
                Paragraph p=range.getParagraph(i);
                String content=p.text();
                if(!(resultText.get(i).equals(content)))
                    p.replaceText(content,resultText.get(i));
            }


            //处理图片
            byte[] dataStream=hwpfDocument.getDataStream();
            int numChar=range.numCharacterRuns();


            PicturesTable pTable=new PicturesTable(hwpfDocument,dataStream,new byte[1024]);
            for(int j=0;j<numChar;++j)
            {
                CharacterRun cRun=range.getCharacterRun(j);
                //是否有图片
                boolean has=pTable.hasPicture(cRun);
                if(has)
                {
                    Picture picture=pTable.extractPicture(cRun,true);

                    //大于300bits的图片才弄下来
                    if(picture.getSize()>300)
                    {
                        String picTmpPath=tmpPath+"/"+email+fileName+j+".png";
                        File tmpFile=new File(picTmpPath);
                        FileOutputStream imgFos=new FileOutputStream(picTmpPath);
                        picture.writeImageContent(imgFos);
                        imgFos.flush();
                        imgFos.close();
                        if(ImageCheck.imageCheck(picTmpPath))
                        {
                            cRun.delete();
                        }
                        tmpFile.delete();
                    }

                }
            }

           fos=fileSystem.create(path);
            hwpfDocument.write(fos);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            range.delete();
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
