package ssdut.xueqiu

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

import java.text.SimpleDateFormat
import java.util.{Date, HashMap}
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

object TaskTwo {
  var result = new HashMap[String, Array[String]]()

  def main(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val tblName = request.getParameter("tblName")
    println(s"$tblName analysis start")
    val startTime = System.currentTimeMillis

    if (result.get(tblName) == null) {
      println("spark start")
      val conf = new SparkConf()
        .setMaster("spark://192.168.17.10:7077")
        .setAppName("TaskTwo")
        .setJars(Seq("E:\\xueqiuAnalysis\\target\\xueqiuAnalysis-1.0-SNAPSHOT.jar")) //程序导出的 Jar包路径，根据实际情况修改
        .setIfMissing("spark.driver.host", "192.168.17.1") //设置IDEA所在机器与集群通信的网卡IP（VMnet8虚拟网卡）
        .setExecutorEnv("HADOOP_USER_NAME", "hadoop") //设置Hadoop环境变量，用于访问HDFS等
      //      .setExecutorEnv("SPARK_WORKER_MEMORY", "2G") //设置Executor内存，用于访问HDFS等

      val spark = SparkSession.builder().config(conf).getOrCreate() //创建 SparkSession 对象 //此处的spark是SparkSession对象，用于隐式转换

      import spark.implicits._

      val df = spark.read.option("delimiter", ",").option("header", "true").csv(s"hdfs://192.168.17.10:9000/data/$tblName.csv")

      df.createTempView(tblName)

      val df2 = spark.sql(s"select timestamp, open, close, low, high from $tblName order by timestamp")

      val sdf = new SimpleDateFormat("yyyy/MM/dd") //要转换的时间格式
      val df3 = df2.rdd.map(row => {
        (sdf.format(new Date(row.getString(0).toLong)), row.getString(1), row.getString(2), row.getString(3), row.getString(4))
      }).toDF("date", "open", "close", "low", "high")

      val resultMap = df3.toJSON.collect()

      result.put(tblName, resultMap)

      spark.close()
    } else {
      println("data already exists")
    }

    var isPriority = 0
    if (request.getSession().getAttribute("username") != null) {
      isPriority = request.getSession().getAttribute("acl").asInstanceOf[Int]
    }

    var resultMap: Array[String] = new Array[String](0)
    if (isPriority == 1) {
      println("is priority")
      resultMap = result.get(tblName)
    } else {
      println("not priority")
      resultMap = result.get(tblName).takeRight(30)
    }

    val str = resultMap.mkString("[", ",", "]")

    val out = response.getWriter

    //    println(str)
    out.print(str)

    out.flush()
    out.close()

    val endTime = System.currentTimeMillis
    val usedTime = (endTime - startTime) / 1000
    println(s"$tblName analysis finish used time: $usedTime s")
  }
}
