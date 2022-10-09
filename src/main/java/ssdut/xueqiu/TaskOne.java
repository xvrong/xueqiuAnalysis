package ssdut.xueqiu;

import com.alibaba.fastjson.JSON;
import javafx.util.Pair;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;


public class TaskOne {

    public static Map<String, List<Pair<String, List<Double>>>> result = new HashMap<>();

    // 程序入口main函数
    public static void main(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tblName = request.getParameter("tblName");
        System.out.printf("%s analysis start%n", tblName);
        long startTime = System.currentTimeMillis();

        // 获取数据
        if (result.get(tblName) == null) {
            System.out.println("hive start");

            String drv = "org.apache.hive.jdbc.HiveDriver";                //Hive驱动名称
            String url = "jdbc:hive2://192.168.17.10:10000/xv_rong";        //默认端口号10000
            String usr = "hive";
            String pwd = "123456";
            Class.forName(drv);
            Connection conn = DriverManager.getConnection(url, usr, pwd);
            Statement stmt = conn.createStatement();
            ResultSet rs;

            //创建表
            String sql = "CREATE TABLE IF NOT EXISTS " + tblName + " ( " +
                    "timestamps TIMESTAMP, " +
                    "volume int, " +
                    "opens double, " +
                    "high double, " +
                    "low double, " +
                    "closes double, " +
                    "chg double, " +
                    "percents double, " +
                    "turnoverrate double, " +
                    "amount double, " +
                    "volume_post double, " +
                    "amount_post double " +
                    ") " +
                    "ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde' " +
                    "TBLPROPERTIES ('skip.header.line.count'='1')";

            stmt.executeUpdate(sql);
            System.out.println("create table over");

            // 判断表中是否有数据
            sql = String.format("SELECT * FROM %s limit 1", tblName);
            rs = stmt.executeQuery(sql);

            if (!rs.next()) {
                //载入数据
                String localFile = String.format("/data/%s.csv", tblName);
                sql = String.format("LOAD DATA INPATH '%s' INTO TABLE %s", localFile, tblName);
                stmt.executeUpdate(sql);
            }

            System.out.println("load data over");

            sql = String.format("select timestamps, opens, closes, low, high from %s", tblName);

            rs = stmt.executeQuery(sql);

            //定义保存统计数据结果的map集合
            ArrayList<Pair<String, List<Double>>> resultMap = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");//要转换的时间格式
            while (rs.next()) {
                long timeLong = rs.getLong(1);
                String date = sdf.format(new Date(timeLong));
                resultMap.add(new Pair<>(date, Arrays.asList(rs.getDouble(2), rs.getDouble(3), rs.getDouble(4), rs.getDouble(5))));
            }

            // 按照timestamps升序排列
            resultMap.sort(Comparator.comparing(Pair::getKey));

            stmt.close();
            conn.close();

            result.put(tblName, resultMap);
        } else {
            System.out.println("data already exists");
        }

        int isPriority = 0;

        if (request.getSession().getAttribute("username") != null) {
            isPriority = (int) request.getSession().getAttribute("acl");
        }

        List<Pair<String, List<Double>>> resultMap;
        if (isPriority == 1) {
            System.out.println("is priorty");
            resultMap = result.get(tblName);
        } else {
            System.out.println("not priorty");
            // 非会员只能看30条数据
            resultMap = result.get(tblName).subList(result.get(tblName).size() - 30, result.get(tblName).size());
        }

        //通过构建map集合转换为嵌套json格式数据
        String str = JSON.toJSONString(resultMap);

        PrintWriter out = response.getWriter();
//        System.out.println(str);
        out.print(str);
        out.flush();
        out.close();

        long endTime = System.currentTimeMillis();
        long usedTime = (endTime - startTime) / 1000;
        System.out.printf("%s analysis end, used time: %d seconds%n", tblName, usedTime);
    }
}