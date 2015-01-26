package com.glacier.spider;

import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA on 2015-01-24 22:00.
 * Author:  Glacier (RenLixiang), OurHom.759@gmail.com
 * Company: Class 1204 of Computer Science and Technology
 */
public class BlogSoho {
    public static void main(String[] args) {
        BlogSoho obj = new BlogSoho();
        obj.start("http://blogz.sohu.com/stock/s2014/s178601.shtml");
    }

    private String title, date, content, source;

    public void start(String url) {
        try {
            Document document = document(url);

            Element directory = document.getElementById("blog-directory");
            Elements list = directory.select("li").select("a[href]");

            int count = 1;
            for ( Element ele:list ) {
                try {
                    System.out.println("[人物] - " + count + "/" + list.size());
                    String link = ele.attr("abs:href");
                    Document doc = document(link + "entry/");
                    String script = doc.select("script").toString();
                    int index = script.indexOf("_ebi = '") + 8;
                    String _ebi = script.substring(index, index + 10);
                    Pattern p = Pattern.compile("var totalCount = (.*);");
                    Matcher m = p.matcher(doc.toString());
                    double totalCount = 0;
                    if (m.find()) {
                        totalCount = Integer.parseInt(m.group(1));
                    }
                    int pageCount = (int) Math.ceil(totalCount / 20);

                    int num = 1;
                    for (int i = 1; i <= pageCount; i++) {
                        try {
                            String articleLink = link + "action/v_frag-ebi_" + _ebi + "-pg_" + i + "/entry/";

                            System.out.println(articleLink);
                            doc = document(articleLink);
                            Elements elements = doc.select("a[class=list-title]");
                            for (Element element : elements) {
                                try {
                                    System.out.println("[博客] - " + num + "/" + (int) totalCount);
                                    String blogLink = element.attr("abs:href");
                                    getContent(blogLink);
                                    num++;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getContent(String url) {
        try {
            Document document = document(url);

            source = url;
            title = document.select("h2").text();
            date = document.select("span[class=date]").text();
            content = "";

            Elements contentEles = document.getElementById("main-content").select("p");
            for ( Element contentEle:contentEles ) {
                content += contentEle.text() + "\n";
            }

            saveXML();
//            System.exit(1);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveXML() {
        try {
            org.dom4j.Document document = DocumentHelper.createDocument();
            org.dom4j.Element rootEle = document.addElement("root");

            org.dom4j.Element titleEle = rootEle.addElement("title");
            titleEle.addText(title);

            org.dom4j.Element sourceEle = rootEle.addElement("source");
            sourceEle.addText(source);

            org.dom4j.Element publishdateEle = rootEle.addElement("publishdate");
            publishdateEle.addText(date);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            org.dom4j.Element crawldateEle = rootEle.addElement("crawldate");
            crawldateEle.addText(format.format(new Date()));

            org.dom4j.Element contentEle = rootEle.addElement("content");
            contentEle.addText(content);

            String xml = formatXML(rootEle);
            if ( xml != null ) {
                FileUtils.writeStringToFile(new File(new File(new File("Data"), "BlogSoho"), System.currentTimeMillis()+xml.hashCode()+".xml"), xml);
                System.out.println("[成功] - " + source);
            }
            else
                System.out.println("[异常] - " + source);


  //          System.out.println(xml);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatXML(org.dom4j.Element root) {
        String formatXMLStr = null;
        try {
            SAXReader saxReader = new SAXReader();
            org.dom4j.Document document = saxReader.read(new ByteArrayInputStream(root.asXML().getBytes()));
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            StringWriter writer = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(writer, format);
            xmlWriter.write(document);
            formatXMLStr = writer.toString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return formatXMLStr;
    }

    private Document document(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url)
                    .timeout(50000)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36")
                    .get();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }
}
