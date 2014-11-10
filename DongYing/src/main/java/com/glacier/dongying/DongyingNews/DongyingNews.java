package com.glacier.dongying.DongyingNews;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by glacier on 14-11-10.
 */
public class DongyingNews {
    public static void main(String[] agrs) {
        try {
            Document document = Jsoup.connect("http://news.dongyingnews.cn/system/2014/11/10/010485770.shtml")

                    .get();
            System.out.println(document.toString());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
