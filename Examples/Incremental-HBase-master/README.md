# Incremental-HBase
Incremental HBase POC


### 1. Problem Statement

Determine if incremental load is possible from HBase to Hive:

As a Reporting System User, needs to determine if incremental load from Hbase Market basket table to Transaction line items table in Hive.

With below acceptance criteria:
	 	 	
1) Need ETL job that can read only incremental data from Hbase table each week and load in to Hive.
2) Each week during the load, the load Job should pick only newly updated data(compared to previous week) from HBase Market Basket table and load in to Transaction line items in Hive.
3) The program should be able to read configuration information from Config server.
4) Output from the program can be in any of the formats. Preferred option is direct load in to Hive table.

### 2. Proposed Solution Approaches:


Yes it is possible but not with any tool (ie. library), such as if we would like to do incremental load from RDBMS to Hive, Apache sqoop is the key player tool who does with below command and then you can schedule it as cron.

Example,

```sh sqoop job --create myssb1 -- import --connect jdbc:mysql://<hostname>:<port>/sakila --username admin --password admin --driver=com.mysql.jdbc.Driver --query "SELECT address_id, address, district, city_id, postal_code, alast_update, cityid, city, country_id, clast_update FROM(SELECT a.address_id as address_id, a.address as address, a.district as district, a.city_id as city_id, a.postal_code as postal_code, a.last_update as alast_update, c.city_id as cityid, c.city as city, c.country_id as country_id, c.last_update as clast_update FROM sakila.address a INNER JOIN sakila.city c ON a.city_id=c.city_id) as sub WHERE $CONDITIONS" --incremental lastmodified --check-column alast_update --last-value 1900-01-01 --target-dir /user/cloudera/ssb7 --hive-import --hive-table test.sakila -m 1 --hive-drop-import-delims --map-column-java address=String```

I did couple of research at HBase community and HBase incubator projects families such as Apache Phoenix.

#### 2.1) Read HBase as a Spark RDD

[![alt text](/images/1.png)]

At the Apache HBase side we will maintain the indexing on Integer column, so when next time spark job runs it take newly arrived rows.
Indexing will be done in policy as below values:
0 - By default
1 - Sent
2 - Acknowledged 
Next time, when Spark job runs it  only takes rows having value 0 and 1.

#### 2.2) Use  Apache Phoenix along with HBase

[![alt text](/images/2.png)]

1. compiling your SQL queries to native HBase scans
2. determining the optimal start and stop for your scan key
3. orchestrating the parallel execution of your scans
4. bringing the computation to the data by
5. pushing the predicates in your where clause to a server-side filter
6. executing aggregate queries through server-side hooks (called co-processors)


In addition to these items, we’ve got some interesting enhancements in the works to further optimize performance:
1. secondary indexes to improve performance for queries on non row key columns
2. stats gathering to improve parallelization and guide choices between optimizations
3. skip scan filter to optimize IN, LIKE, and OR queries
4. optional salting of row keys to evenly distribute write load


### Setup Process:

1. download and expand our installation tar
2. copy the phoenix server jar that is compatible with your HBase installation into the lib directory of every region server
3. restart the region servers
4. add the phoenix client jar to the classpath of your HBase client
5. download and setup SQuirrel as your SQL client so you can issue adhoc SQL against your HBase cluster

For more: Spark Transformation on HBase with Phoenix as a wrapper
Ref. https://phoenix.apache.org/phoenix_spark.html

#### 2.3) Querying HBase tables with Apache Drill and Apache Spark

[![alt text](/images/3.png)]

Apache Drill supports DAG(Directly Acyclic Graph) Engine & Apache Spark can read as a DataFrame, then apply Spark Transformations on Dataframe and Save processed DataFrame to Hive via HiveContext in Spark.

#### 2.4) SparkSQL on HBase

[![alt text](/images/4.png)]

There is no official Spark core package is available to read HBase with SparkSQL, but widely used and popular packages are available at https://spark-packages.org from hortonworks, IBM, Huawei etc key-players where spark only adapt after reviewing and verification. 


### Approach Dependencies:
All the above approach widely depends on the version of Apache HBase, Apache Spark, Apache Hive and other related libraries.

### Steps to run current repository code

1. Pull this repository.
2. Go to root of this project.
3. sbt clean assembly
4. go to the target directory for getting Uber Jar
 
 /IncrementalHBase/target/scala-2.11/IncrementalHBase-assembly-1.0.jar
 
5. Execute the Spark Job
``` $SPARK_HOME/bin/spark-submit --class com.chetan.poc.hbase.IncrementalJob /home/chetan/Documents/open-contribution/IncrementalHBase/target/scala-2.11/IncrementalHBase-assembly-1.0.jar```







