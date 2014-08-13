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
        RenminClass,BeijingDayClass,BinhaiClass,BeijingChen,BeijingQingnian,BingtuanClass,
        ChangchunClass,XianClass,ChongqingDayClass,ChengduShangbao,ChengduRibao,ChangjiangRibao,
        ChongqingChenbao,DalianRibao,DazhongRibao,FazhiRibao;
    }
    private ExtractorClass toExtractor( String ExtractorClass ) {
        return Spider.ExtractorClass.valueOf(ExtractorClass);
    }

    public static void main( String[] args ) throws Exception {

        Spider obj = new Spider();
        obj.start();
        //new DalianRibao("测试").getNewsInfo("http://szb.dlxww.com/dlrb/html/2014-08/13/content_1048291.htm");
    }

    public void start() throws Exception {
        List<String> SpiderConf = FileUtils.readLines(new File("Spider.conf"));
        for ( String ConfLine:SpiderConf ) {
            String[] line = ConfLine.split(",");
            String TrueUrl = getTrueLink(line[1]);
            System.out.println(TrueUrl);
            switch(toExtractor(line[0])) {
                case RenminClass: new RenminClass(line[2]).start(TrueUrl); break;
                case BeijingDayClass: new BeijingDayClass(line[2]).start(TrueUrl); break;
                case BinhaiClass: new BinhaiClass(line[2]).start(TrueUrl); break;
                case BeijingChen: new BeijingChen(line[2]).start(TrueUrl); break;
                case BeijingQingnian: new BeijingQingnian(line[2]).start(TrueUrl); break;
                case BingtuanClass: new BingtuanClass(line[2]).start(TrueUrl); break;
                case ChangchunClass: new ChangchunClass(line[2]).start(TrueUrl); break;
                case XianClass: new XianClass(line[2]).start(TrueUrl); break;
                case ChongqingDayClass: new ChongqingDayClass(line[2]).start(TrueUrl); break;
                case ChengduShangbao: new ChengduShangbao(line[2]).start(TrueUrl); break;
                case ChengduRibao: new ChengduRibao(line[2]).start(TrueUrl); break;
                case ChangjiangRibao: new ChangjiangRibao(line[2]).start(TrueUrl); break;
                case ChongqingChenbao: new ChongqingChenbao(line[2]).start(TrueUrl); break;
                case DalianRibao: new DalianRibao(line[2]).start(TrueUrl); break;
                case DazhongRibao: new DazhongRibao(line[2]).start(TrueUrl); break;
                case FazhiRibao: new FazhiRibao(line[2]).start(TrueUrl); break;
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
