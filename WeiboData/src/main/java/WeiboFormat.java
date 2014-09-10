import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

/**
 * Created by glacier on 14-9-9.
 */

public class WeiboFormat {
    private Document xmlDoc = DocumentHelper.createDocument();
    private Element root = xmlDoc.addElement("root");
    public void saveWeiboSearch(List<weiboSearch> weiboList, int pageCount, String question) {
        root.addAttribute("question", question);
        Element page = root.addElement("page");
        page.addAttribute("count", pageCount+"");
        for ( weiboSearch obj:weiboList ) {
            Element weibo = page.addElement("weibo");
            Element weiboSender = weibo.addElement("weiboSender");
            weiboSender.addText(obj.getWeiboSender());
            Element senderURL = weibo.addElement("senderURL");
            senderURL.addText(obj.getSenderURL());
            Element weiboText = weibo.addElement("weiboText");
            weiboText.addText(obj.getWeiboText());
            Element weiboForward = weibo.addElement("weiboForward");
            weiboForward.addText(obj.getWeiboForward());
            Element forwardReason = weibo.addElement("forwardReason");
            forwardReason.addText(obj.getForwardReason());
        }
    }

    public void saveFansList(List<weiboFans> fansList, String userURL) {
        root.addAttribute("userURL", userURL);
        root.addAttribute("count", fansList.size()+"");
        for ( weiboFans obj:fansList ) {
            Element fans = root.addElement("fans");
            Element fansName = fans.addElement("fansName");
            fansName.addText(obj.getFansName());
            Element fansURL = fans.addElement("fansURL");
            fansURL.addText(obj.getFansURL());
        }
    }

    public void saveXML() {
        try {
            String fileName = System.currentTimeMillis() + ".xml";
            OutputFormat format = OutputFormat.createPrettyPrint();
            Writer fileWriter = new FileWriter(fileName);
            XMLWriter output = new XMLWriter( fileWriter, format );
            output.write(xmlDoc);
            output.close();
            System.out.println(FileUtils.readFileToString(new File(fileName)));
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
