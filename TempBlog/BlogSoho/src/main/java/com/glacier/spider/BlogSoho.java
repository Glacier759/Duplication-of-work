package com.glacier.spider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

    public void start(String url) {
        try {
            Document document = document(url);

            Element directory = document.getElementById("blog-directory");
            Elements list = directory.select("li").select("a[href]");

            for ( Element ele:list ) {
                String link = ele.attr("abs:href");
                Document doc = document(link);
                String script = doc.select("script").first().toString();
                int index = script.indexOf("_ebi = '") + 8;
                String _ebi = script.substring(index, index + 10 );
                String articleLink = link + "sff/entries/" + _ebi + ".html";
                doc = document(articleLink);
                Elements elements = doc.select("a[class=check-all]");
                for ( Element element:elements ) {
                    articleLink = element.attr("abs:href");
                    getContent(articleLink);
                }
                break;
                //String _ebi = doc.toString().substring()
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getContent(String url) {
        try {
            Document document = document(url);

            String title = document.select("h2").text();
            String date = document.select("span[class=date]").text();
            String content = "";

            Elements contentEles = document.getElementById("main-content").select("p");
            for ( Element contentEle:contentEles ) {
                content += contentEle.text() + "\n";
            }

            System.out.println(title);
            System.out.println(date);
            System.out.println(content);
            System.exit(1);

        }catch (Exception e) {
            e.printStackTrace();
        }
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
