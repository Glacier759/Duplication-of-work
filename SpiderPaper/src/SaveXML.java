
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


class Format {
	public String title, source;
	public String newspaper, page;
	public String publishtime, crawltime;
	public String language, encode;
	public String body;
	public List<String> img;
}

public class SaveXML {
	
	Format format = new Format();
	public void save() {		
		try {
			Document xmlDoc = DocumentHelper.createDocument();
			Element root = xmlDoc.addElement("root");
			Element title = root.addElement("title");
			Element source = root.addElement("source");
			Element newspaper = root.addElement("newspaper");
			Element page = root.addElement("page");
			Element publishtime = root.addElement("publishtime");
			Element crawltime = root.addElement("crawltime");
			Element language = root.addElement("language");
			Element encode = root.addElement("encode");
			Element body = root.addElement("body");
			
			title.addText(format.title);
			source.addText(format.source);
			newspaper.addText(format.newspaper);
			page.addText(format.page);
			publishtime.addText(format.publishtime);
			crawltime.addText(format.crawltime);
			language.addText(format.language);
			encode.addText(format.encode);
			body.addText(format.body);
			
			for ( String str:format.img ) {
				Element img = body.addElement("img");
				img.addText(str);
			}
			System.out.println(xmlDoc.asXML());
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8"); //设置XML文档的编码类型
			format.setSuppressDeclaration(true);
			format.setIndent(true); //设置是否缩进
			format.setIndent(" "); //以空格方式实现缩进
			format.setNewlines(true); //设置是否换行
			FileUtils.writeStringToFile(new File("a.xml"), xmlDoc.asXML());
			System.exit(0);
			Writer fileWriter = new FileWriter("output.xml");
			XMLWriter output = new XMLWriter( fileWriter, format );
			output.write(xmlDoc);
			output.close();
			String xml = FileUtils.readFileToString(new File("output.xml"));
			//System.out.println(xml);
			//System.out.println(format.source+"\n"+format.body.length());
			//FileUtils.writeStringToFile(new File(new File(new File("Data"), format.newspaper), System.currentTimeMillis()+".xml"), str);
		} catch( Exception e ) {
			System.out.println(e);
		}
	}
}
