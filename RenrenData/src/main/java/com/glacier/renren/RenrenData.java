package com.glacier.renren;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by glacier on 14-9-18.
 */
public class RenrenData {

    private String username, password;
    private HttpClient httpClient = new DefaultHttpClient();
    private String loginPage = "http://3g.renren.com/login.do?autoLogin=true&";

    public static void main(String[] args) throws Exception{
        RenrenData obj = new RenrenData();
        //obj.login();
        String renren = FileUtils.readFileToString(new File("geren.html"));
        Document doc = Jsoup.parse(renren);
        obj.getUserInfo(doc);
        //FileUtils.writeStringToFile(new File("userinfo.html"), doc.toString());

    }

    public void login() {
        try {
            getUserPass();
            HttpGet httpGet = getHttpGet(loginPage);
            HttpResponse response = httpClient.execute(httpGet);
            String loginHTML = EntityUtils.toString(response.getEntity());
            Document Doc = Jsoup.parse(loginHTML);
            Element formEle = Doc.select("form").first();
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Elements hiddenEles = formEle.select("input[type=hidden]");
            for ( Element hiddenEle:hiddenEles ) {
                nvps.add(new BasicNameValuePair(hiddenEle.attr("name"), hiddenEle.attr("value")));
            }

            nvps.add(new BasicNameValuePair("email", username));
            nvps.add(new BasicNameValuePair("password", password));
            nvps.add(new BasicNameValuePair("login", "登陆"));

            HttpPost httpPost = getHttpPost(formEle.attr("action"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            response = httpClient.execute(httpPost);
            String homePage = EntityUtils.toString(response.getEntity());
            Doc = Jsoup.parse(homePage);
            httpGet = getHttpGet(Doc.select("a[href]").first().attr("href"));
            response = httpClient.execute(httpGet);
            String homePageHTML = EntityUtils.toString(response.getEntity());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTopicURL( Document Doc, String topic ) {
        String topicURL = null;
        try {
            Elements secnavDiv = Doc.select("div[class=sec nav]").first().select("a[href]");
            for ( Element topicEle:secnavDiv ) {
                if ( topicEle.text().equals(topic) ) {
                    topicURL = topicEle.attr("href");
                    break;
                }
            }
            if ( topicURL == null ) {
                secnavDiv = Doc.select("div[class=list]").first().select("a[href]");
                for ( Element topicEle:secnavDiv ) {
                    if ( topicEle.text().equals(topic) ) {
                        topicURL = topicEle.attr("href");
                        break;
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return topicURL;
    }

    public String getNextPage( Document Doc ) {
        String nextURL = null;
        try {
            Elements nextPageEles = Doc.select("div[class=l]").select("a[href]");
            for ( Element nextPageEle:nextPageEles ) {
                if ( nextPageEle.text().equals("下一页") ) {
                    nextURL = nextPageEle.attr("href");
                    break;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return nextURL;
    }

    public void getUserPass() {
        try {
            String userPass = FileUtils.readFileToString(new File("userpass.temp"));
            String[] userPassArry = userPass.split(",");
            username = userPassArry[0];
            password = userPassArry[1];
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public HttpGet getHttpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        //httpGet.setHeader("Accept", "Accept text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        //httpGet.setHeader("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
        //httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        //httpGet.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
        //httpGet.setHeader("refer", "http://3g.renren.com/login.do?autoLogin=true&");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
        return httpGet;
    }

    public HttpPost getHttpPost(String url) {
        HttpPost httpPost = new HttpPost(url);

        //httpPost.setHeader("Accept", "Accept text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        //httpPost.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
        //httpPost.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        //httpPost.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
        //httpPost.setHeader("refer", "http://3g.renren.com/login.do?autoLogin=true&");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
        return httpPost;
    }

    public Document getDocument(String url) {
        Document doc = null;
        try {
            HttpGet httpGet = getHttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            String pageHTML = EntityUtils.toString(response.getEntity());
            doc = Jsoup.parse(pageHTML);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    public String getTbodyTopic( Document doc, String topic ) {
        String topicURL = null;
        try {
            Elements topicDiv = doc.select("tbody").select("a[href]");
            for ( Element topicTag:topicDiv ) {
                if ( topicTag.text().equals(topic) ) {
                    topicURL = topicTag.attr("href");
                    break;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return topicURL;
    }

    public void getUserInfo(Document doc) {
        HashMap<String,HashMap<String,String>> userInfoMap = new HashMap<String, HashMap<String,String>>();
        try {
            String userInfoLink = getTbodyTopic(doc, "详细资料");
            System.out.println(userInfoLink);
            Document userInfoDoc = Jsoup.parse(FileUtils.readFileToString(new File("userinfo.html")));
            //Document userInfoDoc = getDocument(userInfoLink);
            Elements userInfoEles = userInfoDoc.select("div[class=list]");
            String reg = "[\u4e00-\u9fa5]";
            Pattern pat = Pattern.compile(reg);
            Matcher mat = null;
            for ( Element userInfoEle:userInfoEles ) {
                String infoType = userInfoEle.select("div[class=t]").text();
                Element infoEle = userInfoEle.select("div").last();
                String[] lineArry = infoEle.toString().split("\n");
                HashMap<String,String> infoTypeMap = new HashMap<String, String>();
                for ( String infoLine:lineArry ) {
                    mat = pat.matcher(infoLine);
                    if ( !mat.find() && !infoLine.contains("MSN") && !infoLine.contains("QQ") )
                        continue;
                    if ( infoLine.contains("<") )
                        infoLine = infoLine.replaceAll(infoLine.substring(infoLine.indexOf('<'), infoLine.lastIndexOf('>')+1 ), "");
                    while( infoLine.contains(" ") )
                        infoLine = infoLine.replaceAll(" ", "");
                    if ( infoLine.contains("：") )
                        infoTypeMap.put(infoLine.substring(0,infoLine.indexOf('：')),infoLine.substring(infoLine.indexOf('：')+1));
                    else if ( infoLine.contains("座") )
                        infoTypeMap.put("星座", infoLine);
                }
                userInfoMap.put(infoType, infoTypeMap);
            }
            for ( String key:userInfoMap.keySet() ) {
                System.out.println(key);
                for ( String keyc:userInfoMap.get(key).keySet() ) {
                    System.out.println(keyc + " - " + userInfoMap.get(key).get(keyc));
                }
            }
            new RenrenFormat().saveUserInfo(userInfoMap);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}




