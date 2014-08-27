import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by glacier on 14-8-27.
 */
public class RenminZhengxie {

    SaveXML savexml = new SaveXML();

    public RenminZhengxie( String newspaper ) {
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

    public HashMap<String,String> getLayout( String Link ) {
        HashMap<String,String> LayoutMap = new HashMap<String,String>();
        try {
            Document Doc = Jsoup.connect(Link)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
            //获取版面链接
            Elements Layouts = Doc.select("a[class=banzi_info]");
            for (Element Layout:Layouts) {
                    LayoutMap.put(Layout.attr("abs:href"), Layout.text());
            }

        } catch( Exception e ) {
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
            Elements Areas = Doc.select("area");
            for ( Element Area:Areas ) {
                NewsLink.add(Area.attr("abs:href"));
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
            Element TitleEle = Doc.select("div[id=title]").first();
            String Title = TitleEle.text(); 		//获得文章title

            String PublishTime = Doc.select("h4").last().text().substring(0,10); //获得文章发表日期
            Elements ContentPTags = Doc.select("div[id=content]").select("p");
            String Content = "\r\n"; 						//获得文章正文内容
            for ( Element ContentPTag:ContentPTags ) {
                Content += ContentPTag.text() + "\r\n";
            }
            List<String> IMGList = new ArrayList<String>(); 		//获得图片地址列表
            Elements IMGs = Doc.select("div[id=imgs]").select("img[src]");
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
