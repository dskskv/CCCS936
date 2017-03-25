package com.chetan.data.poc

/**
  * Created by chetan on 3/2/17.
  */
import org.yaml.snakeyaml.Yaml
import java.util.Map
import java.io.{File, FileInputStream}
object ConfigHelder {

def main(args: Array[String]) {
    val configFile = new FileInputStream(new File(args(0)))
    val yaml = new Yaml()
    val configParams = yaml.load(configFile).asInstanceOf[Map[String, Map[String, String]]]
    val sparkConfig: java.util.Map[String, String] = configParams.get("sparkJobConfig")
    println(sparkConfig.get("spark.hbase.host"))
    println(sparkConfig.get("spark.hbase.table"))
    
}
}
