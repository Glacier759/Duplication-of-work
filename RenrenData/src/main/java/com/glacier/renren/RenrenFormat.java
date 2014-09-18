package com.glacier.renren;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.HashMap;

/**
 * Created by glacier on 14-9-18.
 */
public class RenrenFormat {
    private Document xmlDoc = DocumentHelper.createDocument();
    private Element root = xmlDoc.addElement("root");

    public void saveUserInfo( HashMap<String,HashMap<String,String>> userInfoMap) {
        Element userInfo = root.addElement("userInfo");
        for ( String pKey : userInfoMap.keySet() ) {
            Element infoType = userInfo.addElement("infoType");
            infoType.addAttribute("type", pKey);
            HashMap<String,String> typeInfoMap = userInfoMap.get(pKey);
            for (String cKey : typeInfoMap.keySet()) {
                Element infoText = infoType.addElement("infoText");
                infoText.addAttribute("name",cKey);
                infoText.addText(typeInfoMap.get(cKey));
            }
        }
    }

    public String formatXML() {
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
