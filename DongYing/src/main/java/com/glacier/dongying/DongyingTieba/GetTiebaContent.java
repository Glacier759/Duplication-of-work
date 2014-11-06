package com.glacier.dongying.DongyingTieba;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by glacier on 14-11-6.
 */
public class GetTiebaContent implements PageProcessor {
    Site site = Site.me().setSleepTime(1000).setRetryTimes(3)
            .addCookie("tieba.baidu.com", "TIEBAUID", "52dc7439f3e72559bb48a912")
            .addCookie("tieba.baidu.com", "TIEBA_USERTYPE", "fcf922b15b80e609daefdb56")
            .addCookie("tieba.baidu.com", "bdshare_firstime", "1413434482309")
            .addCookie("tieba.baidu.com", "dasense_show_10172", "1")
            .addCookie("tieba.baidu.com", "dasense_show_10495", "1")
            .addCookie("tieba.baidu.com", "fuwu_center_bubble", "1")
            .addCookie("tieba.baidu.com", "rpln_guide", "1")
            .addCookie("tieba.baidu.com", "wanleTipCircle", "1415277415883")
            .addCookie("tieba.baidu.com", "zt2meizhi", "");
    @Override
    public void process(Page page) {
        String url = page.getUrl().toString();
        String content = getContent(url);
        content = "<url>" + url + "</url>\n" + content;
        try {
            File dir = new File("Data", "Tieba");
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

    public String getContent( String url ) {
        try {
            Map<String, String> cookieMap = new HashMap<String, String>();
            cookieMap.put("TIEBAUID", "52dc7439f3e72559bb48a912");
            cookieMap.put("TIEBA_USERTYPE", "fcf922b15b80e609daefdb56");
            cookieMap.put("bdshare_firstime", "1413434482309");
            cookieMap.put("dasense_show_10172", "1");
            cookieMap.put("dasense_show_10495", "1");
            cookieMap.put("fuwu_center_bubble", "1");
            cookieMap.put("rpln_guide", "1");
            cookieMap.put("wanleTipCircle", "1415277415883");
            cookieMap.put("zt2meizhi", "");
            Document document = Jsoup.connect(url).cookies(cookieMap).get();
            //Elements contentEles = document.select("div[class=d_post_content j_d_post_content]");
            Elements contentEles = document.select("cc");
            String content = "";
            for (Element contentEle : contentEles) {
                String eleText = contentEle.text();
                if ( eleText.length() == 0 )
                    continue;
                content += "<content>" + eleText + "</content>\n";
            }
            Elements pageURLs = document.select("li[class=l_pager pager_theme_5 pb_list_pager]").select("a[href]");
            for (Element pageURL : pageURLs) {
                String eleText = pageURL.text();
                if (eleText.contains("下一页")) {
                    String nextURL = pageURL.attr("abs:href");
                    content += getContent(nextURL);
                    break;
                }
            }
            return content;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
