
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
    public static void main(String[] args) {
        WeiboData obj = new WeiboData("l_ee_hom@msn.cn", "Rlx0825leehom");
        obj.getSearchWeibo("java", 10);
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

            Document Doc = Jsoup.parse(searchHTML);
            Elements weiboDivs = Doc.select("div[id]");
            for (Element weiboDiv:weiboDivs) {
                Element weiboUser = weiboDiv.select("a[class=nk]").first();
                Element weiboForward = weiboDiv.select("span[class=cmt]").first();
                Element weiboSpan = weiboDiv.select("span[class=ctt]").first();
                if ( weiboSpan != null ) {
                    if ( weiboForward != null ) {
                        System.out.println(weiboUser.text() + weiboForward.text() + weiboSpan.text() + "\n");
                        Element forwardReason = weiboDiv.select("span[class=cmt]").last().parent();
                    }
                    else
                        System.out.println();
                        //System.out.println(weiboUser.text() + weiboSpan.text() + "\n");
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
