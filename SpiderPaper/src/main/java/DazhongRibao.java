import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by glacier on 14-8-13.
 */

public class DazhongRibao {

    SaveXML savexml = new SaveXML();

    public DazhongRibao( String newspaper ) {
        savexml.format.newspaper = newspaper;
    }
    public void start( String URL ) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        savexml.format.crawltime = sdf.format(new Date());
        savexml.format.language = "中文";
        savexml.format.encode = "UTF-8";

        HashMap<String,String> LayoutMap = getLayout(URL);
        for ( String LayoutLink:LayoutMap.keySet() ) {
            String Page = LayoutMap.get(LayoutLink); 			//获得版面
            savexml.format.page = Page;
            HashSet<String> NewsLink = getNewsLinks( LayoutLink );
            for ( String NewsUrl:NewsLink ) {
                getNewsInfo(NewsUrl);
            }
        }
    }

    public HashMap<String,String> getLayout( String URL ) {
        HashMap<String,String> LayoutMap = new HashMap<String,String>();
        try {
            Document Doc = Jsoup.connect(URL)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
            Elements Layouts = Doc.select("li[class=banci]");
            for ( Element Layout:Layouts ) {
                LayoutMap.put(Layout.select("a[href]").attr("abs:href"), Layout.text());
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return LayoutMap;
    }

    public HashSet<String> getNewsLinks( String LayoutLink ) {
        HashSet<String> NewsLink = new HashSet<String>();
        try {
            Document Doc = Jsoup.connect(LayoutLink)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
            Elements Areas = Doc.select("ul[id=newslist]");
            for ( Element Area:Areas ) {
                NewsLink.add(Area.select("a[href]").attr("abs:href"));
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }
        return NewsLink;
    }

    public void getNewsInfo( String NewsUrl ) { 	//获得新闻来源URL
        try {
            System.out.println(NewsUrl);
            Document Doc = Jsoup.connect(NewsUrl)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
            Element TitleEle = Doc.select("h1").first();
            String Title = TitleEle.text(); 		//获得文章title

            String PublishTime = getDate(NewsUrl); 	//获得文章发表日期
            Elements ContentPTags = Doc.select("span[id=contenttext]");
            String Content = "\r\n" + ContentPTags.text().replaceAll(" 　　", "\r\n") + "\r\n:";
            List<String> IMGList = new ArrayList<String>(); 		//获得图片地址列表
            Elements IMGs = Doc.select("img[id=image]");
            for ( Element IMG:IMGs ) {
                IMGList.add(IMG.attr("abs:src"));
            }
            savexml.format.source = NewsUrl;
            savexml.format.title = Title;
            savexml.format.publishtime = PublishTime;
            savexml.format.body = Content;
            savexml.format.img = IMGList;
            savexml.save();
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public String getDate( String Link ) {
        try {
            String UrlPath = new URL(Link).getPath();
            int firstindex = UrlPath.indexOf("2014");
            String Date = UrlPath.substring(firstindex, firstindex+10);
            Date = Date.replace('/', '-');
            return Date;
        } catch ( Exception e ) {
            System.out.println(e);
        }
        return null;
    }
}