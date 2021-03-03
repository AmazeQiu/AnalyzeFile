package com.project.XXcloud.SparkSense

import java.io.File
import java.util

import XMLUtil.XMLUtil
import com.project.XXcloud.HDFS.HDFSOperation
import com.project.XXcloud.SplitWords.SplitWords
import org.apache.spark.{SparkConf, SparkContext}


object SparkSense {
  private val hdfsConf = XMLUtil.getHdfsConf();
  private val ip = hdfsConf(0);
  private val port = hdfsConf(1);
  private var conf: SparkConf = null
  private var sc: SparkContext = null
  /**
   *
   * */

  def initialSpark():Unit=
    {
      conf= new SparkConf().setAppName(System.currentTimeMillis().toString).setMaster("local[2]")
      sc= new SparkContext(conf);
    }
  /**
   * 功能：分析单个文本文件，屏蔽敏感词
   * 传入参数：参数1：用户文件夹名称（即用户邮箱），参数2：文件名
   * 返回值：无
   * */
  def analyzeTextFile(email: String, fileName: String): Unit = {

    val data = sc.textFile("hdfs://" + ip + ":" + port + "/" + email + "/" + fileName);
    val tmpPath=XMLUtil.getTemDir;
    data.map(x =>SplitWords.splitWords(x)).repartition(1).saveAsTextFile(tmpPath + email + "_tmp");

    HDFSOperation.writeFile(email, fileName, tmpPath + email + "_tmp\\part-00000");
    val path: File = new File(tmpPath+ email + "_tmp");
    deleteDir(path);

  }

  def analyzeWordText(originText:Seq[String],num:Int):util.ArrayList[String]=
  {
    val resultText:util.ArrayList[String]=new util.ArrayList[String](num);
    val data=sc.parallelize(originText);
    data.map(x=>SplitWords.splitWords(x)).zipWithIndex().collect().map(x=>{resultText.add(x._2.toInt,x._1);x});
    resultText;
  }

  /**
   * 删除一个文件夹,及其子目录
   *
   * @param dir
   */
  private def deleteDir(dir: File): Unit = {
    val files = dir.listFiles()
    files.foreach(f => {
      if (f.isDirectory) {
        deleteDir(f)
      } else {
        f.delete()
      }
    })
    dir.delete()

  }
}
