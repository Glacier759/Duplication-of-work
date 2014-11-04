package com.glacier.dongying.Main;

import com.glacier.dongying.DongyingBBS.BBSPageProcessor;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.BloomFilterDuplicateRemover;

/**
 * Created by glacier on 14-11-4.
 */
public class Main {
    public static void main(String[] args) {
        Spider jobSpider = Spider.create(new BBSPageProcessor());
        jobSpider.setScheduler(new QueueScheduler()
        .setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)));
        jobSpider.addUrl("http://club.dzwww.com/forum-223-1.html");
        jobSpider.thread(10);
        jobSpider.run();
    }
}
