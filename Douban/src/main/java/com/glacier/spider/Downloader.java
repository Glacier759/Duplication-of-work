package com.glacier.spider;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;

/**
 * Created by glacier on 15-6-4.
 */
public class Downloader {

    private static Logger logger = Logger.getLogger(Downloader.class.getName());
    private String proxy_ip = null;
    private Integer proxy_port = null;
    private HttpHost proxy_host = null;

    public Document document(URL url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = null;
        CloseableHttpResponse response = null;
        RequestConfig requestConfig = null;
        try {

            HttpHost target = new HttpHost(url.getHost(), url.getDefaultPort(), url.getProtocol());
            if ( proxy_ip != null && proxy_port != null ) {
                HttpHost proxy = new HttpHost(proxy_ip, proxy_port, "http");
                requestConfig = RequestConfig.custom()
                        .setProxy(proxy)
                        .setConnectionRequestTimeout(20000).setConnectTimeout(20000)
                        .setSocketTimeout(20000).build();
            }
            else if ( proxy_host != null ) {
                requestConfig = RequestConfig.custom()
                        .setProxy(proxy_host)
                        .setConnectionRequestTimeout(20000).setConnectTimeout(20000)
                        .setSocketTimeout(20000).build();
            }
            else {
                requestConfig = RequestConfig.custom()
                        .setConnectionRequestTimeout(20000).setConnectTimeout(20000)
                        .setSocketTimeout(20000).build();
            }

            httpGet = new HttpGet(url.toString());
            httpGet.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36");
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(target, httpGet);
            if ( response.getStatusLine().getStatusCode() == 403 ) {
                return null;
            }

            String content = EntityUtils.toString(response.getEntity(), "UTF-8");
            Document document = Jsoup.parse(content);
            document.setBaseUri(url.toString());
            return document;
        } catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        } finally {
            try {
                response.close();
                httpClient.close();
            }catch (Exception e) {

            }
        }
        return null;
    }

    public Document document(String url) {
        try {
            return document(new URL(url));
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return null;
    }

    public void setProxy(String proxy_ip, Integer proxy_port) {
        this.proxy_ip = proxy_ip;
        this.proxy_port = proxy_port;
    }

    public void setProxy(HttpHost proxy) {
        this.proxy_host = proxy;
    }

}
