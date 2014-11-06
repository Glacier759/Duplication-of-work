package com.glacier.dongying.DongyingTieba;

import com.glacier.dongying.DongyingBBS.GetBBSContent;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.BloomFilterDuplicateRemover;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by glacier on 14-11-6.
 */
public class TiebaPageProcessor implements PageProcessor {
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
    Spider pageSpider = Spider.create(new GetTiebaContent());
    {
        pageSpider.setScheduler(new QueueScheduler()
                .setDuplicateRemover(new BloomFilterDuplicateRemover(100000000)));
        pageSpider.thread(10);
    }

    @Override
    public void process(Page page) {
        Document document = page.getHtml().getDocument();
        Elements elements = document.getElementById("contet_wrap").select("a[title]");
        List<String> urlist = new ArrayList<String>();
        for ( Element element:elements ) {
            String link = element.attr("href");
            if ( link.contains("/p/") ) {
                urlist.add(link);
            }
        }
        for ( String link:urlist ) {
            pageSpider.addUrl(link);
        }
        pageSpider.run();
        Element nextPage = document.select("a[class=next]").first();
        if ( nextPage != null )
            page.addTargetRequest(nextPage.attr("href"));
    }

    @Override
    public Site getSite() {
        return site;
    }
}
