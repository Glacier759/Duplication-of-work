package com.glacier.baidu;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * Created by glacier on 14-10-16.
 */
public class GetPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(300).setCharset("gbk");
    @Override
    public void process(Page page) {
        System.out.println("get page: " + page.getUrl());
        List<String> urlist = page.getHtml().$("div.wgt-category").links().all();
        Document document = page.getHtml().getDocument();
        Elements questionEles = document.select("ul[class=question-list]").select("a[class=question-title]");
        String questionPage = "\n";
        for ( Element questionEle:questionEles ) {
            questionPage += questionEle.text() + "\n";
        }
        page.putField("question", questionPage);

        String nextPage = page.getHtml().$("a.pager-next").links().get();
        page.addTargetRequest(nextPage);
        for (  String urline:urlist ) {
            if ( urline.contains("#") )
                continue;
            BaiduZhidao.count ++ ;
            page.addTargetRequest(urline);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
