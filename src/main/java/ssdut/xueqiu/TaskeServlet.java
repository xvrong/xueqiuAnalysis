package ssdut.xueqiu;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * http://localhost:8080/bigdataviz001/index.jsp
 * Servlet 访问以下/CountServlet请求，得到以下结果
 * {"mylegend":"识别标签","mytitle":"统计标题","prolist":{"qq1":66,"qq3":86,"qq2":56}}
 */
@WebServlet("/TaskServlet")
public class TaskeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TaskeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // TODO Auto-generated method stub
        //response.getWriter().append("Served at: ").append(request.getContextPath());

        //post乱码处理
        request.setCharacterEncoding("utf-8");
        // 设置响应数据类型
        response.setContentType("text/html");
        // 设置响应编码格式
        response.setCharacterEncoding("utf-8");

        //组织json数据
        try {
            int taskType = Integer.parseInt(request.getParameter("task"));
            switch (taskType) {
                case 1:
                    TaskOne.main(request, response);
                    break;
                case 2:
                    TaskTwo.main(request, response);
                    break;
                case 3:
                    TaskThree.main(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取数据出错");
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
