package com.glacier.proxy;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.googlecode.aviator.AviatorEvaluator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.net.URL;
import java.util.HashMap;

/**
 * Created by glacier on 14-10-27.
 */
public class GetProxy {
    public static String seedURL = null;
    public static void main(String[] args) {
        seedURL = "http://www.pachong.org";

        GetProxy obj = new GetProxy();
        //obj.getProxyList();
        obj.test();
    }

    public void test() {
        try {
            Document document = Jsoup.connect(seedURL).get();
            Elements jsEles = document.select("head").select("script");
            String data = jsEles.last().data();

            String[] scriptVar = data.replaceAll("var ", "").split(";");
            HashMap<String,Integer> ansMap = new HashMap<String, Integer>();
            for ( String var:scriptVar ) {
                String keyStr = var.substring(0,var.indexOf('='));
                String valueStr = var.substring(var.indexOf('=')+1);

                String resultKey = judge(valueStr, ansMap);
                if ( resultKey != null )
                    valueStr = valueStr.replace(resultKey, ansMap.get(resultKey).toString());

                Long value = (Long)AviatorEvaluator.execute(valueStr);
                ansMap.put(keyStr, new Integer(value.intValue()));
            }

            Elements proxyEles = document.select("tr[data-id]");
            for ( Element proxyEle:proxyEles ) {
                String ip = proxyEle.select("td").get(1).text();
                String portStr = proxyEle.select("script").first().data();
                portStr = portStr.substring(portStr.indexOf('('), portStr.lastIndexOf(')')+1);
                String resultKey = judge(portStr, ansMap);
                if ( resultKey != null )
                    portStr = portStr.replace(resultKey, ansMap.get(resultKey).toString());
                Long portLong = (Long)AviatorEvaluator.execute(portStr);
                String port = portLong.toString();
                System.out.println(ip+":"+port);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String judge( String str, HashMap<String,Integer> ansMap ) {
        for ( String key:ansMap.keySet() ) {
            if ( str.contains(key) )
                return key;
        }
        return null;
    }

    public void getProxyList() {
        try {
            URL link = new URL(seedURL);
            WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3_6);

            webClient.getCookieManager().setCookiesEnabled(true);
            webClient.setJavaScriptEnabled(true);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.setTimeout(10000);
            webClient.setThrowExceptionOnScriptError(false);

            HtmlPage htmlPage = webClient.getPage(link);
            System.out.println(htmlPage.getWebResponse().getContentAsString());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
