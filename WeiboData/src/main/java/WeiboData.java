
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by glacier on 14-9-9.
 */
public class WeiboData {

    private String loginURL = "http://login.weibo.cn/login/";
    private String searchURL = "http://weibo.cn/search/";
    private String username, password;
    private HttpClient httpclient = new DefaultHttpClient();
    private WeiboFormat format = new WeiboFormat();
    private String userID = "";
    private int userPageCount = 0;
    private HashSet<String> userSet = new HashSet<String>();
    private static PrintStream ps;
    public static void main(String[] args) throws Exception{
    	ps = new PrintStream(new FileOutputStream("system.log")); 
        System.setOut(ps); 
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println(dateFormat.format(new Date()));
        WeiboData obj = new WeiboData();
        //obj.getSearchWeibo("java", 2);
        String weiboURL = "http://weibo.cn/drsmile";
        obj.getUserAll(weiboURL, true, 1, 1);
        System.out.println("抓取过程结束");
        System.out.println(dateFormat.format(new Date()));
    }

    public WeiboData() {
        getUserPass();
        login();
    }

    public void getUserPass( ) {
        try {
            List<String> userpassList = FileUtils.readLines(new File("userpass.temp"));
            String userpass = userpassList.remove(0);
            String[] temp = userpass.split(",");
            String username = temp[0], password = temp[1];
            this.username = username;
            this.password = password;
            userpassList.add(userpass);
            String usernameText = "";
            for ( String usernameLine:userpassList ) {
                usernameText += "\n" + usernameLine;
            }
            usernameText = usernameText.substring(1);
            FileUtils.writeStringToFile(new File("userpass.temp"), usernameText);
            System.out.println("获取新用户准备登陆 user: " + this.username);
        }catch(Exception e) {
        	e.printStackTrace(ps);
        }
    }

    private void login() {
        try {
            System.out.println("正在登陆...");
            Document Doc = Jsoup.connect(loginURL).timeout(50000)
                    .userAgent("Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .get();

            String loginBackURL = Doc.select("input[name=backURL]").attr("value");
            String loginBackTitle = Doc.select("input[name=backTitle]").attr("value");
            String loginVK = Doc.select("input[name=vk]").attr("value");
            String loginSubmit = Doc.select("input[name=submit]").attr("value");

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("mobile", username));
            nvps.add(new BasicNameValuePair("password_"+loginVK.substring(0, 4), password));
            nvps.add(new BasicNameValuePair("remember", "on"));
            nvps.add(new BasicNameValuePair("backURL", loginBackURL));
            nvps.add(new BasicNameValuePair("backTitle", loginBackTitle));
            nvps.add(new BasicNameValuePair("tryCount", ""));
            nvps.add(new BasicNameValuePair("vk", loginVK));
            nvps.add(new BasicNameValuePair("submit", loginSubmit));

            HttpPost httpost = new HttpPost(loginURL);
            httpost.setEntity(new UrlEncodedFormEntity( nvps, "UTF-8" ));
            HttpResponse response = httpclient.execute(httpost);
            /*String loginHTML = */EntityUtils.toString(response.getEntity());
            String localtionURL = response.getFirstHeader("Location").getValue();
            HttpGet httpget = new HttpGet(localtionURL);
            response = httpclient.execute(httpget);
            userPageCount = 0;
            String LoginSuccessHTML = EntityUtils.toString(response.getEntity());
            Doc = Jsoup.parse(LoginSuccessHTML);
            String idhref = Doc.select("div[class=tip2]").select("a[href]").first().attr("href");
            userID = idhref.substring(idhref.indexOf("/")+1, idhref.lastIndexOf("/"));
        }catch(Exception e) {
        	e.printStackTrace(ps);
        }
    }

    public void getSearchWeibo( String question, int page ) {
        try {
            System.out.println("正在处理微博搜索...");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("keyword", question));
            nvps.add(new BasicNameValuePair("smblog", "搜微博"));

            HttpPost httpost = new HttpPost(searchURL);
            httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            HttpResponse response = httpclient.execute(httpost);
            String searchHTML = EntityUtils.toString(response.getEntity());

            int pageCount = 1;
            List<weiboSearch> weiboList = new ArrayList<weiboSearch>();
            do {
                Document Doc = Jsoup.parse(searchHTML);
                Elements weiboDivs = Doc.select("div[id]");
                for (Element weiboDiv : weiboDivs) {
                    weiboSearch obj = new weiboSearch();
                    Element weiboUser = weiboDiv.select("a[class=nk]").first();
                    Element weiboForward = weiboDiv.select("span[class=cmt]").first();
                    Element weiboSpan = weiboDiv.select("span[class=ctt]").first();
                    if (weiboSpan != null) {
                        if (weiboForward != null) {
                            //System.out.println(weiboUser.text() + weiboForward.text() + weiboSpan.text() + "\n");
                            Element forwardReason = weiboDiv.select("span[class=cmt]").last().parent();
                            obj.setWeiboText(weiboSpan.text());
                            obj.setWeiboSender(weiboUser.text());
                            obj.setSenderURL(weiboUser.attr("href"));
                            obj.setWeiboForward(weiboForward.text());
                            obj.setForwardReason(forwardReason.text());
                        } else {
                            //System.out.println(weiboUser.text() + weiboSpan.text() + "\n");
                            obj.setWeiboText(weiboSpan.text());
                            obj.setWeiboSender(weiboUser.text());
                            obj.setSenderURL(weiboUser.attr("href"));
                            obj.setWeiboForward("");
                            obj.setForwardReason("");
                        }
                        weiboList.add(obj);
                    }
                }
                format.saveWeiboSearch(weiboList, pageCount, question);
                weiboList.clear();
                searchURL = getSearchNext(searchURL, page);
                pageCount = Integer.parseInt(searchURL.substring(searchURL.indexOf("page=")+5));
                //System.out.println(pageCount);
                if ( pageCount > page )
                    break;
                HttpGet httpget = new HttpGet(searchURL);
                response = httpclient.execute(httpget);
                searchHTML = EntityUtils.toString(response.getEntity());
            }while(searchURL != null);
        }catch(Exception e) {
        	e.printStackTrace(ps);
        }
    }

    private String getSearchNext( String pageURL, int maxPage ) {
        String nextURL = "";
        if ( !pageURL.contains("?") ) {
            nextURL = pageURL + "?page=2";
        }
        else {
            String thisPage = pageURL.substring(pageURL.indexOf("page=")+5);
            if ( Integer.parseInt(thisPage) >= maxPage )
                nextURL = null;
            else
                nextURL = pageURL.substring(0,pageURL.indexOf("?")) + "?page=" + (Integer.parseInt(thisPage)+1);
        }
       // System.out.println(nextURL);
        return nextURL;
    }

    public List<weiboFans> getFansList( String userURL ) throws IOException {
    	Document countDoc = null;
        try {
            System.out.println("正在获取粉丝列表...");
            HttpGet httpget = new HttpGet(userURL);
            HttpResponse response = httpclient.execute(httpget);
            String userHTML = EntityUtils.toString(response.getEntity());

            Document Doc = Jsoup.parse(userHTML);
            countDoc = Doc;
            Element fansDiv = Doc.select("div[class=tip2]").first();
            Elements fansDivaTags = fansDiv.select("a[href]");
            String fansURL = "";
            for ( Element atag:fansDivaTags ) {
                if ( atag.text().contains("粉丝") ) {
                    fansURL = "http://weibo.cn" + atag.attr("href");
                    break;
                }
            }
            int pageCount = 1;
            List<weiboFans> fansList = new ArrayList<weiboFans>();
            do {
                httpget = new HttpGet(fansURL);
                response = httpclient.execute(httpget);
                String fansHTML = EntityUtils.toString(response.getEntity());
                Doc = Jsoup.parse(fansHTML);
                Elements tableEles = Doc.select("table");
                if ( tableEles.size() == 0 ) {
                	format.saveFansList(fansList, userURL, "fans");
                    return fansList;
                }
                for (Element tableEle : tableEles) {
                    Element userTag = tableEle.select("td").last().select("a[href]").first();
                    weiboFans obj = new weiboFans();
                    obj.setFansName(userTag.text());
                    obj.setFansURL(userTag.attr("href"));
                    fansList.add(obj);
                }
                fansURL = getSearchNext(fansURL, 20);
            }while(fansURL != null);
            format.saveFansList(fansList, userURL, "fans");
            return fansList;
        }catch(NullPointerException e) {
        	e.printStackTrace(ps);
        	getUserPass();
        	login();
        	return getFansList(userURL);
        	//FileUtils.writeStringToFile(new File(System.currentTimeMillis() + "_fans.html"), countDoc.toString());
        }catch(Exception e) {
        	e.printStackTrace(ps);
        }
        return null;
    }

    public void getUserWeibo( String userURL, String lastDate ) {
        try {
            HttpGet httpget = new HttpGet(userURL);
            HttpResponse response = httpclient.execute(httpget);
            String userHTML = EntityUtils.toString(response.getEntity());

            Document Doc = Jsoup.parse(userHTML);
            Element maxPageEle = Doc.select("input[type=hidden]").first();
            String maxPage = "";
            if ( maxPageEle != null )
            	maxPage = maxPageEle.attr("value");
            else
            	maxPage = "1";
            getUserWeibo(userURL, Integer.parseInt(maxPage), lastDate);
        }catch(Exception e) {
        	e.printStackTrace(ps);
        }
    }
    public void getUserWeibo( String userURL, int page, String lastDate ) {
        Document Doc = null;
        Element errorEle = null;
        Elements errorEles = null;
        try {
            System.out.println("正在获取用户微博...");
            List<weiboSearch> weiboList = new ArrayList<weiboSearch>();
            int pageCount = 1;
            breakDate:
            do {
                userPageCount ++;
                if ( userPageCount >= 1000 ) {
                    getUserPass();
                    login();
                }
                System.out.println("获取到微博第 "+pageCount+" 页 / " + page + " 页\t当前用户抓取到第 " + userPageCount + " 页");
                HttpGet httpget = new HttpGet(userURL);
                HttpResponse response = httpclient.execute(httpget);
                String userHTML = EntityUtils.toString(response.getEntity());

                Doc = Jsoup.parse(userHTML);
                Element maxPageEle = Doc.select("input[type=hidden]").first();
                String maxPage = "";
                if ( maxPageEle != null )
                    maxPage = maxPageEle.attr("value");
                else
                    maxPage = "1";
                String title = "";
                String sender = "";
                try {
	                errorEles = Doc.select("title");
	                title = Doc.select("title").text();
	                sender = title.substring(0, title.lastIndexOf("的"));
                }catch( StringIndexOutOfBoundsException e ) {
                	System.out.println("出现StringIndexOutOfBoundsException\tline: 294");
                	System.out.println("第 " + pageCount + " 页被掠过");
                	getUserPass();
                	login();
                	continue;
                }
                Elements weiboDivs = Doc.select("div[id]");
                for (Element weiboDiv : weiboDivs) {
                    try {
                        weiboSearch obj = new weiboSearch();
                        Element weiboText = weiboDiv.select("span[class=ctt]").first();
                        if ( weiboText == null )
                        	continue;
                        obj.setWeiboText(weiboText.text());
                        obj.setSenderURL(userURL);
                        obj.setWeiboSender(sender);
                        String imageURL = "";
                        String likeCount = "", forwardCount = "", commentCount = "";
                        String weiboFrom = "", weiboDate;
                        Elements imageEles = weiboDiv.select("a[href]");
                        if (imageEles.size() > 0) {
                            for (Element imageEle : imageEles) {
                            	try {
	                                String eleText = imageEle.text();
	                                if (eleText.contains("原图")) {
	                                    imageURL = imageEle.attr("href");
	                                } else if (eleText.contains("赞")) {
	                                    likeCount = eleText.substring(eleText.indexOf('[') + 1, eleText.indexOf(']'));
	                                } else if (eleText.contains("转发")) {
	                                    forwardCount = eleText.substring(eleText.indexOf('[') + 1, eleText.indexOf(']'));
	                                } else if (eleText.contains("评论")) {
	                                    commentCount = eleText.substring(eleText.indexOf('[') + 1, eleText.indexOf(']'));
	                                }
                            	}catch(StringIndexOutOfBoundsException e){
                                	System.out.println("出现StringIndexOutOfBoundsException，可以弃之不理\tline: 317");
                                }
                            }
                            obj.setWeiboImage(imageURL);
                            obj.setWeiboLikeCount(likeCount);
                            obj.setWeiboForwardCount(forwardCount);
                            obj.setWeiboCommentCount(commentCount);
                        }
                        Element fromDate = weiboDiv.select("span[class=ct]").first();
                        String fromDateText = fromDate.text();
                        weiboDate = fromDateText.substring(0, fromDateText.indexOf("来自"));
                        weiboFrom = fromDateText.substring(fromDateText.indexOf("来自"));
                        obj.setWeiboDate(weiboDate);
                        obj.setWeiboFrom(weiboFrom);

                        Element weiboForward = weiboDiv.select("span[class=cmt]").first();
                        if (weiboForward != null) {
                            Element forwardReason = weiboDiv.select("span[class=cmt]").last().parent();
                            String forwardReasonText = forwardReason.text();
                            obj.setWeiboForward(weiboForward.text());
                            obj.setForwardReason(forwardReasonText.substring(0, forwardReasonText.indexOf("赞[")));
                        } else {
                            obj.setWeiboForward("");
                            obj.setForwardReason("");
                        }
                        if (obj != null)
                            weiboList.add(obj);
                        if ( weiboDate.contains(lastDate) )
                            break breakDate;
                    }catch(NullPointerException e) {
                    	e.printStackTrace(ps);
                    }catch (Exception e) {
                    	e.printStackTrace(ps);
                    }
                }
                format.saveUserWeibo(weiboList, sender, userURL, pageCount);
                weiboList.clear();
                userURL = getSearchNext(userURL, page);
                pageCount = Integer.parseInt(userURL.substring(userURL.indexOf("page=")+5));
                if ( pageCount > page || pageCount >= Integer.parseInt(maxPage)  )
                    break;
            }while(userURL != null);
        }catch(Exception e) {
        	e.printStackTrace(ps);
            //System.out.println(Doc);
        }
    }

    public List<weiboFans> getWatchList( String userURL ) throws IOException {
    	Document countDoc = null;
        try {
            System.out.println("正在获取关注列表...");
            HttpGet httpget = new HttpGet(userURL);
            HttpResponse response = httpclient.execute(httpget);
            String userHTML = EntityUtils.toString(response.getEntity());

            Document Doc = Jsoup.parse(userHTML);
            countDoc = Doc;
            Element watchDiv = Doc.select("div[class=tip2]").first();
            Elements watchDivaTags = watchDiv.select("a[href]");
            String watchURL = "";
            for ( Element atag:watchDivaTags ) {
                if ( atag.text().contains("关注") ) {
                    watchURL = "http://weibo.cn" + atag.attr("href");
                    break;
                }
            }
            List<weiboFans> watchList = new ArrayList<weiboFans>();
            do {
                httpget = new HttpGet(watchURL);
                response = httpclient.execute(httpget);
                String watchHTML = EntityUtils.toString(response.getEntity());
                Doc = Jsoup.parse(watchHTML);
                Elements tableEles = Doc.select("table");
                if ( tableEles.size() == 0 ) {
                	format.saveFansList(watchList, userURL, "watch");
                    return watchList;
                }
                for (Element tableEle : tableEles) {
                    Element userTag = tableEle.select("td").last().select("a[href]").first();
                    weiboFans obj = new weiboFans();
                    obj.setFansName(userTag.text());
                    obj.setFansURL(userTag.attr("href"));
                    watchList.add(obj);
                }
                watchURL = getSearchNext(watchURL, 20);
            }while(watchURL != null);
            format.saveFansList(watchList, userURL, "watch");
            return watchList;
        }catch(NullPointerException e) {
        	e.printStackTrace(ps);
        	getUserPass();
        	login();
        	return getWatchList(userURL);
        	//FileUtils.writeStringToFile(new File(System.currentTimeMillis() + "_watch.html"), countDoc.toString());
        }catch(Exception e) {
        	e.printStackTrace(ps);
        }
        return null;
    }

    public void getUserInfo( String userURL ) throws IOException {
    	Document countDoc = null;
        try {
            System.out.println("正在获取用户信息...");
            userInfo obj = new userInfo();
            HttpGet httpget = new HttpGet(userURL);
            HttpResponse response = httpclient.execute(httpget);
            String userHTML = EntityUtils.toString(response.getEntity());
            Document Doc = Jsoup.parse(userHTML);

            countDoc = Doc;
            Element countEle = Doc.select("div[class=tip2]").first(); 	//div class=tip2
            String countTemp = countEle.select("span").first().text();
            String weiboCount = countTemp.substring(countTemp.indexOf("[")+1, countTemp.indexOf("]"));
            countTemp = countEle.select("a[href]").get(0).text();
            String watchCount = countTemp.substring(countTemp.indexOf("[")+1, countTemp.indexOf("]"));
            countTemp = countEle.select("a[href]").get(1).text();
            String fansCount = countTemp.substring(countTemp.indexOf("[")+1, countTemp.indexOf("]"));
            obj.setWeiboCount(weiboCount);
            obj.setFansCount(fansCount);
            obj.setWatchCount(watchCount);


            Element infoDiv = Doc.select("div[class=ut]").first();
            String infoURL = "";
            Elements atags = infoDiv.select("a[href]");
            for (Element atag:atags) {
                if ( atag.text().contains("资料") ) {
                    infoURL = "https://weibo.cn" + atag.attr("href");
                    break;
                }
            }

            httpget = new HttpGet(infoURL);
            response = httpclient.execute(httpget);
            String infoHTML = EntityUtils.toString(response.getEntity());
            //String infoHTML = FileUtils.readFileToString(new File("test.html"));
            Doc = Jsoup.parse(infoHTML);

            Element imageEle = Doc.select("img[alt=头像]").first();
            obj.setUserPicURL(imageEle.attr("src"));
            Element infoEle = Doc.select("div[class=tip]").first().nextElementSibling();
            String infoText = infoEle.html().replaceAll("<br />", "");
            infoText = infoText.replaceAll("：", ":");
        	String infoTagText = "";
            if ( infoText.contains("标签") ) {
            	infoText = infoText.substring(0, infoText.indexOf("标签:")-1);
            	String infoTagURL = "";
            	Elements infoTagEles = infoEle.select("a[href]");
            	for ( Element infoTagEle:infoTagEles ) {
            		if ( infoTagEle.text().equals("更多>>") ) {
            			infoTagURL = "http://weibo.cn" + infoTagEle.attr("href");
            		}
            	}

            	httpget = new HttpGet(infoTagURL);
            	response = httpclient.execute(httpget);
            	String tagHTML = EntityUtils.toString(response.getEntity());
            	Doc = Jsoup.parse(tagHTML);
            	Elements infoTags = Doc.select("div[class=c]").get(2).select("a[href]");
            	for ( Element infoTag:infoTags ) {
            		infoTagText += "," + infoTag.text();
            	}
            	infoTagText = infoTagText.substring(1);
            }
            obj.setUserTag(infoTagText);
            obj.setUserName("");
            obj.setConfirmInfo("");
            obj.setUserSex("");
            obj.setUserAddr("");
            obj.setUserBirth("");
            obj.setUserResume("");

            String[] infoArry = infoText.split("\n");
            for ( String infoLine:infoArry ) {
                String infoType = infoLine.substring(0, infoLine.indexOf(":"));
                String infoData = infoLine.substring(infoLine.indexOf(":")+1);
                switch (toInfoEnum(infoType)) { 	//除此之外 比如性取向
                    case 昵称:obj.setUserName(infoData); break;
                    case 认证:obj.setConfirmInfo(infoData); break;
                    case 性别:obj.setUserSex(infoData); break;
                    case 地区:obj.setUserAddr(infoData); break;
                    case 生日:obj.setUserBirth(infoData); break;
                    case 简介:obj.setUserResume(infoData); break;
                    default:break;
                }
            }
            format.saveUserInfo(obj, userURL);
        }catch(NullPointerException e) {
        	e.printStackTrace(ps);
        	getUserPass();
        	login();
        	getUserInfo(userURL);
        	//FileUtils.writeStringToFile(new File(System.currentTimeMillis() + "_info.html"), countDoc.toString());
        }catch(Exception e) {
        	e.printStackTrace(ps);
        }
    }

    public void getUserAll( String userURL, boolean isRecursion, int recursionCount, int targetCount ) {
        try {
    		System.out.println("开始抓取: " + userURL);
            getUserInfo(userURL);
            getUserWeibo(userURL, "2012");
            List<weiboFans> fansList = getFansList(userURL);
            List<weiboFans> watchList = getWatchList(userURL);
            format.saveXML();
            format = new WeiboFormat();
            userSet.add(userURL);
            if ( isRecursion ) {
                for (weiboFans watchUser : watchList) {
                	if ( !userSet.contains(watchUser)  ) {
                		System.out.println("接下来开始抓取: " + watchUser.getFansURL());
                		if ( recursionCount >= targetCount )
                			getUserAll(watchUser.getFansURL(), false, recursionCount+1, targetCount);
                		else
                			getUserAll(watchUser.getFansURL(), true, recursionCount+1, targetCount);
                	}
                }
            }
        }catch(Exception e) {
        	e.printStackTrace(ps);
        }
    }
    private enum infoEnum {
        昵称,认证,性别,地区,生日,简介,标签,认证信息,性取向,达人,感情状况,血型,个性域名
    }
    private infoEnum toInfoEnum( String infoType ) {
        return WeiboData.infoEnum.valueOf(infoType);
    }
}
