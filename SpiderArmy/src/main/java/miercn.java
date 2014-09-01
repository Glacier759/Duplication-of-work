import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

/**
 * Created by glacier on 14-9-1.
 */
public class miercn {
    BloomFilter filter = new BloomFilter();
    Redis redis = new Redis();

    public void init ( String SeedURL ) throws Exception {
        filter.clearBitset();
        redis.ConnectRedis();
        redis.setKey("miercn");
        redis.clearRedis();

        start(SeedURL);
        int count = 0;
        while( redis.getLength() > 0 ) {
            if ( count % 20 == 0 ) {
                System.out.println("redis length = " + redis.getLength());
                count = 0;
            }
            count ++;
            System.out.print(count+"  ");
            getBlog();
        }
    }

    public void start( String SeedURL ) throws Exception {
        try {
            Document Doc = Jsoup.connect(SeedURL)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(30000)
                    .get();
            Elements linksEle = Doc.select("a[href]");
            for (Element linkEle : linksEle) {
                String link = linkEle.attr("abs:href");
                if (link.contains("bbs.miercn.com/") && link.contains("/thread_") && !link.contains("#") && !filter.isUniqueValue(link)) {
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
        //String URL = "http://blog.csdn.net/sweetvvck/article/details/38409861";
        System.out.println(URL);
        try {
            Document Doc = Jsoup.connect(URL)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(30000)
                    .get();
            Elements content = Doc.select("SPAN[id=zoom]").select("p");
            String text = Doc.select("h1").text()+"\n";
            for (Element ptag : content) {
                if ( ptag.text().length() > 0 )
                    text += ptag.text() + "\n";
            }
            String Text = text + URL;
            //String date = Doc.select("span[class=link_postdate]").text();
            //String XML = xmlFormat.start(text, date);
            //System.out.println(XML);
            if ( text.length() >= 30 )
                FileUtils.writeStringToFile(new File(new File("Data"), System.currentTimeMillis() + ".txt"), Text, "UTF-8");
            Elements linksEle = Doc.select("a[href]");
            for (Element linkEle : linksEle) {
                String link = linkEle.attr("abs:href");
                if (link.contains("bbs.miercn.com/") && link.contains("/thread_") && !link.contains("#") && !filter.isUniqueValue(link)) {
                    redis.pushValue(link);
                    filter.addValue(link);
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
