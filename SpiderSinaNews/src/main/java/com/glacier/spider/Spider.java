package com.glacier.spider;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA on 2015-01-12 19:58.
 * Author:  Glacier (RenLixiang), OurHom.759@gmail.com
 * Company: Class 1204 of Computer Science and Technology
 */
public class Spider {

    private static HashSet<String> hashSet = new HashSet<String>();
    private static List<String> dateList = new ArrayList<String>();
    static {
        int count = 0;
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        do {
            calendar.setTime(date);
            String date1 = format1.format(date);
            String date2 = format2.format(date);
            dateList.add(date1);
            dateList.add(date2);
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
            date = calendar.getTime();
            count ++;
            if (count >= 10) {
                break;
            }
        }while(true);
    }

    public static void main(String[] args) {
        Spider spider = new Spider();
        spider.start();
    }

    public void start() {
        Document document = document("http://www.sina.com.cn");
        Elements elements = document.select("div[class=main-nav]").select("li").select("a[href]");

        String[] filter = {"科技", "财经", "体育", "健康", "教育", "汽车", "娱乐"};
        List<String> filterList = Arrays.asList(filter);
        for (Element element:elements) {
            try {
                String text = element.text();
                if (filterList.contains(text)) {
                    next(element, new HashSet<String>());
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void next(Element root, HashSet<String> nextSet) {
        System.out.println(root.text());
        String type = root.text();
        Document document = document(root.attr("abs:href"));
        Elements elements = document.select("a[href$=.shtml]");
        for ( Element element:elements ) {
            try {
                String href = element.attr("abs:href");
                if (!hashSet.contains(href) && href.contains(document.baseUri()) && hasDate(href)) {
                    //System.out.println(element);
                    hashSet.add(href);
                    nextSet.add(href);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(nextSet.size());
        get(nextSet, type, 1, 3);
        System.out.println("搞定 " + type);
    }

    public void get(HashSet<String> urlSet, String type, int now, int target) {
        String host = null;
        HashSet newSet = new HashSet();
        for ( Iterator itor = urlSet.iterator(); itor.hasNext(); ) {
            String url = (String)itor.next();
            try {
                Document document = document(url);
                host = new URL(url).getHost();
                if (now < target) {
                    Elements elements = document.select("a[href$=.shtml]");
                    for (Element element : elements) {
                        try {
                            String href = element.attr("abs:href");
                            if (!hashSet.contains(href) && href.contains(host) && hasDate(href)) {
                                hashSet.add(href);
                                newSet.add(href);
                                System.out.println("get new set - " + newSet.size());
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }   //把新的地址加入队列

                //提取正文内容
                try {
                    Element titleEle = document.getElementById("artibodyTitle");
                    String title = titleEle.text();
                    Element body = document.getElementById("artibody");
                    Elements pTags = body.select("p");
                    String content = "";
                    for (Element pTag : pTags) {
                        content += pTag.text();
                    }
                    content = content.replaceAll("　　", "");
                    String save = title + "：\t" + content;
                    System.out.println(title + "\t" + content);

                    FileUtils.writeStringToFile(new File("Data", type + ".txt"), save + "\n", true);
                } catch (Exception e) {
                    System.out.println(document.baseUri());
                    e.printStackTrace();
                }

                itor.remove();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("newSet = " + newSet.size());
        if ( now < target ) {
            System.out.println("深度增加");
            get(newSet, type, now + 1, target);
        }
    }

    public static Document document(String url) {

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

    private boolean hasDate(String href) {
        for ( String date:dateList ) {
            if ( href.contains(date) ) {
                return true;
            }
        }
        return false;
    }
}
