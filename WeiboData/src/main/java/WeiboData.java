
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by glacier on 14-9-9.
 */
public class WeiboData {

    private String loginURL = "http://login.weibo.cn/login/";
    private String searchURL = "http://weibo.cn/search/";
    private String username, password;
    private HttpClient httpclient = new DefaultHttpClient();
    public static void main(String[] args) throws Exception{
        String userpass = FileUtils.readFileToString(new File("userpass.temp"));
        String[] temp = userpass.split(",");
        String username = temp[0], password = temp[1];
        WeiboData obj = new WeiboData(username, password);
        obj.getSearchWeibo("java", 2);
        //new WeiboFormat().saveWeiboSearch();
    }

    public WeiboData( String username, String password ) {
        this.username = username;
        this.password = password;
        login();
    }

    private void login() {
        try {
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
            /*String LoginSuccessHTML = */EntityUtils.toString(response.getEntity());

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void getSearchWeibo( String question, int page ) {
        try {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("keyword", question));
            nvps.add(new BasicNameValuePair("smblog", "搜微博"));

            HttpPost httpost = new HttpPost(searchURL);
            httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            HttpResponse response = httpclient.execute(httpost);
            String searchHTML = EntityUtils.toString(response.getEntity());

            int pageCount = 1;
            List<weiboSearch> weiboList = new ArrayList<weiboSearch>();
            WeiboFormat format = new WeiboFormat();
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
                format.saveWeiboSearch(weiboList, pageCount);
                weiboList.clear();
                searchURL = getSearchNext(Doc);
                pageCount = Integer.parseInt(searchURL.substring(searchURL.indexOf("page=")+5));
                //System.out.println(pageCount);
                if ( pageCount > page )
                    break;
                HttpGet httpget = new HttpGet(searchURL);
                response = httpclient.execute(httpget);
                searchHTML = EntityUtils.toString(response.getEntity());
            }while(searchURL != null);
            format.saveXML();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String getSearchNext( Document Doc ) {
        Element nextDiv = Doc.getElementById("pagelist");
        Element nextEle = nextDiv.select("a[href]").first();
        if ( nextEle.text().equals("下页") )
            return "http://weibo.cn" + nextEle.attr("href");
        return null;
    }
}
