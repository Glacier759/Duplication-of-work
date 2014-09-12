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
        Element questionEle = root.addElement("question");
        questionEle.addAttribute("question", question);
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

    public void saveUserWeibo(List<weiboSearch> weiboList, String sender, String senderurl, int pageCount) {
        Element weibolist = root.addElement("weiboList");
        Element page = weibolist.addElement("page");
        page.addAttribute("count", pageCount+"");
        Element weiboSender = page.addElement("weiboSender");
        weiboSender.addText(sender);
        Element senderURL = page.addElement("senderURL");
        senderURL.addText(senderurl);
        for ( weiboSearch obj:weiboList ) {
            Element weibo = page.addElement("weibo");
            Element weiboText = weibo.addElement("weiboText");
            weiboText.addText(obj.getWeiboText());
            Element weiboImage = weibo.addElement("weiboImage");
            weiboImage.addText(obj.getWeiboImage());
            Element weiboForward = weibo.addElement("weiboForward");
            weiboForward.addText(obj.getWeiboForward());
            Element forwardReason = weibo.addElement("forwardReason");
            forwardReason.addText(obj.getForwardReason());
            Element likeCount = weibo.addElement("likeCount");
            likeCount.addText(obj.getWeiboLikeCount());
            Element forwardCount = weibo.addElement("forwardCount");
            forwardCount.addText(obj.getWeiboForwardCount());
            Element commentCount = weibo.addElement("commentCount");
            commentCount.addText(obj.getWeiboCommentCount());
            Element weiboDate = weibo.addElement("weiboDate");
            weiboDate.addText(obj.getWeiboDate());
            Element weiboFrom = weibo.addElement("weiboFrom");
            weiboFrom.addText(obj.getWeiboFrom());
        }
    }

    public void saveFansList(List<weiboFans> fansList, String userURL, String type) {
        Element fanslist = root.addElement(type+"List");
        fanslist.addAttribute("userURL", userURL);
        fanslist.addAttribute("count", fansList.size()+"");
        for ( weiboFans obj:fansList ) {
            Element fans = fanslist.addElement(type);
            Element fansName = fans.addElement(type+"Name");
            fansName.addText(obj.getFansName());
            Element fansURL = fans.addElement(type+"URL");
            fansURL.addText(obj.getFansURL());
        }
    }

    public void saveUserInfo( userInfo obj, String userURL ) {
        Element userinfo = root.addElement("userInfo");
        userinfo.addAttribute("userURL", userURL);
        Element userName = userinfo.addElement("userName");
        userName.addText(obj.getUserName());
        Element userPicURL = userinfo.addElement("userPicURL");
        userPicURL.addText(obj.getUserPicURL());
        Element weiboCount = userinfo.addElement("weiboCount");
        weiboCount.addText(obj.getWeiboCount());
        Element fansCount = userinfo.addElement("fansCount");
        fansCount.addText(obj.getFansCount());
        Element watchCount = userinfo.addElement("watchCount");
        watchCount.addText(obj.getWatchCount());
        Element confirmInfo = userinfo.addElement("confirmInfo");
        confirmInfo.addText(obj.getConfirmInfo());
        Element userSex = userinfo.addElement("userSex");
        userSex.addText(obj.getUserSex());
        Element userAddr = userinfo.addElement("userAddr");
        userAddr.addText(obj.getUserAddr());
        Element userBirth = userinfo.addElement("userBirth");
        userBirth.addText(obj.getUserBirth());
        Element userResume = userinfo.addElement("userResume");
        userResume.addText(obj.getUserResume());
        Element userTag = userinfo.addElement("userTag");
        userTag.addText(obj.getUserTag());
    }

    public void saveXML() {
        try {
            String fileName = System.currentTimeMillis() + ".xml";
            System.out.println("正在进行格式整理并保存为 " + fileName + " ...");
            OutputFormat format = OutputFormat.createPrettyPrint();
            Writer fileWriter = new FileWriter(fileName);
            XMLWriter output = new XMLWriter( fileWriter, format );
            output.write(xmlDoc);
            output.close();
           // System.out.println(FileUtils.readFileToString(new File(fileName)));
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
