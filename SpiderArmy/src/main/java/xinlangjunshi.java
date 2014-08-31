import org.apache.commons.io.FileUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashMap;

/**
 * Created by glacier on 14-8-31.
 */
public class xinlangjunshi {
    static int count;
    public void start() throws Exception {
        HashMap<String,String> urlMap = new HashMap<String, String>();
        urlMap.put("http://roll.mil.news.sina.com.cn/col/zgjq/index.shtml", "中国军情");
        urlMap.put("http://roll.mil.news.sina.com.cn/col/gjjq/index.shtml", "国际军情");
        for ( String typeUrl:urlMap.keySet() ) {
            String typeDir = urlMap.get(typeUrl);
            HashMap<String,String> newsMap = getNewsMap(typeUrl, typeDir);
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

    public HashMap<String,String> getNewsMap( String URL, String typeDir ) throws Exception {
        HashMap<String,String> newsMap = new HashMap<String, String>();
        Document Doc = null;
        try {
            do {
                Doc = getDoc(URL);
                Elements newsTags = Doc.select("div[class=fixList]").select("li").select("a[href]");
                for (Element newsTag : newsTags) {
                    newsMap.put(newsTag.attr("abs:href"), newsTag.text());
                }
                for ( String newsUrl:newsMap.keySet() ) {
                    String content = getNewsCont(newsUrl);
                    if ( content.length() == 0 )
                        continue;
                    count ++;
                    File file = new File(new File(new File(new File("armyData"), "新浪军事"), typeDir), System.currentTimeMillis()+".txt");
                    FileUtils.writeStringToFile(file, content, "UTF-8");
                }
                newsMap.clear();
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
            Elements pTags = Doc.select("div[class=blkContainerSblk]").select("p");
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
            Elements nextTags = Doc.select("a[title=下一页]").select("a[href]");
            if ( nextTags.size() == 0 )
                return null;
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
