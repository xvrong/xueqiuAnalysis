package ssdut.xueqiu;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


@WebListener
public class init implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        // 将文件上传到hdfs文件系统中
        try {
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", "hdfs://192.168.17.10:9000");    //设置配置对象的参数
            FileSystem fs;

            fs = FileSystem.get(conf);
            Path dataPath = new Path("E:\\xueqiuAnalysis\\data");

            File file = new File(dataPath.toString());
            File[] tempList = file.listFiles();

            for (File value : tempList) {
                Path src = new Path(value.toString());
                Path des = new Path("/data/" + value.getName());
                if (!fs.exists(des)) {
                    fs.copyFromLocalFile(src, des);
                }
                System.out.println("upload " + value.getName() + " to hdfs");
            }
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 连接hive创建表
        try {
            String drv = "org.apache.hive.jdbc.HiveDriver";                    //Hive驱动名称
            String url = "jdbc:hive2://192.168.17.10:10000";        //默认端口号10000
            String usr = "hive";
            String pwd = "123456";
            Class.forName(drv);
            Connection conn = DriverManager.getConnection(url, usr, pwd);
            Statement stmt = conn.createStatement();

            String database = "xv_rong";
            String sql = "CREATE DATABASE IF NOT EXISTS " + database;
            stmt.execute(sql);

            System.out.println("Create Database OK!\n");

            stmt.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
//        try {
//            Configuration conf = new Configuration();
//            conf.set("fs.defaultFS", "hdfs://192.168.17.10:9000");	//设置配置对象的参数
//            FileSystem fs = null;
//            // 自动删除文件
//            fs = FileSystem.get(conf);
//            Path des = new Path("/data");
//            fs.deleteOnExit(des);
//            fs.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            String drv = "org.apache.hive.jdbc.HiveDriver";                    //Hive驱动名称
//            String url = "jdbc:hive2://192.168.17.10:10000";        //默认端口号10000
//            String usr = "hive";
//            String pwd = "123456";
//            Class.forName(drv);
//            Connection conn = DriverManager.getConnection(url, usr, pwd);
//            Statement stmt = conn.createStatement();
//
//            String database = "xv_rong";
//            String sql = "DROP DATABASE IF EXISTS " + database +" CASCADE";
//            stmt.execute(sql);
//
//            stmt.close();
//            conn.close();
//        } catch (SQLException e){
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
    }
}
