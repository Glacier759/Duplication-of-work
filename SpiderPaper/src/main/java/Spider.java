import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.security.sasl.SaslServer;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.SimpleFormatter;

/**
 * Created by glacier on 14-8-12.
 */
public class Spider {

    private enum ExtractorClass {
        RenminClass,BeijingDayClass,BinhaiClass,BeijingChen,BeijingQingnian,BingtuanClass,
        ChangchunClass,XianClass,ChongqingDayClass,ChengduShangbao,ChengduRibao,ChangjiangRibao,
        ChongqingChenbao,DalianRibao,DazhongRibao,FazhiRibao,FujianRibao,GansuRibao,
        GuiyangDayClass,GuangxiRibao,GuangzhouRibao,GuizhouRibao,HaerbinRibao,HaikouWanbao,
        HainanRibao,HainanTequbao,HHHHClass,HefeiWanbao,HenanRibao,HunanRibao,HuaxiDushibao,
        HeilongjiangClass,JinanRibao,JilinRibao,JianchaRibao,JiefangJunbao,JiefangRibao,
        JinriZaobao,Jinwanbao,JinghuaShibao,JingjiCankao,KejiRibao,KunmingRibao,LanzhouClass,
        NanchangRibao,NanfangRibao,NanjingRibao,NeimengguRibao,NingboRibao,NingxiaRibao,
        RenminYoudian,RenminZhengxie,RenminFayuan,ShanxiRibao,ShenyangRibao,SichuanRibao,
        XiningWanbao,XinhuaMeiri,XinjingBao,YangziWanbao,YangchengWanbao,YinchuanWanbao,
        ZhongguoFunv,ZhongguoJiaoyu;
    }
    private ExtractorClass toExtractor( String ExtractorClass ) {
        return Spider.ExtractorClass.valueOf(ExtractorClass);
    }

    public static void main( String[] args ) throws Exception {

        Spider obj = new Spider();
        obj.start();
        //new YinchuanWanbao("测试").getNewsInfo("http://szb.ycen.com.cn/html/2014-08/28/content_129257.htm");
    }

    public void start() throws Exception {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM/dd");
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
                case FujianRibao: new FujianRibao(line[2]).start(TrueUrl); break;
                case GansuRibao: new GansuRibao(line[2]).start(TrueUrl); break;
                case GuiyangDayClass: new GuiyangDayClass(line[2]).start(TrueUrl); break;
                case GuangxiRibao: new GuangxiRibao(line[2]).start(TrueUrl); break;
                case GuangzhouRibao: new GuangzhouRibao(line[2]).start(TrueUrl); break;
                case GuizhouRibao: new GuizhouRibao(line[2]).start(TrueUrl); break;
                case HaerbinRibao: new HaerbinRibao(line[2]).start(TrueUrl); break;
                case HaikouWanbao: new HaikouWanbao(line[2]).start(TrueUrl); break;
                case HainanRibao: new HainanRibao(line[2]).start(TrueUrl); break;
                case HainanTequbao: new HainanTequbao(line[2]).start(line[1]+"hntqb"+new URL(TrueUrl).getPath()); break;
                case HHHHClass: new HHHHClass(line[2]).start(TrueUrl); break;
                case HefeiWanbao: new HefeiWanbao(line[2]).start(TrueUrl); break;
                case HenanRibao: new HenanRibao(line[2]).start(TrueUrl); break;
                case HunanRibao: new HunanRibao(line[2]).start(TrueUrl); break;
                case HuaxiDushibao: new HuaxiDushibao(line[2]).start(TrueUrl); break;
                case HeilongjiangClass: new HeilongjiangClass(line[2]).start(TrueUrl); break;
                case JinanRibao: new JinanRibao(line[2]).start(TrueUrl); break;
                case JilinRibao: new JilinRibao(line[2]).start(TrueUrl); break;
                case JianchaRibao: new JianchaRibao(line[2]).start(TrueUrl); break;
                case JiefangJunbao: new JiefangJunbao(line[2]).start(TrueUrl); break;
                case JiefangRibao: new JiefangRibao(line[2]).start("http://newspaper.jfdaily.com/jfrb/html/"+format.format(date)+"/node_2.htm"); break;
                case JinriZaobao: new JinriZaobao(line[2]).start(TrueUrl); break;
                case Jinwanbao: new Jinwanbao(line[2]).start(TrueUrl); break;
                case JinghuaShibao: new JinghuaShibao(line[2]).start(TrueUrl); break;
                case JingjiCankao: new JingjiCankao(line[2]).start(TrueUrl); break;
                case KejiRibao: new KejiRibao(line[2]).start(TrueUrl); break;
                case KunmingRibao: new KunmingRibao(line[2]).start(TrueUrl); break;
                case LanzhouClass: new LanzhouClass(line[2]).start(TrueUrl); break;
                case NanchangRibao: new NanchangRibao(line[2]).start(TrueUrl); break;
                case NanfangRibao: new NanfangRibao(line[2]).start(TrueUrl); break;
                case NanjingRibao: new NanjingRibao(line[2]).start(TrueUrl); break;
                case NeimengguRibao: new NeimengguRibao(line[2]).start(TrueUrl); break;
                case NingboRibao: new NingboRibao(line[2]).start(TrueUrl); break;
                case NingxiaRibao: new NingxiaRibao(line[2]).start(TrueUrl); break;
                case RenminYoudian: new RenminYoudian(line[2]).start(TrueUrl); break;
                case RenminZhengxie: new RenminZhengxie(line[2]).start(TrueUrl); break;
                case RenminFayuan: new RenminFayuan(line[2]).start(TrueUrl); break;
                case ShanxiRibao: new ShanxiRibao(line[2]).start(TrueUrl); break;
                case ShenyangRibao: new ShenyangRibao(line[2]).start(TrueUrl); break;
                case SichuanRibao: new SichuanRibao(line[2]).start(TrueUrl); break;
                case XiningWanbao: new XiningWanbao(line[2]).start(TrueUrl); break;
                case XinhuaMeiri: new XinhuaMeiri(line[2]).start(TrueUrl); break;
                case XinjingBao: new XinjingBao(line[2]).start(TrueUrl); break;
                case YangziWanbao: new YangziWanbao(line[2]).start(TrueUrl); break;
                case YangchengWanbao: new YangchengWanbao(line[2]).start(TrueUrl); break;
                case YinchuanWanbao: new YinchuanWanbao(line[2]).start(TrueUrl); break;
                case ZhongguoFunv: new ZhongguoFunv(line[2]).start(TrueUrl); break;
                case ZhongguoJiaoyu: new ZhongguoJiaoyu(line[2]).start(TrueUrl); break;
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
