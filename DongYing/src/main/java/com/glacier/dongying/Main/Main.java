package com.glacier.dongying.Main;

import com.glacier.dongying.DongyingBBS.BBSPageProcessor;
import com.glacier.dongying.DongyingBa.DongyingPageProcessor;
import com.glacier.dongying.DongyingTieba.TiebaPageProcessor;
import com.glacier.dongying.DongyingZhijia.ZhijiaPageProcessor;
import com.glacier.dongying.ShengliShequ.ShengliPageProcessor;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.BloomFilterDuplicateRemover;

/**
 * Created by glacier on 14-11-4.
 */
public class Main {
    public static void main(String[] args) {
        //Spider jobSpider = Spider.create(new BBSPageProcessor());     //BBS
        //Spider jobSpider = Spider.create(new TiebaPageProcessor());     //Tieba
        //Spider jobSpider = Spider.create(new ShengliPageProcessor());     //ShengliShequ
        //Spider jobSpider = Spider.create(new DongyingPageProcessor());    //东营吧
        //Spider jobSpider = Spider.create(new ZhijiaPageProcessor());    //东营之家
        Spider jobSpider = Spider.create(new com.glacier.dongying.QiluShequ.BBSPageProcessor());    //齐鲁之家
        jobSpider.setScheduler(new QueueScheduler()
        .setDuplicateRemover(new BloomFilterDuplicateRemover(100000000)));
        //jobSpider.addUrl("http://club.dzwww.com/forum-223-1.html");     //BBS
        //jobSpider.addUrl("http://tieba.baidu.com/f?kw=%B6%AB%D3%AA");     //Tieba
        //jobSpider.addUrl("http://www.slit.cn/bbs/forum.php");           //ShengliShequ
        //jobSpider.addUrl("http://www.dy8.com/");                      //东营吧
        //jobSpider.addUrl("http://www.dyzhijia.com/forum.php");          //东营之家
        jobSpider.addUrl("http://bbs.iqilu.com/forum-422-1.html");      //齐鲁之家
        jobSpider.thread(1);
        System.out.println("start");
        jobSpider.run();
        System.out.println("success");
    }
}
