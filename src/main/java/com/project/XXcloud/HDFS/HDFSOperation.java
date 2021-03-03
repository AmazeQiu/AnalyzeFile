package com.project.XXcloud.HDFS;

import XMLUtil.XMLUtil;
import com.project.XXcloud.FileProcess.FileProcess;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;


public class HDFSOperation
{
    private static Configuration conf = new Configuration();
    private static FileSystem fileSystem;
    private static String ip;
    private static String port;

    static {
        try {
            String[] strings= XMLUtil.getHdfsConf();
            ip = strings[0];
            port=strings[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**创建目录
     * 参数1：用户邮箱
     * 返回值：创建状态，成功返回0
     * */
    public static int createDir(String email) throws Exception
    {
        String uri="hdfs://"+ip+":"+port+"/"+email;
        fileSystem=FileSystem.get(URI.create(uri),conf);
        Path path=new Path(uri);
        fileSystem.mkdirs(path);
        fileSystem.close();
        return 0;
    }

    /**
     *下载文件
     * 参数1：用户邮箱； 参数2：文件名； 参数3： 输出流
     * 返回值：下载结果：成功file对象，文件不存在返回null
     * */
    public static int downloadFile(String email, String fileName,OutputStream outputStream) throws IOException {

        String uri="hdfs://"+ip+":"+port+"/"+email+"/"+fileName;

        fileSystem= FileSystem.get(URI.create(uri),conf);
        InputStream in=null;
        try
        {
            in=fileSystem.open(new Path(uri));
            IOUtils.copyBytes(in,outputStream,4096,false);
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        finally {
            IOUtils.closeStream(in);
            fileSystem.close();
        }

        return 0;
    }


    /**
     *上传文件，供客户端调用
     * 参数1：用户邮箱； 参数2：文件名； 参数3：文件对象
     * 返回值：上传结果：成功返回0
     * */
    public static int uploadFile(String email, String fileName, byte[] file) throws Exception
    {
        String uri="hdfs://"+ip+":"+port+"/"+email+"/"+fileName;
        fileSystem=FileSystem.get(URI.create(uri),conf);
        OutputStream out=fileSystem.create(new Path(uri));
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(file);
        IOUtils.copyBytes(byteArrayInputStream,out,4096,true);
        IOUtils.closeStreams(out);

        byteArrayInputStream.close();
        out.close();

        fileSystem.close();
        return 0;
    }


    public static int uploadFile(String email, String fileName, InputStream in) throws Exception
    {
        String uri="hdfs://"+ip+":"+port+"/"+email+"/"+fileName;
        fileSystem=FileSystem.get(URI.create(uri),conf);
        OutputStream out=fileSystem.create(new Path(uri));
        IOUtils.copyBytes(in,out,4096,true);
        out.close();
        fileSystem.close();
        return 0;
    }


    /**
    * 获取指定目录下的文件列表
    * 参数1：用户邮箱
    * 返回值：文件名列表 ArrayList
    * */
    public static ArrayList<String> listFile(String email) throws IOException {

        String uri="hdfs://"+ip+":"+port+"/"+email;

        ArrayList<String> fileList=new ArrayList<String>();
        fileSystem=FileSystem.get(URI.create(uri),conf);
        FileStatus[] statuses;
        try
        {
            statuses = fileSystem.listStatus(new Path(uri));
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;//路径不存在
        }

        for (FileStatus status : statuses)
        {
            String[] strings=status.getPath().toString().split("/");
            int len= strings.length;
            fileList.add(strings[len-1]);
        }
        fileSystem.close();

        return fileList;
    }

    /**
    * 删除文件
    * 参数1：用户邮箱； 参数2：文件名
    * 返回值：删除状态：成功删除返回true,否则返回false
    * */
    public static boolean deleteFile(String email, String fileName) throws IOException
    {
        String uri="hdfs://"+ip+":"+port+"/"+email+"/"+fileName;
        fileSystem=FileSystem.get(URI.create(uri),conf);
        Path delef;
        delef=new Path(uri);
        boolean isDeleted= fileSystem.delete(delef,true);
        fileSystem.close();
        return isDeleted;
    }

    /**
     * 功能删除目录
     * 参数1：用户邮箱
     * 返回值：删除状态：成功删除返回true,否则返回false
     * */
    public static boolean deleteFile(String email) throws IOException
    {
        String uri="hdfs://"+ip+":"+port+"/"+email;
        fileSystem=FileSystem.get(URI.create(uri),conf);
        Path delef;
        delef=new Path(uri);
        boolean isDeleted= fileSystem.delete(delef,true);
        fileSystem.close();
        return isDeleted;
    }


    /**
     * 功能：写入文件，供Spark调用
     * */
    public static int writeFile(String email, String fileName,String sourceName) throws IOException {
        FileInputStream fis=new FileInputStream(new File(sourceName));
        String uri="hdfs://"+ip+":"+port+"/"+email+"/"+fileName;
        fileSystem=FileSystem.get(URI.create(uri),conf);
        OutputStream out=fileSystem.create(new Path(uri));
        IOUtils.copyBytes(fis,out,4096,true);

        fis.close();
        out.close();
        fileSystem.close();
        return 0;
    }
}
