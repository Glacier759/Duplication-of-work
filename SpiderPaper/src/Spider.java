
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Spider {

	static int count = 0;
	static int number = 0;
	Extractor extractor = new Extractor();
	HashMap<String,String> PaperLink = new HashMap<String,String>();
	
	public static void main(String[] args) throws Exception {
		
		String SeedURL = "http://paper.chinaso.com/quanbubaokan.html";
		Spider obj = new Spider();
		
		obj.getAllURL(SeedURL);
		obj.updateAllURL();
		System.out.println("final count = " + count);
	}
	
	//从首页获取所有报刊的链接与报刊名
	public void getAllURL( String Url )  { 	
		try {
			Document Doc = Jsoup.connect(Url).get();
			Elements URLs = Doc.select("div[class=bk_cd_zmxz]").select("a[href]");
			for ( Element URL:URLs ) {
				PaperLink.put(URL.attr("abs:href"), URL.text());
			}
		} catch( Exception e ) {
			System.out.println(e);
		}
	} 

	//更新所有报刊对应的URL
	public void updateAllURL() throws Exception {
		@SuppressWarnings("unchecked")
		HashMap<String,String> TempMap = (HashMap<String, String>) PaperLink.clone();
		PaperLink.clear();
		for ( String Link:TempMap.keySet() ) {
			String TrueLink = "";
			String PageName = "";
			URL LINK = new URL(Link);
			if ( LINK.getHost().equals("paper.chinaso.com") ) {
				try {
					Document Doc = Jsoup.connect(Link).get();
					Element JumpEle = Doc.select("div[class=newpaper_con]").first();
					String JumpLink = JumpEle.select("a[href]").attr("abs:href");
					TrueLink = getTrueLink( JumpLink );
					PageName = TempMap.get(Link);
					PaperLink.put(TrueLink, PageName);
				} catch( Exception e ) {
					System.out.println(e);
				}
			} 
			else {
				TrueLink = getTrueLink( Link );
				PageName = TempMap.get(Link);
				PaperLink.put(TrueLink, PageName);
			}
			
			getPage(TrueLink, PageName);
		}
		// 	获得所有跳转后的页面连接	
	}
	
	//获取到所有版面链接
	public void getPage( String Link, String Name ) {
		try {
			HashMap<String,String> NodeMap = new HashMap<String,String>();
			NodeMap.clear();
			Document Doc = Jsoup.connect(Link)
					  .userAgent("Mozilla")
					  .cookie("auth", "token")
					  .timeout(3000)
					  .get();
			Elements Hrefs = Doc.select("a[href]");
			for ( Element Href:Hrefs ) {
				String HrefText = Href.text();
				Pattern p = Pattern.compile(".*\\d+.*");
				Matcher m = p.matcher(HrefText);
				if ( Href.attr("href").contains("node") && !HrefText.contains("下一版") && !HrefText.contains("上一版") && HrefText.length()>0 && m.matches()) {
					System.out.println(getDate(Link));
					NodeMap.put(Href.attr("abs:href"), Href.text());
				}
			}
			
			for ( String NodeLink:NodeMap.keySet() ) {
				getLayout( NodeLink, NodeMap.get(NodeLink), Link, Name, getDate(Link) );
			}
				
		} catch( Exception e ) {
			System.out.println(e);
		}
	}
	
	public void getLayout( String NodeLink, String NodeTitle, String PageLink, String PageName, String Date ) {
		try {
			Document Doc = Jsoup.connect(NodeLink)
					  .userAgent("Mozilla")
					  .cookie("auth", "token")
					  .timeout(3000)
					  .get();
			Elements Areas = Doc.select("area");
			for ( Element Area:Areas ) {
				String AreaLink = Area.attr("abs:href");
				Document doc = Jsoup.connect(AreaLink)
						  .userAgent("Mozilla")
						  .cookie("auth", "token")
						  .timeout(3000)
						  .get();
				String Text = extractor.parse(doc.toString());
			}
		} catch( Exception e ) {
			
		}
		
		
	}
	
	//获取Link对应的最终跳转链接，返回最终链接
	public String getTrueLink( String Link ) {
		try {
			Document Doc = Jsoup.connect(Link)
					  .userAgent("Mozilla")
					  .cookie("auth", "token")
					  .timeout(3000)
					  .get();
			Elements Meta = Doc.select("meta[http-equiv=REFRESH]");
			
			if ( Meta.size() == 0 )
				return Link;
			else {
				String content = Meta.attr("content");
				int firstindex = content.toUpperCase().indexOf("URL")+4;
				String link = content.substring(firstindex);
				if ( link.indexOf('\\') >= 0 )
					link = link.replace('\\', '/');
				if ( link.contains("http://") )
					return link;
				String sub = Link.substring(Link.lastIndexOf('/'));
				if ( sub.length() != 1 ) 
					return getTrueLink(Link.substring(0, Link.lastIndexOf('/')+1) + link);
				else
					return getTrueLink(Link + link);
			}
		} catch( Exception e ) {
			System.out.println(e);
		}
		return Link;
	}
	
	public String getDate( String Link ) {
		try {
			String UrlPath = new URL(Link).getPath();
			int firstindex = UrlPath.indexOf("2014");
			String Date = UrlPath.substring(firstindex, firstindex+10);
			Date = Date.replace('/', '-');
			return Date;
		} catch ( Exception e ) {
			System.out.println(e);
		}
		return null;
	}
}
