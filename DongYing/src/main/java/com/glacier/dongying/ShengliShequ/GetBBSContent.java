package com.glacier.dongying.ShengliShequ;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.File;

/**
 * Created by glacier on 14-11-7.
 */
public class GetBBSContent implements PageProcessor {

    Site site = Site.me().setSleepTime(1000).setRetryTimes(3);

    @Override
    public void process(Page page) {
        String content = "<url>" + page.getUrl() + "</url>\n";
        content += "<title>" + page.getHtml().getDocument().getElementById("thread_subject").text() + "</title>\n";

        content += getContent(page.getUrl().toString());
        System.out.println(content);

        try {
            File dir = new File("Data", "ShengliShequ");
            String fileName = System.currentTimeMillis()+""+(Math.abs(content.hashCode()))+".xml";
            File file = new File(dir, fileName);
            FileUtils.writeStringToFile(file, content);
            System.out.println(page.getUrl()+"\tsaved "+fileName);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public String getContent(String url) {
        try {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(30000)
                    .get();
            Elements contentEles = document.select("div[class=t_fsz]");
            String content = "";
            for ( Element contentEle:contentEles ) {
                content += contentEle.toString() + "\n";
            }
            Element nextEle = document.select("a[class=nxt]").first();
            if ( nextEle != null ) {
                String nextURL = nextEle.attr("abs:href");
                content += getContent(nextURL);
            }
            return content;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
