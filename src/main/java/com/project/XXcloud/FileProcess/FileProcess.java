package com.project.XXcloud.FileProcess;

import org.apache.hadoop.fs.FileSystem;

public interface FileProcess
{
    public abstract void fileAnalyze(String email, String fileName, FileSystem fileSystem, String url);
}
