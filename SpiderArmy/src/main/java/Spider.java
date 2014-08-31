import org.apache.commons.io.FileUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashMap;

/**
 * Created by glacier on 14-8-30.
 */
public class Spider {
    static int count = 0;
    public static void main(String[] args) throws Exception {
        HashMap<String,String> urlMap = new HashMap<String, String>();
        urlMap.put("http://www.xinjunshi.com/jujiao/hxla/", "海峡两岸");
        urlMap.put("http://mil.xinjunshi.com/zlps/", "战略评述");
        urlMap.put("http://www.xinjunshi.com/jujiao/jllt/", "军力擂台");
        urlMap.put("http://www.xinjunshi.com/jujiao/hwsd/", "海外观点");
        urlMap.put("http://www.xinjunshi.com/hudong/tsjs", "图说军事");
        Spider obj = new Spider();
        for ( String typeUrl:urlMap.keySet() ) {
            String typeDir = urlMap.get(typeUrl);
            HashMap<String,String> newsMap = obj.getNewsMap(typeUrl);
            for ( String newsUrl:newsMap.keySet() ) {
                String content = obj.getNewsCont(newsUrl);
                if ( content.length() == 0 )
                    continue;
                count ++;
                File file = new File(new File(new File("armyData"), typeDir), System.currentTimeMillis()+".txt");
                FileUtils.writeStringToFile(file, content, "UTF-8");
            }
        }
        System.out.println("Success: " + count);
    }

    public Document getDoc( String URL ) throws Exception {
        Document Doc = null;
        try {
            Doc = Jsoup.connect(URL)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(30000)
                    .get();
        }catch(HttpStatusException e) {
        }catch(Exception e) {
            System.out.println(e);
        }
        return Doc;
    }

    public HashMap<String,String> getNewsMap( String URL ) throws Exception {
        HashMap<String,String> newsMap = new HashMap<String, String>();
        Document Doc = null;
        try {
            do {
                Doc = getDoc(URL);
                Elements newsTags = Doc.select("div[class=newsList]").select("li").select("a[href]");
                for (Element newsTag : newsTags) {
                    newsMap.put(newsTag.attr("abs:href"), newsTag.text());
                }
                System.out.println(newsMap.size());
            }while( (URL = getNextPage(Doc)) != null );
        }catch(Exception e) {
            System.out.println(e);
        }
        return newsMap;
    }

    public String getNewsCont( String URL ) throws Exception {
        System.out.println(URL);
        String content = "";
        try {
            Document Doc = getDoc(URL);
            Elements pTags = Doc.select("div[id=cont]").select("p");
            content = Doc.select("h1").text()+"\n";
            for ( Element pTag:pTags ) {
                if ( pTag.text().length() > 0 )
                    content += pTag.text() + "\n";
            }
            content += URL;
        }catch (Exception e) {
            System.out.println(e);
        }
        return content;
    }

    public String getNextPage( Document Doc ) throws Exception {
        try {
            Elements nextTags = Doc.select("ul[class=pagelist]").select("a[href]");
            for ( Element nextTag:nextTags ) {
                if ( nextTag.text().equals("下一页") )
                    return nextTag.attr("abs:href");
            }
        }catch(Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
