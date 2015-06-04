
package com.glacier.spider;

import org.apache.http.HttpHost;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by glacier on 15-6-1.
 */
public class GetProxy {

    private static Logger logger = Logger.getLogger(GetProxy.class.getName());
    private static final String seedURL = "http://www.youdaili.net/Daili/http/";
    private static HashMap<String, Integer> proxyMap = new HashMap<String, Integer>();
    private static ArrayList<String> proxyList = new ArrayList<String>();

    public void initProxy() {
        Downloader downloader = new Downloader();

        Document document = downloader.document(seedURL);
        Element element = document.select("ul[class=newslist_line]").select("li").select("a[href]").first();
        document = downloader.document(element.attr("abs:href"));
        element = document.select("div[class=newsdetail_cont]").select("p").first();

        String[] proxy_array = null;
        if ( element.html().contains("<br />") ) {
            proxy_array = element.html().split("<br />");
        }
        else if ( element.html().contains("<br>") ) {
            proxy_array = element.html().split("<br>");
        }
        for ( String proxy : proxy_array ) {
            proxy.replace(" ", "");
            logger.info("[代理] - 自动获取到代理 " + proxy);
            String remark = proxy.substring(proxy.indexOf("@"));
            proxy = proxy.replace(remark, "");
            String proxy_ip = proxy.split(":")[0].replace(" ", "");
            String proxy_port = proxy.split(":")[1];

            proxyMap.put(proxy_ip, Integer.parseInt(proxy_port));
        }
    }

    public HttpHost getProxy() {
        HttpHost proxy = null;
        for (Iterator iterator = proxyMap.keySet().iterator(); iterator.hasNext(); ) {
            String proxy_ip = (String)iterator.next();
            Integer proxy_port = proxyMap.get(proxy_ip);
            if ( !proxyList.contains(proxy_ip) ) {
                proxy = new HttpHost(proxy_ip, proxy_port, "http");
                proxyList.add(proxy_ip);
            }
            if ( proxyList.size() == proxyMap.size() ) {
                proxyList.clear();
            }
            if ( proxy != null ) {
                break;
            }
        }
        logger.info("[Proxy] - 切换代理 " + proxy);
        return proxy;
    }

}
