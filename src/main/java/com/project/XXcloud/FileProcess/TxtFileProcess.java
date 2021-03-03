package com.project.XXcloud.FileProcess;


import com.project.XXcloud.SparkSense.SparkSense;
import org.apache.hadoop.fs.FileSystem;


public class TxtFileProcess implements FileProcess
{

    public void fileAnalyze(String email, String fileName, FileSystem fileSystem,String url)
    {
        SparkSense.analyzeTextFile(email,fileName);

    }

}
