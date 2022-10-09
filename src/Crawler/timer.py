# from scrapy import cmdline
import os
import sys
import time

if __name__ == '__main__':
    sys.path.append(os.path.dirname(os.path.abspath(__file__)))
    while True:
        # cmdline.execute(f'scrapy crawl {name}'.split())
        os.system(f'scrapy crawl 和迈股份')
        os.system(f'scrapy crawl 思瑞浦')
        # 打印爬取时间
        time.sleep(3600)
