import os
from scrapy import cmdline

if __name__ == '__main__':
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    name = '历史数据'
    cmdline.execute(f'scrapy crawl {name}'.split())
