import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by glacier on 14-8-12.
 */
public class Spider {

    private enum ExtractorClass {
        RenminClass,BeijingDayClass;
    }
    private ExtractorClass toExtractor( String ExtractorClass ) {
        return Spider.ExtractorClass.valueOf(ExtractorClass);
    }

    public static void main( String[] args ) throws Exception {
        int count = 0;
        while(true) {
            String URL = "http://blog.csdn.net/sapphirestart/article/details/38492185";
            Document Doc = Jsoup.connect(URL)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
            count ++;
            if ( count > 10000 )
                break;
        }

        //Spider obj = new Spider();
        //obj.start();
        //String str = obj.getTrueLink("http://zqb.cyol.com/");
        //System.out.println(str);
    }

    public void start() throws Exception {
        List<String> SpiderConf = FileUtils.readLines(new File("Spider.conf"));
        for ( String ConfLine:SpiderConf ) {
            String[] line = ConfLine.split(",");
            String TrueUrl = getTrueLink(line[1]);
            switch(toExtractor(line[0])) {
                case RenminClass: new RenminClass(line[2]).start(TrueUrl); break;
            }
        }
    }

    //获取Link对应的最终跳转链接，返回最终链接
    public String getTrueLink( String Link ) {
        try {
            Document Doc = Jsoup.connect(Link)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
            if ( Link.contains("paper.chinaso.com") ) {
                Element JumpEle = Doc.select("div[class=newpaper_con]").first();
                Link = JumpEle.select("a[href]").attr("abs:href");
                return getTrueLink(Link);
            }
            Elements Meta = Doc.select("meta[http-equiv=REFRESH]");
            if ( Meta.size() == 0 )
                return Link;
            else {
                String content = Meta.attr("content");
                int firstindex = content.toUpperCase().indexOf("URL")+4;
                String link = content.substring(firstindex);
                if ( link.indexOf('\\') >= 0 )
                    link = link.replace('\\', '/');
                if ( link.contains("http://") )
                    return link;
                String sub = Link.substring(Link.lastIndexOf('/'));
                if ( sub.length() != 1 )
                    return getTrueLink(Link.substring(0, Link.lastIndexOf('/')+1) + link);
                else
                    return getTrueLink(Link + link);
            }
        } catch( Exception e ) {
            System.out.println(e);
        }
        return Link;
    }
}
