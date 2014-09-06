import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class SpiderBlog {

	public static Integer count = 0;
	public static String nameDir = "";
	public static void main(String[] args) throws Exception {
		
		SpiderBlog obj = new SpiderBlog();
		File Dir = new File("数据源Url");
		obj.readDir(Dir);
		System.out.println(count);
		//obj.readFile(new File(new File("数据源Url", "anmin0001"), "10.txt"));
	}
	
	public void readDir( File Dir ) throws Exception {
		try {
			File childDirs[] = Dir.listFiles();
			for ( int i = 0; i < childDirs.length; i ++ ) {
				File childDir = childDirs[i];
				File childFiles[] = childDir.listFiles();
				for ( int j = 0; j < childFiles.length; j ++ ) {
					File childFile = childFiles[j];
					//System.out.println(childDir.getName()+"\t"+childFile.getName());
					nameDir = childDir.getName();
					readFile( childFile );
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void readFile( File file ) throws Exception {
		try {
			List<String> urlist = FileUtils.readLines(file);
			//count += urlist.size();
			for ( String url:urlist ) {
				if ( url.length() > 0 )
					readHTML(url);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		//readHTML("http://blog.sina.com.cn/s/blog_729b01aa0102e9k6.html");
	}
	
	public void readHTML( String url ) throws Exception {
		try {
			Document Doc = Jsoup.connect(url).get();
			Elements Contents = Doc.select("div[id=sina_keyword_ad_area2]");
			Elements Times = Doc.select("span[class=time SG_txtc]");
			String Time = Times.text();
			String Content = Contents.text();
			
			//System.out.println(Content);
			if ( Content.indexOf("大盘") >= 0 ) {
				System.out.println("大盘\t"+Time+"\t"+url);
				//System.out.println(Content);
				saveFile(Time, Content);
			}else {
				System.out.println(Time+"\t"+url);
			}
			//saveFile(Time, Content);
		} catch( Exception e ) {
			e.printStackTrace();
			FileUtils.writeStringToFile(new File("undownload"), url+"\r\n", "UTF-8", true);
		}
	}
	
	public void saveFile( String Time, String Content ) throws Exception {
		try {
			//System.out.println(Time);
			String timeDay = Time.substring(1, 11);
			String timeMonth = Time.substring(1, 8);
			//FileUtils.writeStringToFile(new File(new File(new File(new File("Data"), timeMonth), timeDay), System.currentTimeMillis()+".txt") , Content);
			FileUtils.writeStringToFile(new File(new File(new File(new File(new File("Data"), timeMonth), timeDay), nameDir), System.currentTimeMillis()+".txt"), Content, "UTF-8");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
