/* Batch Job Processing with Spark 
   Author: Chetan Khatri, Data Science Lab, University of Kachchh.
   Web: http://cs.kutchuni.edu.in
*/

Description:

1. High level Architecture flow:

JSON -> HDFS -> SparkSQL -> Write to HDFS (Parquet) [Assume it is Master table] -> Read another table from JSON to HDFS [Assume it is Transactional Table] -> Read Parquet from HDFS -> Join Master and Transactional Table -> Store Processed Dataframe to MongoDB (Processed Data warehouse)

2. Start Spark Shell with Tuning Hyper parameters 

Description:

When you start spark engine, by default all the hyper parameters are being set. But if you would like to optimize your Spark Job and would like to get efficient throughput at that time you need to tune some hyper parameters.

You need to set hyper parameter's value when you start spark-shell, pre-set value of hyper parameter can be found at conf/spark-defaults.conf in apache spark. If you want your all the parameters are dynamic for all the different types of Batch Data Processing then you can pass while starting spark deamon engine or else you can update the values at conf/spark-defaults.conf.

We are discussing Some of the important hyper parameters. Assume we have 10 GB of RAM for below example.

1. spark.driver.memory=8g
Driver is the Main Deamon process for SparkContext, Amount of memory to use for the driver process. By Default value is 1g.
2. spark.executor.memory=5g
Many Executors can run under single Driver at Spark. Amount of memory to use per executor process.
3. spark.kryoserializer.buffer.max=256
Maximum allowable size of Kryo serialization buffer. This must be larger than any object you attempt to serialize. Increase this if you get a "buffer limit exceeded" exception inside Kryo.
4. spark.dynamicAllocation.enabled=true
Whether to use dynamic resource allocation, which scales the number of executors registered with this application up and down based on the workload. 
5. spark.shuffle.service.enabled=true
Enables the external shuffle service. This service preserves the shuffle files written by executors so the executors can be safely removed. This must be enabled if spark.dynamicAllocation.enabled is "true". The external shuffle service must be set up in order to enable it.
6. spark.rpc.askTimeout=300s
Duration for an RPC ask operation to wait before timing out.
7. spark.dynamicAllocation.minExecutors=5
Lower bound for the number of executors if dynamic allocation is enabled.
8. spark.sql.shuffle.partitions=1024
Configures the number of partitions to use when shuffling data for joins or aggregations.

[Read More] http://spark.apache.org/docs/latest/configuration.html , https://spark.apache.org/docs/1.2.0/sql-programming-guide.html

Implementation:

1) Start spark shell

./spark-shell --packages com.stratio.datasource:spark-mongodb_2.10:0.11.2 --conf spark.executor.memory=5g --conf spark.driver.memory=8g --conf spark.kryoserializer.buffer.max=256 --conf spark.dynamicAllocation.enabled=true --conf spark.shuffle.service.enabled=true --conf spark.rpc.askTimeout=300s --conf spark.dynamicAllocation.minExecutors=5 --conf spark.sql.shuffle.partitions=1024

Note: When you are saying --packages and the path you are importing Artifact / package runtime and you can use it once spark shell starts.

2) Import Packages

import com.mongodb.casbah.{WriteConcern => MongodbWriteConcern}
import com.stratio.datasource.mongodb._
import com.stratio.datasource.mongodb.config.MongodbConfig._
import com.stratio.datasource.mongodb.config.MongodbConfigBuilder
import com.mongodb.{WriteConcern => _, _}
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

3) Configure MongoDB Connection

var MONGO_DB_HOST : String = "IP Address"
var MONGO_DB_PORT : String = "27017"
var MONGO_NAMESPACE : String = "MongoDB Schema Name"

val host: String = String.format("%s:%s", MONGO_DB_HOST, MONGO_DB_PORT)

Note: You have to change IP Table entry in MongoDB Configuration, by default bind ip at mongodb configuration is set to localhost(127.0.0.1) you need to replace it with 0.0.0.0 so it can be publicly accessible from any ip address.
Change this if you are using cloud instances or else your mongodb and spark runs on different instances / servers.

Edit /etc/mongod.conf and replace the value of bind_ip from 127.0.0.1 to 0.0.0.0 

4) Read JSON from HDFS

val dsStudentMaster = sqlContext.read.json("/home/ubuntu/dskskv/admission.json")

Note: Over here we are assuming that you have transferred admission.json file from local to HDFS by "hadoop fs -put localpath to destination"

5) Spark SQL Transformation

dsStudentMaster.registerTempTable("admissionmaster")

val selectedStudents = sqlContext.sql("SQL_QUERY") // Write your sql query, use admissionmaster as a table, which we registered as a temperory table in step:5

6) Store Sql Transformation output to HDFS (in Parquet format, parquet is columnar format)

selectedStudents.write.mode("overwrite").format("parquet").save("StudentMaster")

7) Read already stored file from HDFS at step 6.

var studentMaster = sqlContext.read.parquet("StudentMaster")
studentMaster.registerTempTable("studentmaster")

8) Read another JSON from HDFS and store it as temp table (Assume it is Transactional Table)

val dsExamMaster = sqlContext.read.json("/home/ubuntu/dskskv/dsexam.json")
dsExamMaster.registerTempTable("exammaster")

9) Spark SQL Join Query with Master Table(studentmaster) and Transactional Table(exammaster)

val dsResultMaster = sqlContext.sql("SQL_JOIN_QUERY")

10) Build MongoDB Configuration

val dsResultBuilder = MongodbConfigBuilder(Map(Host -> List(host), Database -> MONGO_NAMESPACE,Collection -> "dsresult", SamplingRatio -> 1.0, WriteConcern -> "normal"))
val dsResultWriteConfig = dsResultBuilder.build()

dsResultMaster.saveToMongodb(dsResultWriteConfig) // Saving Result of Spark SQL Join query as dsResultMaster Spark Dataframe to dsresult MongoDB Collection.



Reference:

[1] Scala API for MongoDB datasource and Apache Spark.
[Online] https://github.com/Stratio/spark-mongodb/blob/master/doc/src/site/sphinx/First_Steps.rst#scala-api

