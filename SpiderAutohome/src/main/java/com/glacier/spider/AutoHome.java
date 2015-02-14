package com.glacier.spider;

import org.apache.log4j.Logger;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA on 2015-02-14 16:26.
 * Author:  Glacier (RenLixiang), OurHom.759@gmail.com
 * Company: Class 1204 of Computer Science and Technology
 */
public class AutoHome {

    private static Logger logger = Logger.getLogger(AutoHome.class.getName());

    public static void main(String[] args) {
        AutoHome obj = new AutoHome();
        obj.init("http://k.autohome.com.cn/");

    }

    public void init(String url) {
        try {
            Document document = document(url);
            Elements elements = document.select("div[class=navcar]").select("li");
            for ( Element element:elements ) {
                Element aTag = element.select("a[href]").first();
                if ( aTag.text().contains("电动车") )
                    continue;
                String type = aTag.text();
                String link = aTag.attr("abs:href");
                System.out.println(type + " - " + link);
                getCarList(link);
                break;
            }
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    private void getCarList(String link) {
        try {
            Document document = document(link);
            Elements elements = document.select("div[class=tab-content-item]").first().select("div[class=uibox]");

            for ( Element element:elements ) {
                Elements typeList = element.select("dl");
                for ( Element typeTag:typeList ) {
                    String name1 = typeTag.select("dt").first().text();
                    String name2 = typeTag.select("div[class=h3-tit]").first().text();

                    Elements carList = typeTag.select("ul").select("li");
                    for ( Element carTag:carList ) {
                        String name3 = carTag.select("h4").text();
                        Elements temps = carTag.select("a[href]");
                        String url = null;
                        for ( Element temp:temps ) {
                            if ( temp.text().contains("口碑") ) {
                                url = temp.attr("abs:href");
                                break;
                            }
                        }
                        System.out.println("name1 = " + name1 + "\tname2 = " + name2 + "\tname3 = " + name3 + "\turl = " + url);
                        getCarInfo("http://k.autohome.com.cn/2922/#pvareaid=104136");
                        System.exit(1);
                    }
                }
            }

        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    private void getCarInfo(String link) {
        try {
            Document document = document(link);

            //-----------获取当前车型的基础信息
            Elements carInfos = document.select("ul[class=list-ul font-14]").select("li");
            String score = carInfos.select("span[class=font-arial number-fen]").text();                     //用户评分
            String peopleNum = carInfos.select("span[class=font-arial number-ren]").text();                 //参与人数
            String rank = carInfos.select("a[class=font-arial number-ming]").text();                        //紧凑型车排名
            String price = carInfos.select("li[class=price]").select("span[class]").text();                 //全车系厂商指导价格
            String dealerPriceBox = carInfos.select("li[id=dealerPriceBox]").select("span[class]").text();  //全车系网友裸车购买价格
            String total = null;                                                                            //该级别共有的车系个数
            Pattern pattern = Pattern.compile("该级别共(.*)个车系");
            Matcher matcher = pattern.matcher(carInfos.toString());
            if ( matcher.find() )
                total = matcher.group(1);
            System.out.println("score = " + score + "\tpeopleNum = " + peopleNum + "\trank = " + rank + "\tprice = " + price + "\tdealerP = " + dealerPriceBox + "\ttotal = " + total);
            //-----------获取完毕

            //-----------耗油情况
            Elements petrolEles = document.select("div[class=tab-content]").first().select("div[style]");
            for ( Element petrolEle:petrolEles ) {
                String petrol_type = petrolEle.attr("class");
                if (petrol_type.contains("manual-cont"))
                    petrol_type = "手动";
                else if (petrol_type.contains("auto-cont"))
                    petrol_type = "自动";

                String petrol_count = petrolEle.select("p[class=font-14]").text();
                Elements temps = petrolEle.select("ul[class=manual-cont-left]").select("li");
                List<String> petrol_info = new ArrayList<String>();
                for (Element temp : temps) {
                    String name1 = temp.select("span[class=tit-txt]").remove().text();
                    String name2 = temp.select("span[class=c999]").remove().text();
                    String name3 = temp.text();
                    System.out.println("name1 = " + name1 + "\tname2 = " + name2 + "\tname3 = " + name3);
                    petrol_info.add(name1 + "," + name2 + "," + name3);
                }
                System.out.println("petrol_type = " + petrol_type + "\tpetrol_count = " + petrol_count);
                //---------耗油情况完毕
            }
            //---------所有车型评分情况
            Elements carScores = document.getElementById("specListUL").select("li[data-value]");
            List<String> emissList = new ArrayList<String>();
            for ( Element carScore:carScores ) {
                String emiss_title = carScore.select("div[class=emiss-title]").text();
                String emiss_price = carScore.select("div[class=emiss-price]").select("a").text();
                String emiss_fen = carScore.select("div[class=emiss-fen]").select("a").text();
                String emiss_ren = carScore.select("div[class=emiss-ren]").select("a").text();
                if ( emiss_fen.length() == 0 )
                    emiss_fen = " ";
                if ( emiss_ren.length() == 0 )
                    emiss_ren = " ";
                System.out.println("title = " + emiss_title + "\tprice = " + emiss_price + "\tfen = " + emiss_fen + "\tren = " + emiss_ren);
                emissList.add(emiss_title + "," + emiss_price + "," + emiss_fen + "," + emiss_ren);
            }
            //---------所有车型评分情况完毕

            //---------获取所有评论内容
            getAllReview(link);

        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    private void getAllReview(String link) {
        try {
            do {
                Document document = document(link);
                Elements reviewEles = document.select("div[class=mouthcon js-koubeidataitembox]");
                for (Element reviewEle : reviewEles) {
                    String userID = reviewEle.select("div[class=name-pic]").select("img").attr("title");
                    Elements userEles = reviewEle.select("div[class=choose-con mt-10]").select("dl");
                    for (Element userEle : userEles) {
                        String dt = userEle.select("dt").text();
                        String value = null;
                        if (dt.contains("购买车型"))
                            value = userEle.select("span[class=font-arial]").text();
                        else if (dt.contains("购买地点") || dt.contains("购买时间") || dt.contains("裸车购买价") || dt.contains("空间") || dt.contains("动力"))
                            value = userEle.select("dd").text();
                        else if (dt.contains("操控") || dt.contains("油耗") || dt.contains("舒适性") || dt.contains("外观") || dt.contains("内饰") || dt.contains("性价比"))
                            value = userEle.select("dd").text();
                        System.out.println("dt = " + dt + "\tvalue = " + value);
                    }

                    Element add_dl = reviewEle.select("dl[class=add-dl]").first();
                    String review = "";
                    if (add_dl != null) {
                        String time = add_dl.select("dt").text();   //追加评论时间
                        String str1 = null, str2 = null, str3 = null, str4 = null, str5 = null;
                        String text = add_dl.text();

                        review += add_dl.select("div[class=text-height]").text();
                        //放弃行驶历程平均耗油保养次数等信息
                    /*
                    Pattern pattern = Pattern.compile("当前行驶里程(.*)公里 当");
                    Matcher matcher = pattern.matcher(add_dl.text());
                    if ( matcher.find() )
                        str1 = matcher.group(1);

                    pattern = Pattern.compile("当前平均油耗(.*)升/百公里 ");
                    matcher = pattern.matcher(add_dl.text());
                    if ( matcher.find() )
                        str2 = matcher.group(1);

                    pattern = Pattern.compile("免费保养(.*)次");
                    matcher = pattern.matcher(add_dl.text());
                    if ( matcher.find() )
                        str3 = matcher.group(1);

                    pattern = Pattern.compile("收费保养(.*)次 共`");
                    matcher = pattern.matcher(add_dl.text());
                    if ( matcher.find() )
                        str4 = matcher.group(1);

                    pattern = Pattern.compile("共花费(.*)元 ");
                    matcher = pattern.matcher(add_dl.text());
                    if ( matcher.find() )
                        str5 = matcher.group(1);

                    System.out.println(add_dl.text());
                    System.out.println("str1 = " + str1 + "\nstr2 = " + str2 + "\nstr3 = " + str3 + "\nstr4 = " + str4 + "\nstr5 = " + str5);*/
                    }
                    review += reviewEle.select("div[class=text-con height-list]").select("div").first().text();
                    System.out.println(review);

                    String support = reviewEle.select("label[class=supportNumber]").text();
                    String browse = reviewEle.select("a[class=orange]").text();
                    System.out.println("support = " + support + "\tbrowse = " + browse);

                }//本页所有评论获取完毕
                link = document.select("a[class=page-item-next]").attr("abs:href");
                if ( link == null )
                    break;
            }while(true);
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    private void saveXML() {
        try {

        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    private Document document(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url)
                    .timeout(50000)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36")
                    .get();
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return document;
    }

    private String formatXML(org.dom4j.Element root) {
        String formatXMLStr = null;
        try {
            SAXReader saxReader = new SAXReader();
            org.dom4j.Document document = saxReader.read(new ByteArrayInputStream(root.asXML().getBytes()));
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            StringWriter writer = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(writer, format);
            xmlWriter.write(document);
            formatXMLStr = writer.toString();
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return formatXMLStr;
    }
}
