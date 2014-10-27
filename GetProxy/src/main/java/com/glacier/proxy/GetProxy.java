package com.glacier.proxy;

import com.googlecode.aviator.AviatorEvaluator;
import org.apache.http.HttpHost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by glacier on 14-10-27.
 */
public class GetProxy {
    public static String seedURL = null;
    public static void main(String[] args) {
        seedURL = "http://www.pachong.org";

        GetProxy obj = new GetProxy();
        HashMap<String,String> proxyMap = obj.getProxyMap();
        for ( String proxyIP:proxyMap.keySet() ) {
            System.out.println(proxyIP+":"+proxyMap.get(proxyIP));
        }
    }

    public List<HttpHost> getProxyList() {
        try {
            HashMap<String, String> proxyMap = getProxyMap();
            List<HttpHost> proxyList = new ArrayList<HttpHost>();
            for ( String proxyIP:proxyMap.keySet() ) {
                String proxyPort = proxyMap.get(proxyIP);
                HttpHost httpHost = new HttpHost(proxyIP, new Integer(proxyPort));
                proxyList.add(httpHost);
            }
            return proxyList;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String,String> getProxyMap() {
        try {
            Document document = Jsoup.connect(seedURL)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(30000)
                    .get();
            Elements jsEles = document.select("head").select("script");
            String data = jsEles.last().data();

            HashMap<String,Integer> ansMap = getJSVar(data);
            HashMap<String,String> proxyMap = new HashMap<String, String>();
            Elements proxyEles = document.select("tr[data-id]");    //遍历每一个proxy代码块
            for ( Element proxyEle:proxyEles ) {
                String ip = proxyEle.select("td").get(1).text();    //获得proxy ip
                String portStr = proxyEle.select("script").first().data();  //获得端口生成所需的js代码
                portStr = portStr.substring(portStr.indexOf('('), portStr.lastIndexOf(')')+1);
                String resultKey = getSubStr(portStr, ansMap);
                if ( resultKey != null )
                    portStr = portStr.replace(resultKey, ansMap.get(resultKey).toString());     //将变量替换为数字形成表达式
                Long portLong = (Long)AviatorEvaluator.execute(portStr);    //表达式计算获得端口号
                String port = portLong.toString();
                proxyMap.put(ip, port);
            }
            return proxyMap;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //解析获得页面中JS各变量的值
    private HashMap<String,Integer> getJSVar( String data ) {
        String[] scriptVar = data.replaceAll("var ", "").split(";");
        HashMap<String,Integer> ansMap = new HashMap<String, Integer>();
        for ( String var:scriptVar ) {
            String keyStr = var.substring(0,var.indexOf('='));
            String valueStr = var.substring(var.indexOf('=')+1);

            String resultKey = getSubStr(valueStr, ansMap);
            if ( resultKey != null )
                valueStr = valueStr.replace(resultKey, ansMap.get(resultKey).toString());

            Long value = (Long)AviatorEvaluator.execute(valueStr);
            ansMap.put(keyStr, new Integer(value.intValue()));
        }
        return ansMap;
    }

    //判断字符串str中是否存在集合ansMap中的键值  存在返回该键值  不存在返回null
    private String getSubStr( String str, HashMap<String,Integer> ansMap ) {
        for ( String key:ansMap.keySet() ) {
            if ( str.contains(key) )
                return key;
        }
        return null;
    }
}
