/usr/local/hive/
$SPARK_HOME/bin/spark-shell --packages it.nerdammer.bigdata:spark-hbase-connector_2.10:1.0.3 --conf spark.hbase.host=127.0.0.1
/*
	@Author: Chetan Khatri
	Description: This Scala script has written for HBase to Hive module, which reads table from HBase and dump it out to Hive
*/
import it.nerdammer.spark.hbase._
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.SparkSession


// Read HBase Table 
val hBaseRDD = sc.hbaseTable[(Option[String], Option[String], Option[String], Option[String], Option[String])]("university").select("stid", "name","subject","grade","city").inColumnFamily("emp")
// Iterate HBaseRDD and generate RDD[Row]
val rowRDD = hBaseRDD.map(i => Row(i._1.get,i._2.get,i._3.get,i._4.get,i._5.get))
// Create sqlContext for createDataFrame method
val sqlContext = new org.apache.spark.sql.SQLContext(sc)
// Create Schema Structure
object empSchema {
val stid = StructField("stid", StringType)
val name = StructField("name", StringType)
val subject = StructField("subject", StringType)
val grade = StructField("grade", StringType)
val city = StructField("city", StringType)
val struct = StructType(Array(stid, name, subject, grade, city))
}

import sqlContext.implicits._
// Create DataFrame with rowRDD and Schema structure
val stdDf = sqlContext.createDataFrame(rowRDD,empSchema.struct);
// Importing Hive 
import org.apache.spark.sql.hive
// Enable Hive with Hive warehouse in SparkSession
val spark = SparkSession.builder().appName("Spark Hive Example").config("spark.sql.warehouse.dir", "/usr/local/hive/warehouse").enableHiveSupport().getOrCreate()
// Importing spark implicits and sql package
import spark.implicits._
import spark.sql

// Saving Dataframe to Hive Table Successfully.
stdDf.write.mode("append").saveAsTable("employee")
