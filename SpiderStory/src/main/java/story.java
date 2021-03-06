
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.sound.midi.SysexMessage;
import java.io.File;
import java.util.HashMap;

/**
 * Created by glacier on 14-8-30.
 */
public class story {
    public static void main( String[] args ) throws Exception {
        String SeedURL = "http://www.tianyabook.org/";
        story obj = new story();
        obj.start(SeedURL);
    }

    public Document getDoc( String URL ) throws Exception {
        Document Doc = null;
        try {
            Doc = Jsoup.connect(URL)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return Doc;
    }

    public void start( String SeedURL ) throws Exception {
        HashMap<String,String> typeMap = getTypeMap(SeedURL);
        for ( String typeURL:typeMap.keySet() ) {
            String typeDir = typeMap.get(typeURL);
            HashMap<String,String> nameMap = getNameMap(typeURL);
            for ( String nameURL:nameMap.keySet() ) {
                String nameDir = nameMap.get(nameURL);
                HashMap<String,String> chapMap = getChapMap(nameURL);
                for ( String chapURL:chapMap.keySet() ) {
                    String content = getContent(chapURL);
                    File file = new File(new File(new File(new File("Data"),typeDir), nameDir), System.currentTimeMillis()+".txt");
                    System.out.println(file.getPath());
                    FileUtils.writeStringToFile(file,content);
                }
            }
        }
    }

    public HashMap<String,String> getTypeMap( String URL ) throws Exception {
        HashMap<String,String> typeMap = new HashMap<String, String>();
        try {
            Document Doc = getDoc(URL);
            Elements typeEles = Doc.select("div[class=nav]").select("a[href]");
            for (Element typeEle : typeEles) {
                typeMap.put(typeEle.attr("abs:href"), typeEle.text());
            }
        }catch(Exception e) {
            System.out.println(e);
        }
        return typeMap;
    }

    public HashMap<String,String> getNameMap( String URL ) throws Exception {
        HashMap<String,String> nameMap = new HashMap<String, String>();
        try {
            do {
                Document Doc = getDoc(URL);
                Elements nameEles = Doc.select("div[class=leftbox]").select("a[href]");
                for (Element nameEle : nameEles) {
                    nameMap.put(nameEle.attr("abs:href"), nameEle.text());
                }
            }while( (URL = getNextPage(URL)) != null );
        }catch(Exception e) {
            System.out.println(e);
        }
        return nameMap;
    }

    public String getNextPage( String URL ) throws Exception {
        try {
            Document Doc = getDoc(URL);
            Elements aTags = Doc.select("ul[class=pagelist]").select("a[href]");
            for (Element aTag : aTags) {
                if (aTag.text().equals("下一页")) {
                    return aTag.attr("abs:href");
                }
            }
        }catch(Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public HashMap<String,String> getChapMap( String URL ) throws Exception {
        HashMap<String,String> chapMap = new HashMap<String, String>();
        try {
            Document Doc = getDoc(URL);
            Elements chapEles = Doc.select("div[id=main]").select("dl").select("a[href]");
            for (Element chapEle : chapEles) {
                chapMap.put(chapEle.attr("abs:href"), chapEle.text());
            }
        }catch(Exception e) {
            System.out.println(e);
        }
        return chapMap;
    }

    public String getContent( String URL ) throws Exception {
        String content = null;
        try {
            Document Doc = getDoc(URL);
            Elements contEles = Doc.select("div[id=main]").select("p");
            content = contEles.text().replaceAll("     ", "\n");
        }catch(Exception e) {
            System.out.println(e);
        }
        return content;
    }

}
