package com.glacier.dongying.DongyingBa;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.BloomFilterDuplicateRemover;


/**
 * Created by glacier on 14-11-8.
 */
public class DongyingPageProcessor implements PageProcessor {

    Site site = Site.me().setSleepTime(1000).setRetryTimes(3);
    Spider pageSpider = Spider.create(new BBSPageProcessor());
    {
        pageSpider.setScheduler(new QueueScheduler()
                .setDuplicateRemover(new BloomFilterDuplicateRemover(100000000)));
        pageSpider.thread(10);
    }

    @Override
    public void process(Page page) {
        Document document = page.getHtml().getDocument();
        Elements blockEles = document.select("div[class=fl_icn_g]");
        for ( Element blockEle:blockEles ) {
            String url = blockEle.select("a[href]").attr("href");
            pageSpider.addUrl(url);
        }
        pageSpider.run();
    }

    @Override
    public Site getSite() {
        return site;
    }
}
