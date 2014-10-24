
package com.glacier.sinablog;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by glacier on 14-10-17.
 */
public class SaveXML {

    public static void save(String title, String time, String content, List<String> taglist) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("root");
        Element titleEle = root.addElement("title");
        titleEle.addText(title);
        Element timeEle = root.addElement("time");
        timeEle.addText(time);
        if ( taglist.size() > 0 ) {
            Element tagsEle = root.addElement("tagsEle");
            for ( String tag:taglist ) {
                Element tagEle = tagsEle.addElement("tag");
                tagEle.addText(tag);
            }
        }
        Element contentEle = root.addElement("content");
        contentEle.addText(content);

        File saveDir = new File(SinaBlog.saveDir);
        try {
            FileUtils.writeStringToFile(new File(saveDir, System.currentTimeMillis() + ".xml"), formatXML(root));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String formatXML(Element root) {
        String formatXMLStr = null;
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new ByteArrayInputStream(root.asXML().getBytes()));
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            StringWriter writer = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(writer, format);
            xmlWriter.write(document);
            formatXMLStr = writer.toString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return formatXMLStr;
    }
}
