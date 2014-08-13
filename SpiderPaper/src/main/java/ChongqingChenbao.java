import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by glacier on 14-8-12.
 */
public class ChongqingChenbao {

    SaveXML savexml = new SaveXML();

    public ChongqingChenbao( String newspaper ) {
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
            URL url = new URL(Link);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer html = new StringBuffer();
            String line = "";
            while((line = reader.readLine()) != null) {
                html.append(line + "\n");
            }
            Document Doc = Jsoup.parse(html.toString(), "utf-8");
            //获取版面链接
            Elements Layouts = Doc.select("a[id=pageLink]");
            for (Element Layout:Layouts) {
                LayoutMap.put(Link.substring(0,Link.lastIndexOf('/')+1)+Layout.attr("href"), Layout.text());
            }

        } catch( Exception e ) {
            e.printStackTrace();
        }
        return LayoutMap;
    }

    public HashSet<String> getNewsLinks( String LayoutLink ) {
        HashSet<String> NewsLink = new HashSet<String>();
        System.out.println(LayoutLink);
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
            URL url = new URL(NewsUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer html = new StringBuffer();
            String line = "";
            while((line = reader.readLine()) != null) {
                html.append(line + "\n");
            }
            Document Doc = Jsoup.parse(html.toString(), "utf-8");
            Element TitleEle = Doc.select("td[align=center]").first();
            String Title = TitleEle.text(); 		//获得文章title

            String PublishTime = getDate(NewsUrl); 	//获得文章发表日期
            Element TextContent = Doc.select("div[id=ozoom]").first();
            Elements ContentPTags = TextContent.select("p");
            String Content = "\r\n"; 						//获得文章正文内容
            for ( Element ContentPTag:ContentPTags ) {
                Content += ContentPTag.text() + "\r\n";
            }
            Content = new String(Content.getBytes());
            List<String> IMGList = new ArrayList<String>(); 		//获得图片地址列表
            Elements IMGs = Doc.select("div[class=hhhhh]").select("img[src]");
            for ( Element IMG:IMGs ) {
                if ( !IMGList.contains(url.getHost()+IMG.attr("src").substring(8)) )
                    IMGList.add(url.getHost()+IMG.attr("src").substring(8));
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
