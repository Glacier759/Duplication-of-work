package com.glacier.dongying.ShengliShequ;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.BloomFilterDuplicateRemover;

import java.util.List;

/**
 * Created by glacier on 14-11-7.
 */
public class BBSPageProcessor implements PageProcessor {

    Site site = Site.me().setSleepTime(1000).setRetryTimes(3);
    Spider pageSpider = Spider.create(new GetBBSContent());
    {
        pageSpider.setScheduler(new QueueScheduler()
                .setDuplicateRemover(new BloomFilterDuplicateRemover(100000000)));
        pageSpider.thread(10);
    }

    @Override
    public void process(Page page) {
        Document document = page.getHtml().getDocument();
        Elements elements = document.select("a[class=s xst]");
        for ( Element element:elements ) {
            pageSpider.addUrl(element.attr("abs:href"));
        }
        pageSpider.run();

        String nexturl = page.getHtml().$("a.nxt").links().get();
        if ( nexturl != null )
            page.addTargetRequest(nexturl);
    }

    @Override
    public Site getSite() {
        return site;
    }
}