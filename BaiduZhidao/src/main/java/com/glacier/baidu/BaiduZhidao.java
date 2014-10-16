package com.glacier.baidu;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.BloomFilterDuplicateRemover;

/**
 * Created by glacier on 14-10-16.
 */
public class BaiduZhidao {
    public static int count = 0;
    public static void main(String[] args) {

        String seedURL = "http://zhidao.baidu.com/browse/74";
        String saveDir = "Internet";
        if ( args.length >= 1 )
            seedURL = args[0];
        if ( args.length == 2 )
            saveDir = args[1];

        Spider jobSpider = Spider.create(new GetPageProcessor());
        jobSpider.setScheduler(new QueueScheduler()
        .setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)));
        jobSpider.addUrl(seedURL);
        jobSpider.addPipeline(new FilePipeline("BaiduZhidao/" + saveDir));
        jobSpider.thread(10);
        jobSpider.run();
        System.out.println(count);
    }
}
