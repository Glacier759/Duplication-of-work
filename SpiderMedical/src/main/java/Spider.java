import org.apache.commons.io.FileUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by glacier on 14-8-31.
 */
public class Spider {
    static BloomFilter filter = new BloomFilter();
    public static void main(String[] args) throws Exception {
        String SeedURL = "http://www.dxy.cn/";
        Spider obj = new Spider();
        obj.start(SeedURL);
    }

    public void start( String SeedURL ) throws Exception {
        try {
            HashMap<String, String> typeMap = getTypeMap(SeedURL);
            for ( String typeUrl:typeMap.keySet() ) {
                HashSet<String> listSet = getListSet(typeUrl);
                for ( String listUrl:listSet ) {
                    getNewsMap(listUrl, typeMap.get(typeUrl));
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String,String> getTypeMap( String URL ) throws Exception {
        HashMap<String,String> typeMap = new HashMap<String, String>();
        try {
            Document Doc = getDoc(URL);
            Elements typeEles = Doc.select("div[class=navlst2]").select("a[href]");
            for (Element typeEle:typeEles) {
                typeMap.put(typeEle.attr("abs:href"), typeEle.text());
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return typeMap;
    }

    public HashSet<String> getListSet( String URL ) throws Exception {
        HashSet<String> listSet = new HashSet<String>();
        try {
            Document Doc = getDoc(URL);
            Elements aTags = Doc.select("a[href]");
            for ( Element aTag:aTags ) {
                String link = aTag.attr("abs:href");
                if ( link.contains("tag") ) {
                    listSet.add(link);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return listSet;
    }

    public HashMap<String,String> getNewsMap(String URL, String typeDir) throws Exception {
        HashMap<String,String> newsMap = new HashMap<String, String>();
        Document Doc = null;
        String listDir = getDoc(URL).select("title").text();
        listDir = listDir.substring(0,listDir.indexOf('_'));
        try {
            do {
                Doc = getDoc(URL);
                Elements newsEles = Doc.select("a[class=h5]");
                for (Element newsEle : newsEles) {
                    newsMap.put(newsEle.attr("abs:href"), newsEle.text());
                }
                for ( String newsUrl:newsMap.keySet() ) {
                    if ( filter.isUniqueValue(newsUrl) )
                        continue;
                    String content = getNewsCont(newsUrl);
                    if ( content.length() == 0 )
                        continue;
                    File file = new File(new File(new File(new File("Data"), typeDir), listDir),System.currentTimeMillis()+".txt");
                    FileUtils.writeStringToFile(file, content, "UTF-8");
                    System.out.println(newsUrl);
                    filter.addValue(newsUrl);
                }
                newsMap.clear();
            }while( (URL = getNextPage(Doc)) != null );
        }catch (Exception e) {
            e.printStackTrace();
        }
        return newsMap;
    }

    public String getNewsCont(String URL) throws Exception {
        String content = "";
        try {
            Document Doc = getDoc(URL);
            Elements contEles = Doc.select("div[id=content]").select("p");
            for ( Element contEle:contEles ) {
                content += contEle.text() + "\n";
            }
            content += URL;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public String getNextPage(Document Doc) throws Exception {
        try {
            Elements nextEles = Doc.select("div[class=el_page x_page1]").select("a[href]");
            for ( Element nextEle:nextEles ) {
                if ( nextEle.text().equals("下一页") )
                    return nextEle.attr("abs:href");
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
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
}
