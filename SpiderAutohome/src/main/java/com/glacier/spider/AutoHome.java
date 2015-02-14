package com.glacier.spider;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

import java.text.SimpleDateFormat;
import java.util.Date;
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
                getCarList(link, type);
            }
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    private void getCarList(String link, String type) {
        try {
            Document document = document(link);
            Elements elements = document.select("div[class=tab-content-item]").first().select("div[class=uibox]");

            logger.info("获取到 " + type + " 汽车 " + elements.size() + " 块");
            int element_count = 0;
            for ( Element element:elements ) {
                element_count ++;
                logger.info("开始获取第 " + element_count + " 块 / " + elements.size() + " 块");
                Elements typeList = element.select("dl");
                for ( Element typeTag:typeList ) {
                    String name1 = typeTag.select("dt").first().text();
                    String name2 = typeTag.select("div[class=h3-tit]").first().text();
                    Elements carList = typeTag.select("ul").select("li");
                    logger.info("当前块有 " + carList.size() + " 辆车");
                    int car_count = 0;
                    for ( Element carTag:carList ) {
                        car_count ++;
                        CarInfo info = new CarInfo();

                        String name3 = carTag.select("h4").text();
                        logger.info("开始获取第 " + car_count + " 辆 / " + carList.size() + " 辆" + "\tname1 = " + name1 + "\tname2 = " + name2 + "\tname3 = " + name3);
                        Elements temps = carTag.select("a[href]");
                        String url = null;
                        for ( Element temp:temps ) {
                            if ( temp.text().contains("口碑") ) {
                                url = temp.attr("abs:href");
                                break;
                            }
                        }
                        info.car_type = type;
                        info.type_link = link;

                        info.source = url;
                        info.name1 = name1;
                        info.name2 = name2;
                        info.name3 = name3;

                        //getCarInfo("http://k.autohome.com.cn/3217/#pvareaid=103459", info);
                        getCarInfo(url, info);
                        logger.info("正在保存文件..");
                        saveXML(info);
                        logger.info("----------------------又一个搞定啦-----------------------");
                    }
                }
            }

        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    private void getCarInfo(String link, CarInfo info) {
        try {
            logger.info("获取车辆 " + link + " 的信息中..");
            Document document = document(link);

            logger.info("开始获取车辆基础信息");
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

            info.score = score;
            info.peopleNum = peopleNum;
            info.rank = rank;
            info.price = price;
            info.dealerPriceBox = dealerPriceBox;
            info.total = total;

            logger.info("车辆基础信息获取完毕");
            //-----------获取完毕

            //-----------耗油情况
            logger.info("开始获取车辆耗油情况");
            Elements petrolEles = document.select("div[class=tab-content]").first().select("div[style]");
            for ( Element petrolEle:petrolEles ) {
                Petrol petrol = new Petrol();
                String petrol_type = petrolEle.attr("class");
                if (petrol_type.contains("manual-cont"))
                    petrol_type = "手动";
                else if (petrol_type.contains("auto-cont"))
                    petrol_type = "自动";
                String petrol_count = petrolEle.select("p[class=font-14]").text();

                petrol.petrol_type = petrol_type;
                petrol.petrol_count = petrol_count;

                Elements temps = petrolEle.select("ul[class=manual-cont-left]").select("li");
                for (Element temp : temps) {
                    String name1 = temp.select("span[class=tit-txt]").remove().text();
                    String name2 = temp.select("span[class=c999]").remove().text();
                    String name3 = temp.text();

                    petrol.petrols.add(name1 + "," + name2 + "," + name3);
                }
                //---------耗油情况完毕
                info.petrolList.add(petrol);
            }
            logger.info("车辆耗油情况获取完毕");
            //---------所有车型评分情况
            logger.info("开始获取车型评分情况");
            Elements carScores = document.getElementById("specListUL").select("li[data-value]");
            for ( Element carScore:carScores ) {
                Type type = new Type();
                String emiss_title = carScore.select("div[class=emiss-title]").text();
                String emiss_price = carScore.select("div[class=emiss-price]").select("a").text();
                String emiss_fen = carScore.select("div[class=emiss-fen]").select("a").text();
                String emiss_ren = carScore.select("div[class=emiss-ren]").select("a").text();
                if ( emiss_fen.length() == 0 )
                    emiss_fen = " ";
                if ( emiss_ren.length() == 0 )
                    emiss_ren = " ";
                type.title = emiss_title;
                type.price = emiss_price;
                type.fen = emiss_fen;
                type.ren = emiss_ren;
                info.typeList.add(type);
            }
            logger.info("车型评分情况获取完毕");
            //---------所有车型评分情况完毕

            //---------获取所有评论内容
            getAllReview(link, info);

        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    private void getAllReview(String link, CarInfo info) {
        try {
            int count = 0;
            String total = "0";
            do {    //获取一页的评论信息
                count ++;
                logger.info("正在获取评论情况 page = " + count + "\tlink = " + link + "\ttotal = " + total);
                Document document = document(link);
                Elements reviewEles = document.select("div[class=mouthcon js-koubeidataitembox]");

                int review_count = 0;
                for (Element reviewEle : reviewEles) {  //单个用户的信息
                    review_count ++;
                    logger.info("正在获取第 " + review_count + " 个用户评论  本页共 " + reviewEles.size() + " 名用户");

                    Review reviewObj = new Review();
                    String userID = reviewEle.select("div[class=name-pic]").select("img").attr("title");
                    reviewObj.userID = userID;
                    Elements userEles = reviewEle.select("div[class=choose-con mt-10]").select("dl");
                    for (Element userEle : userEles) {
                        String dt = userEle.select("dt").text();
                        String value = null;
                        if (dt.contains("购买车型"))
                            value = userEle.select("span[class=font-arial]").text();
                        else if (dt.contains("购买地点") || dt.contains("购买时间") || dt.contains("裸车购买价") || dt.contains("空间") || dt.contains("动力"))
                            value = userEle.select("dd").text();
                        else if (dt.contains("操控") || dt.contains("舒适性") || dt.contains("外观") || dt.contains("内饰") || dt.contains("性价比"))
                            value = userEle.select("dd").text();
                        else
                            continue;
                        reviewObj.reviewMap.put(dt, value);
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
                    review += reviewEle.getElementsByClass("text-cont").text();

                    String support = reviewEle.select("label[class=supportNumber]").text();
                    String browse = reviewEle.select("a[class=orange]").text();


                    reviewObj.review = review;
                    reviewObj.browse = browse;

                    reviewObj.support = support;

                    info.reviewList.add(reviewObj);
                }//本页所有评论获取完毕
                total = document.select("a[class=page-item-last]").attr("abs:href");
                if ( total == null || total.length() == 0 )
                    total = "null";
                link = document.select("a[class=page-item-next]").attr("abs:href");
                if ( link == null || link.length() == 0 )
                    break;
            }while(true);
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    private void saveXML(CarInfo info) {
        try {

            org.dom4j.Document document = DocumentHelper.createDocument();
            org.dom4j.Element root = document.addElement("root");

            org.dom4j.Element sourceEle = root.addElement("source");
            sourceEle.addText(info.source);
            org.dom4j.Element crawlDate = root.addElement("crawl_date");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            crawlDate.addText(format.format(new Date()));

            org.dom4j.Element typeEle = root.addElement("car_type");
            typeEle.addAttribute("value", info.car_type);
            typeEle.addAttribute("link", info.type_link);

            org.dom4j.Element classifyEle = root.addElement("car_classify");
            classifyEle.addAttribute("brand_name", info.name1);
            classifyEle.addAttribute("detail_name", info.name2);
            classifyEle.addAttribute("model_name", info.name3);

            org.dom4j.Element infoEle = root.addElement("car_info");
            org.dom4j.Element scoreEle = infoEle.addElement("car_score");
            scoreEle.addText(info.score);
            scoreEle.addAttribute("people_count", info.peopleNum);

            org.dom4j.Element rankEle = infoEle.addElement("car_rank");
            rankEle.addText(info.rank);
            rankEle.addAttribute("total_count", info.total);

            org.dom4j.Element priceEle = infoEle.addElement("car_price");
            priceEle.addAttribute("official_price", info.price);
            priceEle.addAttribute("users_purchase", info.dealerPriceBox);

            org.dom4j.Element petrolEle = infoEle.addElement("car_petrol");
            for ( Petrol petrol:info.petrolList ) {
                org.dom4j.Element petrolInfo = petrolEle.addElement("petrol_info");
                petrolInfo.addAttribute("petrol_type", petrol.petrol_type);
                petrolInfo.addAttribute("petrol_count", petrol.petrol_count);
                for ( String line:petrol.petrols ) {
                    String[] array = line.split(",");
                    org.dom4j.Element valuesEle = petrolInfo.addElement("values");
                    valuesEle.addAttribute("consume", array[0]);
                    valuesEle.addAttribute("proportion", array[1]);
                    valuesEle.addAttribute("number", array[2]);
                }
            }

            org.dom4j.Element otherEle = infoEle.addElement("other_type");
            for ( Type type:info.typeList ) {
                org.dom4j.Element valuesEle = otherEle.addElement("values");
                valuesEle.addAttribute("car_type", type.title);
                valuesEle.addAttribute("car_price", type.price);
                valuesEle.addAttribute("car_score", type.fen);
                valuesEle.addAttribute("people_count", type.ren);
            }

            org.dom4j.Element reviewEle = root.addElement("car_review");
            for ( Review review:info.reviewList ) {
                org.dom4j.Element review_user = reviewEle.addElement("review_user");
                review_user.addAttribute("userID", review.userID);
                for ( String key:review.reviewMap.keySet() ) {
                    org.dom4j.Element review_table = review_user.addElement("review_table");
                    review_table.addAttribute("key", key);
                    review_table.addAttribute("value", review.reviewMap.get(key));
                }
                org.dom4j.Element review_text = review_user.addElement("review_text");
                review_text.addText(review.review);
                review_text.addAttribute("support", review.support);
                review_text.addAttribute("browse", review.browse);
            }

            String xml = formatXML(root);

            File savePath = new File(new File("Data"), System.currentTimeMillis() + ".xml");
            FileUtils.writeStringToFile(savePath, xml, "utf-8");

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
