
import java.io.File;

import org.apache.commons.io.FileUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


class Format {
	public String title, source;
	public String newspaper, page;
	public String publishtime, crawltime;
	public String language, encode;
	public String body;
}

public class SaveXML {
	
	Format format = new Format();
	public void save() {		
		try {
			XStream xstream = new XStream(new DomDriver());
			xstream.alias("root", Format.class);
			String str = xstream.toXML(format);
			System.out.println(format.source+"\n"+format.body.length());
			FileUtils.writeStringToFile(new File("Data",System.currentTimeMillis()+".xml"), str);
		} catch( Exception e ) {
			System.out.println(e);
		}
		
	}
}
