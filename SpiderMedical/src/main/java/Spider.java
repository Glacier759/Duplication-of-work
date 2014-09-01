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
    public static void main(String[] args) throws Exception {
        //String SeedURL = "http://www.dxy.cn/";
        //Dingxiang obj = new Dingxiang();
        //obj.start(SeedURL);
        String SeedURL = "http://www.cmt.com.cn/";
        CmtCom obj = new CmtCom();
        obj.init(SeedURL);
    }
}
