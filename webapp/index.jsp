<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>雪球网股票数据展示</title>
    <!-- 引入 echarts.js -->
    <script src="script/echarts.min.js"></script>

    <!-- 引入 jquery.js -->
    <script src="script/jquery-1.8.3.min.js"></script>
    <style>
        button {
            display: block;
            width: 200px;
            height: 50px;
            line-height: 50px;
            text-align: center;
            border: 1px solid #ccc;
            text-decoration: none;
            color: white;
            background-color: #55585a;
        }

        button:hover {
            background-color: #ff6700;
        }

        button:focus {
            background-color: #58C9B9;
        }
    </style>
</head>

<body>
<%--<h3>雪球网数据展示</h3>--%>
<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
<div id="main" style="width: 1640px; height: 720px;">
    <div id="topbar" style="width: 1640px; height: 70px;">
        <%--        如果session里有user，则显示用户名，否则显示登录按钮--%>
        <% if (session.getAttribute("username") != null) { %>
        <form method="post" action="logout" style="float:left">
            <input type="hidden" name="name" value="value"/>
            <button>登出</button>
        </form>
        <%--        如果acl = 0, 则添加成为超级会员按钮！！！--%>
        <% if ((int) session.getAttribute("acl") == 0) { %>
        <form method="post" action="getPriority" style="float:left">
            <input type="hidden" name="name" value="value"/>
            <button>渴望力量！！！</button>
        </form>
        <%--            如果是1，则添加成为普通会员按钮！！！--%>
        <% } else if ((int) session.getAttribute("acl") == 1) { %>
        <form method="post" action="removePriority" style="float:left">
            <input type="hidden" name="name" value="value"/>
            <button>退钱！！！</button>
        </form>
        <% } %>
        <% } else { %>
        <%--登录注册链接--%>
        <a href="./loginpage.jsp" style="float:left">
            <button>登录</button>
        </a>
        <a href="./registerpage.jsp" style="float:left">
            <button>注册</button>
        </a>
        <% } %>
        <div id="userprofile" Style="float:right">
            <% if (session.getAttribute("username") != null) { %>
            <h2>用户：<%=session.getAttribute("username")%>
            </h2>
            <% } %>
        </div>
    </div>

    <div id="sidebar" style="width: 200px; height: 720px; float:left">
        <button type="submit" id="gzmt">贵州茅台</button>
        <button type="submit" id="gsyh">工商银行</button>
        <button type="submit" id="amk">爱美客</button>
        <button type="submit" id="sdbd">斯达半导</button>
        <button type="submit" id="srp">思瑞浦</button>
        <button type="submit" id="hmgf">禾迈股份</button>
        <button type="submit" id="ynkj">昱能科技</button>
        <button type="submit" id="hfck">华峰测控</button>
    </div>
    <div id="kchart" style="width: 1440px; height: 720px; float: left"></div>
</div>

<script type="text/javascript">

    const upColor = '#ec0000';
    const upBorderColor = '#8A0000';
    const downColor = '#00da3c';
    const downBorderColor = '#008F28';


    $(function () {
        $('#gzmt').bind('click', function () {
            clickbtn('贵州茅台', 'gzmt', 'hive');
        });

        $('#gsyh').bind('click', function () {
            clickbtn('工商银行', 'gsyh', 'hive');
        });

        $('#amk').bind('click', function () {
            clickbtn('爱美客', 'amk', 'hive');
        });

        $('#sdbd').bind('click', function () {
            clickbtn('斯达半导', 'sdbd', 'hive');
        });

        $('#srp').bind('click', function () {
            clickbtn('思瑞浦', 'srp', 'spark');
        });

        $('#hmgf').bind('click', function () {
            clickbtn('禾迈股份', 'hmgf', 'spark');
        });

        $('#ynkj').bind('click', function () {
            clickbtn('昱能科技', 'ynkj', 'spark');
        });

        $('#hfck').bind('click', function () {
            clickbtn('华峰测控', 'hfck', 'spark');
        });

        function clickbtn(name, short_name, type) {
            if (type === "hive") {
                $.getJSON("TaskServlet?task=1&tblName=" + short_name, function (ori_data) {
                    let data = splitHiveData(ori_data);
                    draw_k(name, data);
                })
            } else if (type === "spark") {
                $.getJSON("TaskServlet?task=2&tblName=" + short_name, function (ori_data) {
                    let data = splitSparkData(ori_data);
                    draw_k(name, data);
                })
            }
        }

        function splitHiveData(ori_data) {
            let categoryData = [];
            let values = [];

            for (let i = 0, len = ori_data.length; i < len; i++) {
                categoryData.push(ori_data[i].key);
                values.push(ori_data[i].value);
            }

            return {
                categoryData: categoryData,
                values: values
            };
        }

        function splitSparkData(ori_data) {
            let categoryData = [];
            let values = [];

            for (let i = 0, len = ori_data.length; i < len; i++) {
                categoryData.push(ori_data[i].date);
                values.push([ori_data[i].open, ori_data[i].close, ori_data[i].low, ori_data[i].high]);
            }

            return {
                categoryData: categoryData,
                values: values
            };
        }

        function draw_k(name, data0) {
            // Each item: open，close，lowest，highest
            let myChart = echarts.init(document.getElementById("kchart"));

            function calculateMA(dayCount) {
                const result = [];

                let i = 0, len = data0.values.length;
                for (; i < len; i++) {
                    if (i < dayCount) {
                        result.push('-');
                        continue;
                    }
                    let sum = 0;
                    for (let j = 0; j < dayCount; j++) {
                        sum += +data0.values[i - j][1];
                    }
                    result.push(sum / dayCount);
                }
                return result;
            }

            // 画图
            let option = {
                title: {
                    text: name,
                    left: 0
                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross'
                    }
                },
                legend: {
                    data: ['日K', 'MA5', 'MA10', 'MA20', 'MA30']
                },
                grid: {
                    left: '10%',
                    right: '10%',
                    bottom: '15%'
                },
                xAxis: {
                    type: 'category',
                    data: data0.categoryData,
                    boundaryGap: false,
                    axisLine: {onZero: false},
                    splitLine: {show: false},
                    min: 'dataMin',
                    max: 'dataMax'
                },
                yAxis: {
                    scale: true,
                    splitArea: {
                        show: true
                    }
                },
                dataZoom: [
                    {
                        type: 'inside',
                        start: 50,
                        end: 100
                    },
                    {
                        show: true,
                        type: 'slider',
                        top: '90%',
                        start: 50,
                        end: 100
                    }
                ],
                series: [
                    {
                        name: '日K',
                        type: 'candlestick',
                        data: data0.values,
                        itemStyle: {
                            color: upColor,
                            color0: downColor,
                            borderColor: upBorderColor,
                            borderColor0: downBorderColor
                        },
                        markPoint: {
                            label: {
                                formatter: function (param) {
                                    return param != null ? Math.round(param.value) + '' : '';
                                }
                            },
                            data: [
                                // {
                                //     name: 'Mark',
                                //     coord: ['2021/07/08', 716.831],
                                //     value: 716.831,
                                //     itemStyle: {
                                //         color: 'rgb(41,60,85)'
                                //     }
                                // },
                                {
                                    name: 'highest value',
                                    type: 'max',
                                    valueDim: 'highest'
                                },
                                {
                                    name: 'lowest value',
                                    type: 'min',
                                    valueDim: 'lowest'
                                },
                                {
                                    name: 'average value on close',
                                    type: 'average',
                                    valueDim: 'close'
                                }
                            ],
                            tooltip: {
                                formatter: function (param) {
                                    return param.name + '<br>' + (param.data.coord || '');
                                }
                            }
                        },
                        markLine: {
                            symbol: ['none', 'none'],
                            data: [
                                [
                                    {
                                        name: 'from lowest to highest',
                                        type: 'min',
                                        valueDim: 'lowest',
                                        symbol: 'circle',
                                        symbolSize: 10,
                                        label: {
                                            show: false
                                        },
                                        emphasis: {
                                            label: {
                                                show: false
                                            }
                                        }
                                    },
                                    {
                                        type: 'max',
                                        valueDim: 'highest',
                                        symbol: 'circle',
                                        symbolSize: 10,
                                        label: {
                                            show: false
                                        },
                                        emphasis: {
                                            label: {
                                                show: false
                                            }
                                        }
                                    }
                                ],
                                {
                                    name: 'min line on close',
                                    type: 'min',
                                    valueDim: 'close'
                                },
                                {
                                    name: 'max line on close',
                                    type: 'max',
                                    valueDim: 'close'
                                }
                            ]
                        }
                    },
                    {
                        name: 'MA5',
                        type: 'line',
                        data: calculateMA(5),
                        smooth: true,
                        lineStyle: {
                            opacity: 0.5
                        }
                    },
                    {
                        name: 'MA10',
                        type: 'line',
                        data: calculateMA(10),
                        smooth: true,
                        lineStyle: {
                            opacity: 0.5
                        }
                    },
                    {
                        name: 'MA20',
                        type: 'line',
                        data: calculateMA(20),
                        smooth: true,
                        lineStyle: {
                            opacity: 0.5
                        }
                    },
                    {
                        name: 'MA30',
                        type: 'line',
                        data: calculateMA(30),
                        smooth: true,
                        lineStyle: {
                            opacity: 0.5
                        }
                    }
                ]
            };

            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);
        }

    });
</script>
</body>

</html>