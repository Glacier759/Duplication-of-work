Duplication-of-work
===================
##2014-08-06	SpiderPaper

>抓取中国搜索报刊中全国性报刊

>获取电子版报刊中的正文内容

>采用统计的抽取算法进行正文抽取(效果不佳弃用)

>只过滤P标签的到文本内容

>结果组织为XML格式

>IDEA+Maven开发

###XML组织格式

>'title'  文章标题

>'source'  文章来源URL

>'newspaper'  报纸名称

>'page'  版面

>'publishtime'  文章发表时间

>'crawltime'  文章抓取时间

>'language'  中文

>'encode'  编码格式(要求为utf-8)

>'body'  文章内容

###增加配置文件,网址按照对应的解析方式进行解析，各类别网址解析 方式待增加

>RenminClass,	人民日报类解析方式

