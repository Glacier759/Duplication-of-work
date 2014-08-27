import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

/**
 * Created by glacier on 14-8-27.
 */
public class Spider {

    BloomFilter filter = new BloomFilter();
    Redis redis = new Redis();

    static public void main ( String[] args ) throws Exception {
        String SeedURL = "http://blog.csdn.net";
        Spider obj = new Spider();

        obj.filter.clearBitset();
        obj.redis.ConnectRedis();
        obj.redis.setKey("csdn");
        obj.redis.clearRedis();

        obj.start(SeedURL);
        int count = 0;
        while( obj.redis.getLength() > 0 ) {
            if ( count % 20 == 0 ) {
                System.out.println("redis length = " + obj.redis.getLength());
                count = 0;
            }
            count ++;
            System.out.print(count+"  ");
            obj.getBlog();
        }
    }

    public void start( String SeedURL ) throws Exception {
        try {
            Document Doc = Jsoup.connect(SeedURL)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
            Elements linksEle = Doc.select("a[href]");
            for (Element linkEle : linksEle) {
                String link = linkEle.attr("abs:href");
                if (link.contains("/article/details/") && !link.contains("#") && !filter.isUniqueValue(link)) {
                    redis.pushValue(link);
                    filter.addValue(link);
                }
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public void getBlog() throws Exception {
        String URL = redis.popValue();
        System.out.println(URL);
        try {
            Document Doc = Jsoup.connect(URL)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
            Elements content = Doc.select("div[id=article_content]").select("p");
            String text = "";
            for (Element ptag : content) {
                if ( ptag.text().length() > 0 )
                    text += ptag.text() + "\r\n";
            }
            FileUtils.writeStringToFile(new File(new File("Data"), System.currentTimeMillis()+".txt"), text, "UTF-8");
            Elements linksEle = Doc.select("a[href]");
            for (Element linkEle : linksEle) {
                String link = linkEle.attr("abs:href");
                if (link.contains("/article/details/") && !link.contains("#") && !filter.isUniqueValue(link)) {
                    redis.pushValue(link);
                    filter.addValue(link);
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
