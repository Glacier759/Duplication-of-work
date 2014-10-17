import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by glacier on 14-10-17.
 */
public class BlogPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(300);

    @Override
    public void process(Page page) {
        //System.out.println(page.getUrl());

        //解析网页中包含的地址，加入队列
        List<String> urlist = page.getHtml().links().all();
        for (ListIterator<String> itor = urlist.listIterator(); itor.hasNext(); ) {
            String line = itor.next();
            if ( !line.contains("blog.sina.com.cn/s/blog_") )
                itor.remove();
            else if ( line.contains("?") ) {
                itor.set(line.substring(0, line.indexOf("?")));
            }
        }
        page.addTargetRequests(urlist);

        try {
            //解析并储存博客
            Document document = page.getHtml().getDocument();
            String title = document.select("h2[class=titName SG_txta]").text();
            String time = document.select("span[class=time SG_txtc]").text();
            Elements tagEles = document.select("h3");
            List<String> tagList = new ArrayList<String>();
            for (Element tagEle : tagEles) {
                tagList.add(tagEle.text());
            }
            String content = document.select("div[id=sina_keyword_ad_area2]").text();
            SaveXML.save(title, time, content, tagList);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
