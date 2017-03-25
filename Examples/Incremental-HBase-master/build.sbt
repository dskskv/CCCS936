import sbt.ExclusionRule
name := "IncrementalHBase"

version := "1.0"

scalaVersion := "2.11.0"

libraryDependencies += "org.apache.spark" % "spark-sql_2.11" % "2.0.0" % "provided" excludeAll(ExclusionRule(organization="joda-time"), ExclusionRule(organization="org.apache.hadoop"))
libraryDependencies +=  "org.apache.spark" % "spark-core_2.11" % "2.0.0" % "provided"

libraryDependencies +=  "org.apache.hbase" % "hbase-common" % "1.0.3" exclude("javax.servlet", "servlet-api") excludeAll(ExclusionRule(organization = "org.apache.hadoop"), ExclusionRule(organization="com.fasterxml.jackson.core"), ExclusionRule(organization="joda-time"))

libraryDependencies +=  "org.apache.hbase" % "hbase-client" % "1.0.3" exclude("javax.servlet", "servlet-api") excludeAll(ExclusionRule(organization = "org.apache.hadoop"), ExclusionRule(organization="com.fasterxml.jackson.core"), ExclusionRule(organization="joda-time"))

libraryDependencies +=  "org.apache.hbase" % "hbase-server" % "1.0.3" excludeAll(ExclusionRule(organization="com.fasterxml.jackson.core"), ExclusionRule(organization="joda-time"), ExclusionRule(organization = "org.mortbay.jetty"))

libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.7.0" exclude("javax.servlet", "servlet-api")  excludeAll(ExclusionRule(organization="com.fasterxml.jackson.core"), ExclusionRule(organization="joda-time"))

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
  case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("com", "google", xs @ _*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
  case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
  case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
  case "about.html" => MergeStrategy.rename
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
  case "META-INF/mailcap" => MergeStrategy.last
  case "META-INF/mimetypes.default" => MergeStrategy.last
  case "plugin.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case "pom.properties" => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}




