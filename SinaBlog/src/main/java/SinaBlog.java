import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.BloomFilterDuplicateRemover;

/**
 * Created by glacier on 14-10-17.
 */
public class SinaBlog {
    public static String saveDir = "SinaBlogData";
    public static void main(String[] args) {

        Spider sinaSipder = Spider.create(new BlogPageProcessor());
        sinaSipder.setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)));
        sinaSipder.addUrl("http://blog.sina.com.cn/lm/finance/");
        sinaSipder.thread(10);
        sinaSipder.run();

    }
}
