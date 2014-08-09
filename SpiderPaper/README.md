Duplication-of-work
===================
##2014-08-06	SpiderPaper

>抓取中国搜索报刊中全国性报刊

>获取电子版报刊中的正文内容

>采用统计的抽取算法进行正文抽取(效果不佳弃用)

>只过滤P标签的到文本内容

>结果组织为XML格式

>引入的jar文件有

>jsoup-1.7.2.jar

>commons-io-2.4.jar

>xstream-1.4.7.jar

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
