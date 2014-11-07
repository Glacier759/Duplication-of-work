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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by glacier on 14-11-7.
 */
public class ShengliPageProcessor implements PageProcessor {

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
        Elements blockEles = document.select("div[class=bm bmw  flg cl]");
        List<String> urlist = new ArrayList<String>();
        for ( Element blockEle:blockEles ) {
            Elements dtEles = blockEles.select("dt");
            for ( Element dtEle:dtEles ) {
                Element block = dtEle.select("a[href]").first();
                String url = block.attr("abs:href");
                if ( !urlist.contains(url) ) {
                    urlist.add(url);
                    pageSpider.addUrl(url);
                }
            }
        }
        pageSpider.run();
    }

    @Override
    public Site getSite() {
        return site;
    }
}
