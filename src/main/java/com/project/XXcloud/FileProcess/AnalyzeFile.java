package com.project.XXcloud.FileProcess;

import XMLUtil.XMLUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import java.io.File;
import java.net.URI;


public class AnalyzeFile
{
    public static void analyze(String ip, String port, String fileMsg, Configuration conf) throws Exception {
        String email=fileMsg.split(" ")[0];
        String fileName=fileMsg.split(" ")[1];
        FileSystem fileSystem;

        String uri="hdfs://"+ip+":"+port+"/"+email+"/"+fileName;
        try
        {
            fileSystem=FileSystem.get(URI.create(uri),conf);
            String[] strings=fileName.split("\\.");
            String tagName=strings[1];
            FileProcess fileProcess=(FileProcess) XMLUtil.getFileProcessClass(tagName);
            fileProcess.fileAnalyze(email,fileName,fileSystem,uri);
            fileSystem.close();
        }
        catch (Exception e)
        {
            String tmpdir=XMLUtil.getTemDir();
            File file=new File(tmpdir+email+"_tmp");
            if(file.exists())
                file.delete();
            e.printStackTrace();
        }


    }
}
