package com.chetan.poc.hbase

/**
  * Created by chetan on 24/1/17.
  */
import org.apache.hadoop.hbase.{CellUtil, HBaseConfiguration}
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.KeyValue.Type
import org.apache.spark.sql.SparkSession
import scala.collection.JavaConverters._
import java.util.Date
import java.text.SimpleDateFormat


object IncrementalJob {
    val APP_NAME: String = "SparkHbaseJob"
    var HBASE_DB_HOST: String = null
    var HBASE_TABLE: String = null
    var HBASE_COLUMN_FAMILY: String = null
    var HIVE_DATA_WAREHOUSE: String = null
    var HIVE_TABLE_NAME: String = null
  def main(args: Array[String]) {
    // Initializing HBASE Configuration variables
    HBASE_DB_HOST="127.0.0.1"
    HBASE_TABLE="university"
    HBASE_COLUMN_FAMILY="emp"
    // Initializing Hive Metastore configuration
    HIVE_DATA_WAREHOUSE = "/usr/local/hive/warehouse"
    // Initializing Hive table name - Target table
    HIVE_TABLE_NAME = "employees"
    // setting spark application
    // val sparkConf = new SparkConf().setAppName(APP_NAME).setMaster("local")
    //initialize the spark context
    //val sparkContext = new SparkContext(sparkConf)
    //val sqlContext = new org.apache.spark.sql.SQLContext(sparkContext)
    // Enable Hive with Hive warehouse in SparkSession
    val spark = SparkSession.builder().appName(APP_NAME).config("hive.metastore.warehouse.dir", HIVE_DATA_WAREHOUSE).config("spark.sql.warehouse.dir", HIVE_DATA_WAREHOUSE).enableHiveSupport().getOrCreate()
    import spark.implicits._
    import spark.sql

    val conf = HBaseConfiguration.create()
    conf.set(TableInputFormat.INPUT_TABLE, HBASE_TABLE)
    conf.set(TableInputFormat.SCAN_COLUMNS, HBASE_COLUMN_FAMILY)
    // Load an RDD of rowkey, result(ImmutableBytesWritable, Result) tuples from the table
    val hBaseRDD = spark.sparkContext.newAPIHadoopRDD(conf, classOf[TableInputFormat],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result])

    println(hBaseRDD.count())
    //hBaseRDD.foreach(println)

    //keyValue is a RDD[java.util.list[hbase.KeyValue]]
    val keyValue = hBaseRDD.map(x => x._2).map(_.list)

    //outPut is a RDD[String], in which each line represents a record in HBase
    val outPut = keyValue.flatMap(x =>  x.asScala.map(cell =>

      HBaseResult(
        Bytes.toInt(CellUtil.cloneRow(cell)),
        Bytes.toStringBinary(CellUtil.cloneFamily(cell)),
        Bytes.toStringBinary(CellUtil.cloneQualifier(cell)),
        cell.getTimestamp,
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date(cell.getTimestamp.toLong)),
        Bytes.toStringBinary(CellUtil.cloneValue(cell)),
        Type.codeToType(cell.getTypeByte).toString
                )
      )
    ).toDF()
    // Output dataframe
    outPut.show

    // get timestamp
    val datetimestamp_threshold = "2016-08-25 14:27:02:001"
    val datetimestampformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").parse(datetimestamp_threshold).getTime()

    // Resultset filteration based on timestamp
    val filtered_output_timestamp = outPut.filter($"colDatetime" >= datetimestampformat)
    // Resultset filteration based on rowkey
    val filtered_output_row = outPut.filter($"colDatetime".between(1668493360,1668493365))


    // Saving Dataframe to Hive Table Successfully.
    filtered_output_timestamp.write.mode("append").saveAsTable(HIVE_TABLE_NAME)
  }
  case class HBaseResult(rowkey: Int, colFamily: String, colQualifier: String, colDatetime: Long, colDatetimeStr: String, colValue: String, colType: String)
}
