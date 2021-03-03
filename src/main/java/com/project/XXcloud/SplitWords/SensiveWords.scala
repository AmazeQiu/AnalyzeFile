package com.project.XXcloud.SplitWords

import XMLUtil.XMLUtil

import scala.io.Source

object SensiveWords {
  val sensiveWordsDir=XMLUtil.getSensiveWordsDir;
  private val inputFile = Source.fromFile(sensiveWordsDir+"\\sensiveWords.txt");
  var sensiveWords = Array[String]();

  for (line <- inputFile.getLines()) {
    sensiveWords = sensiveWords ++ line.split("\\s+")
  }
}
