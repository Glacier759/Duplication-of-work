package com.glacier.dongying.DongyingBBS;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.BloomFilterDuplicateRemover;

import java.util.List;

/**
 * Created by glacier on 14-11-4.
 */

//http://club.dzwww.com/forum-223-1.html
public class BBSPageProcessor implements PageProcessor {

    Site site = Site.me().setSleepTime(1000).setRetryTimes(3);
    Spider pageSpider = Spider.create(new GetBBSContent());
    {
        pageSpider.setScheduler(new QueueScheduler()
        .setDuplicateRemover(new BloomFilterDuplicateRemover(100000000)));
        pageSpider.thread(1);
    }

    @Override
    public void process(Page page) {
        List<String> urlist = page.getHtml().$("a.xst").links().all();
        for ( String urline : urlist ) {
            pageSpider.addUrl(urline);
        }
        pageSpider.run();

        String nexturl = page.getHtml().$("a.nxt").links().get();
        page.addTargetRequest(nexturl);
    }

    @Override
    public Site getSite() {
        return site;
    }
}
