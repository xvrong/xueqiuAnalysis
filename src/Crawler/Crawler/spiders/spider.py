import json
import pandas as pd
import scrapy
import time

cookies = {
    'device_id': '8b5e9eefe0302a4ae8faa062d3b034de',
    'xq_a_token': '28ed0fb1c0734b3e85f9e93b8478033dbc11c856',
    'xqat': '28ed0fb1c0734b3e85f9e93b8478033dbc11c856',
    'xq_r_token': 'bf8193ec3b71dee51579211fc4994d03f17c64ac',
    'xq_id_token': 'eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTY2MzExMzIyMSwiY3RtIjoxNjYwNzE0ODQ0MjM1LCJjaWQiOiJkOWQwbjRBWnVwIn0.K98mK_7se-OB9lgTW9CoGRgYIMS6vghQIiPOrna9Jo3vcFevVYxoY5RregZWdzEMBUSXEmCqXbL_4lJ6FoQMdhYKcP1Uz7Hvv22ZfTO2bn_Vs9G8QA5OhkqbBsqdopwMk98Ytp0jkaTJzl9WTcnX_AlLuLIH8serFStkd3t76DwMq1bSJZtJsF-eZcyAKWZ_7y94NoJx85JrVZ5nADapLyLLEatmdJLQncrZkcoac130aAEFNr1cSg-hBPU1HLxitqYbwjrgLKxWGkJAgHcxkwwlABm2MM2qRpSgcFEMDG0w23WWGr1996fsYXfRByzIvvm1fQSBLemyirkRQENt5g',
    'u': '381660526943697',
    's': 'bw12bqi4gl',
    'is_overseas': '0',
}

# 浏览器用户代理
headers = {
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.81 Safari/537.36 Edg/104.0.1293.54'
}


class Spider_two(scrapy.Spider):
    name = "和迈股份"

    start_urls = [
        f'https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH688032&begin={time.time() * 1000}&period=60m&type=before&count=-1&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance',
    ]

    def start_requests(self):
        # 浏览器用户代理
        headers = {
            'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.81 Safari/537.36 Edg/104.0.1293.54'
        }
        urls = [
            f'https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH688032&begin={int(time.time() * 1000)}&period=60m&type=before&count=-1&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance'
        ]

        for url in urls:
            yield scrapy.Request(url=url, headers=headers, cookies=cookies, callback=self.parse)

    def parse(self, response):
        # 存储返回的json数据
        js = json.loads(response.text)
        data = pd.DataFrame(data=js['data']['item'],
                            columns=js['data']['column'])
        data.to_csv(f'../../data/{self.name}.csv',
                    index=False, mode='a', header=False)
        self.logger.warning('爬取完成')


class Spider_four(scrapy.Spider):
    name = "思瑞浦"

    def start_requests(self):
        # 浏览器用户代理
        headers = {
            'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.81 Safari/537.36 Edg/104.0.1293.54'
        }
        # 指定cookies
        urls = [
            f'https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH688032&begin={int(time.time() * 1000)}&period=60m&type=before&count=-1&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance'
        ]

        for url in urls:
            yield scrapy.Request(url=url, headers=headers, cookies=cookies, callback=self.parse)

    def parse(self, response):
        # 存储返回的json数据
        js = json.loads(response.text)
        data = pd.DataFrame(data=js['data']['item'],
                            columns=js['data']['column'])
        data.to_csv(f'../../data/{self.name}.csv',
                    index=False, mode='a', header=False)
        self.logger.warning('爬取完成')


class Spider_three(scrapy.Spider):
    name = "历史数据"

    def start_requests(self):
        # 爬虫url
        urls = [
            'https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH600519&begin=998841600000&end=166073=1601222400000&end=1660735316603&4275748&period=day&type=before&indicator=kline',
            # 贵州茅台
            'https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH601398&begin=1161878400000&end=1660734100154&period=day&type=before&indicator=kline',
            # 工商银行
            'https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SZ300896&beginperiod=day&type=before&indicator=kline',
            # 爱美客
            'https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH603290&begin=1580745600000&end=1660735383635&period=day&type=before&indicator=kline',
            # 斯达半导
            'https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH688536&begin=1600617600000&end=1660719104145&period=day&type=before&indicator=kline',
            # 思瑞浦
            'https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH688032&begin=1639929600000&end=1660719894159&period=day&type=before&indicator=kline',
            # 和迈股份
            'https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH688348&begin=1654617600000&end=1660735115456&period=day&type=before&indicator=kline',
            # 昱能科技
            'https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH688200&begin=1581955200000&end=1660735778064&period=day&type=before&indicator=kline',
            # 华峰测控

        ]
        full_name = ['贵州茅台', '工商银行', '爱美客', '斯达半导', '思瑞浦', '禾迈股份', '昱能科技',
                     '华峰测控']

        short_name = ['gzmt', 'gsyh', 'amk', 'sdbd', 'srp', 'hmgf', 'ynkj',
                      'hfck']

        def get_parse(name):
            def parse(response):
                js = json.loads(response.text)
                data = pd.DataFrame(data=js['data']['item'],
                                    columns=js['data']['column'])
                data.to_csv(f'../../data/{name}.csv', index=False)

            return parse

        for i in range(len(urls)):
            url = urls[i]
            print(url)

            # def parse(response):
            #     name = short_name[i]
            #     print(name)
            #     js = json.loads(response.text)
            #     data = pd.DataFrame(data=js['data']['item'],
            #                         columns=js['data']['column'])
            #     data.to_csv(f'../../data/{name}.csv', index=False)

            yield scrapy.Request(url=url, headers=headers, cookies=cookies, callback=get_parse(short_name[i]))
