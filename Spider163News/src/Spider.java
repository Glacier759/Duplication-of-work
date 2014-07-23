import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class ReadURL extends Thread {

	private ArrayList<String> urls;
	private String name = null;
	private int count = 0;
	
	public ReadURL( ArrayList<String> urls, String name ) {
		this.urls = (ArrayList<String>) urls.clone();
		this.name = name;
	}
	public void run() {
		while ( urls.size() > 0 ) {
			count ++;
			String url = (String) urls.remove(0);
			
			try {
				System.out.println(count + ": " +name + ": " +url);
				SaveHTML( url );
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				System.out.println(e + ": " +url);
			}
		}
	}
	public void SaveHTML( String url ) throws Exception {
		Document Doc = Jsoup.connect(url).get();
		Elements Titles = Doc.getElementsByTag("title");
		Element Text = Doc.getElementById("endText");
		int count = 0;
		for ( Element Title:Titles ) {
			String title = Title.text();
			title = FileName( title );
			Document DocNew = Jsoup.parse("<html><head><title>"+title+"</title></head><body>"+Text.html()+"</body></html>");
			
			try {
				Elements Te = DocNew.select("object");
				for ( Element te:Te ) {
					te.remove();
				}
			} catch(Exception e) {}
			try {
				Elements Te = DocNew.select("div");
				for ( Element te:Te ) {
					te.remove();
				}
			} catch(Exception e) {}
			try {
				Elements Te = DocNew.select("script");
				for ( Element te:Te ) {
					te.remove();
				}
			} catch(Exception e) {
				FileUtils.writeStringToFile(new File("LOG.TXT"), url +": " +e, "UTF-8", true);
			}
			
			Elements imgs = DocNew.select("img[src]");
			for ( Element img:imgs ) {
				//System.out.println(img.attr("src"));
				String FilePath = SaveIMG( img.attr("src"), title, count );
				count ++;
				//System.out.println(FilePath);
				if ( FilePath != null )
					img.attr("src", FilePath);
			}
			//FileUtils.writeStringToFile(new File("TXT", title+".txt"), Text.text(), "UTF-8");
			System.out.println(title);
			FileUtils.writeStringToFile(new File("Dir", title+".html"), DocNew.toString(), "UTF-8");
		}
	}
	public String SaveIMG( String url, String title, int count ) throws Exception {
		String FilePath = null;
		if ( url.lastIndexOf("css") == -1 && url.lastIndexOf("gif") == -1 ) {
			count ++;
			
			URL obj = new URL( url );
			InputStream is = obj.openStream();
			String type = null;
			type = url.substring(url.length()-4, url.length());
			FilePath = title+"-"+count+type;
			FileUtils.copyInputStreamToFile(is, new File("IMG", FilePath));
			return "..\\IMG\\"+FilePath;
		}
		return null;
	}
	public String FileName( String text ) {
		char[] temp = text.toCharArray();
		int Length = text.length();
		
		for (int i = 0; i < Length; i ++ ) {
			if ( temp[i] == '"'|| temp[i] ==':' || temp[i] == '“'|| temp[i] == '”' || temp[i] == '：' ) {
				for ( int j = i; j < temp.length-1; j ++ ) {
					temp[j] = temp[j+1];
				}
				Length--;
				i --;
			}
		}
		String Text = new String(temp);
		Text = Text.substring(0, Length);
		return Text;
	}
	
}

public class Spider  {
	static private ArrayList<String> urls;
	static private ArrayList<ReadURL> threadList;
	static private HashMap indexedURLs;
	static private int threads;
	static private Spider Spider;
	//static int count = 0;
	
	public static void main(String[] args) throws Exception {
		// TODO 自动生成的方法存根
		String 	URL = "http://news.163.com/domestic/";
		
		Spider Spider = new Spider( URL );
		Spider.Add( URL );
		Spider.ThreadS();
	}
	public Spider( String URL ) {
		urls = new ArrayList<String>();
		threads = 10;
		//urls.add(URL);
		threadList = new ArrayList<ReadURL>();
		indexedURLs = new HashMap();
	}
	public void Add( String URL ) throws Exception {
		System.out.println("正在获取URL...");
		Elements Links = Jsoup.connect(URL).get().select("a[href]");
		for ( Element Link:Links ) {
			String url = Link.attr("href");
			String 	text = Link.text();
			if ( url.length() > 24 && ( url.indexOf("http://news.163.com/14/") != -1 && text.length() > 0 && url.lastIndexOf("update") == -1 )) {
			//if ( url.length() > 24 && ( url.indexOf("http://news.163.com/13/") != -1 || url.indexOf("http://view.163.com/13") != -1 ) && text.length() > 0 && url.lastIndexOf("update") == -1 ) {
				if ( urls.contains(url) == false ) {
					urls.add(url);
				}
			}
		}
		Elements nexturl = Jsoup.connect(URL).get().getElementsByClass("next");
		for ( Element next:nexturl ) {
			String url = next.attr("href");
			if ( url.compareTo("#") != 0 && url != null) {
				this.Add( url );
			}
		}
	}
	public void ThreadS( ) throws Exception {
		ArrayList<String> TempList = new ArrayList<String>();
		
		if ( urls.size() % threads == 0 ) {	
			System.out.println(urls.size());	
		}
		else {	
			System.out.println(urls.size());
		}

		int count = urls.size()/4;
		for ( int i = 1; urls.size() > 0; i ++ ) {
			for ( int j = 1; urls.size() > 0; j ++  ) {
				TempList.add( (String)urls.remove(0) );
				if ( j == count ) {
					break;
				}
			}
			ReadURL t = new ReadURL(TempList, "线程 "+i);
			threadList.add(t);
			t.start();
			TempList.clear();
		}
	}
}