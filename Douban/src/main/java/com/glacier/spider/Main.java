package com.glacier.spider;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by glacier on 15-6-4.
 */
public class Main {

    private Downloader downloader = new Downloader();
    private GetProxy getProxy = new GetProxy();

    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Main obj = new Main();
        obj.start();
    }

    public void start( ) {

        getProxy.initProxy();
        HashSet<String> url_filter = new HashSet<String>();

        Document document = null;
        while( (document = downloader.document("http://book.douban.com/tag/")) == null ) {
            System.out.println(document);
            downloader.setProxy(getProxy.getProxy());
        }
        Elements tag_eles = document.select("tbody").select("a[class=tag]");
        HashSet<String> tag_set = new HashSet<String>();
        for ( Element tag_ele : tag_eles ) {
            String href = tag_ele.attr("abs:href");
            href = href.split("\\?")[0] + "book";
            tag_set.add(href);
        }

        for ( String tag_index : tag_set ) {
            try {
                logger.info("[Tag] - " + tag_index);
                while(  (document = downloader.document(tag_index)) == null ) {
                    downloader.setProxy(getProxy.getProxy());
                }
                int page_index = 0;
                do {
                    try {
                        page_index++;
                        Elements book_eles = document.getElementById("content").select("dl").select("dt").select("a[href]");
                        for (Element book_ele : book_eles) {
                            try {
                                String crawl_link = book_ele.attr("abs:href");
                                logger.info("[Book] - " + crawl_link);
                                if ( url_filter.contains(crawl_link) ) {
                                    logger.info("[Filter] - " + crawl_link + " 已被去重");
                                    continue;
                                }
                                url_filter.add(crawl_link);

                                org.dom4j.Document dom = org.dom4j.DocumentHelper.createDocument();
                                org.dom4j.Element dom_root = dom.addElement("root");
                                org.dom4j.Element dom_crawler = dom_root.addElement("crawler_info");
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                dom_crawler.addElement("crawl_date").setText(format.format(new Date()));
                                dom_crawler.addElement("crawl_link").setText(crawl_link);
                                dom_crawler.addElement("crawl_type").setText("douban_book");
                                dom_crawler.addElement("crawl_encode").setText("UTF-8");

                                Document document_book = null;
                                while ( (document_book = downloader.document(crawl_link)) == null ) {
                                    downloader.setProxy(getProxy.getProxy());
                                }
                                try {
                                    Element title_pic_ele = document_book.getElementById("content").select("a[class=nbg]").first();
                                    String book_name = title_pic_ele.attr("title");
                                    String book_img = title_pic_ele.select("img").attr("abs:src");

                                    Elements info_span_eles = document_book.getElementById("info").select("span");
                                    String author = null, press = null, publish_date = null, page_count = null, price = null;
                                    String binding_layout = null, series = null, isbn = null, original_name = null, translator = null;
                                    for (Element info_span_ele : info_span_eles) {
//                    System.out.println(info_span_ele);
                                        if (info_span_ele.children().size() != 0) {
                                            String span_text = info_span_ele.children().text();
                                            if (span_text.contains("作者")) {
                                                author = span_text.replace("作者", "");
                                            } else if (span_text.contains("译者")) {
                                                translator = span_text.replace("译者", "");
                                            }
//                        System.out.println(info_span_ele.children().text());
                                        } else {
                                            String span_text = info_span_ele.nextSibling().toString();
                                            String span_name = info_span_ele.text();
                                            if (span_name.contains("作者")) {
                                            } else if (span_name.contains("译者")) {
                                            } else if (span_name.contains("出版社")) {
                                                press = span_text;
                                            } else if (span_name.contains("原作名")) {
                                                original_name = span_text;
                                            } else if (span_name.contains("出版年")) {
                                                publish_date = span_text;
                                            } else if (span_name.contains("页数")) {
                                                page_count = span_text;
                                            } else if (span_name.contains("定价")) {
                                                price = span_text;
                                            } else if (span_name.contains("装帧")) {
                                                binding_layout = span_text;
                                            } else if (span_name.contains("ISBN")) {
                                                isbn = span_text;
                                            }

//                        System.out.println(info_span_ele.nextSibling());
                                        }
                                        if (info_span_ele.text().contains("丛书")) {
                                            series = info_span_ele.nextElementSibling().text();
                                        }
                                    }

                                    org.dom4j.Element dom_book = dom_root.addElement("book_info");
                                    if (book_name != null) {
                                        dom_book.addElement("book_name").setText(book_name);
                                    }
                                    if (book_img != null) {
                                        dom_book.addElement("book_img").setText(book_img);
                                    }
                                    if (author != null) {
                                        dom_book.addElement("author").setText(author);
                                    }
                                    if (press != null) {
                                        dom_book.addElement("press").setText(press);
                                    }
                                    if (original_name != null) {
                                        dom_book.addElement("original_name").setText(original_name);
                                    }
                                    if (translator != null) {
                                        dom_book.addElement("translator").setText(translator);
                                    }
                                    if (publish_date != null) {
                                        dom_book.addElement("publish_date").setText(publish_date);
                                    }
                                    if (page_count != null) {
                                        dom_book.addElement("page_count").setText(page_count);
                                    }
                                    if (price != null) {
                                        dom_book.addElement("price").setText(price);
                                    }
                                    if (binding_layout != null) {
                                        dom_book.addElement("binding_layout").setText(binding_layout);
                                    }
                                    if (series != null) {
                                        dom_book.addElement("series").setText(series);
                                    }
                                    if (isbn != null) {
                                        dom_book.addElement("isbn").setText(isbn);
                                    }

                                    org.dom4j.Element dom_tag_list = dom_book.addElement("tag_list");
                                    int index = 1;
                                    Elements book_tag_eles = document_book.getElementById("db-tags-section").select("a[href]");
                                    for (Element book_tag_ele : book_tag_eles) {
                                        dom_tag_list.addElement("tag" + index).addText(book_tag_ele.text());
                                        index++;
                                    }
                                    String file_name = System.currentTimeMillis() + ".xml";
                                    FileUtils.writeStringToFile(new File("Data", file_name), formatXML(dom_root));
                                    logger.info("[Save] - " + file_name);
                                }catch (Exception e) {
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    e.printStackTrace(new PrintStream(baos));
                                    logger.error(baos.toString());
                                    logger.error(document_book.toString());
                                }
                            } catch (Exception e) {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                e.printStackTrace(new PrintStream(baos));
                                logger.error(baos.toString());
                            }
                        }
                    }catch (Exception e) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        e.printStackTrace(new PrintStream(baos));
                        logger.error(baos.toString());
                    }
                }while( (document = nextPage(document, page_index)) != null );
            }catch (Exception e) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(baos));
                logger.error(baos.toString());
            }
        }
        logger.info("[Success]");
    }

    private Document nextPage(Document document, int page_index) {

        if ( page_index >= 20 ) {
            return null;
        }
        else {
            Element next_ele = document.select("link[rel=next]").first();
            if ( next_ele == null ) {
                return null;
            }
            else {
                Document next_document = null;
                String next_url = next_ele.attr("abs:href");
                logger.info("[Next] - 翻页-> " + next_url );
                while( (next_document = downloader.document(next_url)) == null ) {
                    downloader.setProxy(getProxy.getProxy());
                }
                return next_document;
            }
        }

    }

    private static String formatXML(org.dom4j.Element root) {
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
            logger.debug(baos.toString());
        }
        return formatXMLStr;
    }

}
