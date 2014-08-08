
import java.net.URL;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Spider {

	Extractor extractor = new Extractor();
	HashMap<String,String> PaperLink = new HashMap<String,String>();
	
	public static void main(String[] args) throws Exception {
		
		String SeedURL = "http://paper.chinaso.com/quanbubaokan.html";
		Spider obj = new Spider();
		
		obj.getAllURL(SeedURL);
		obj.updateAllURL();
		//obj.getAllPage();
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
		HashMap<String,String> TempMap = (HashMap<String, String>) PaperLink.clone();
		PaperLink.clear();
		for ( String Link:TempMap.keySet() ) {
			System.out.println("Old Link = " + Link);
			URL LINK = new URL(Link);
			if ( LINK.getHost().equals("paper.chinaso.com") ) {
				try {
					Document Doc = Jsoup.connect(Link).get();
					Element JumpEle = Doc.select("div[class=newpaper_con]").first();
					String JumpLink = JumpEle.select("a[href]").attr("abs:href");
					String TrueLink = getTrueLink( JumpLink );
					String PageName = TempMap.get(Link);
					PaperLink.put(TrueLink, PageName);
					System.out.println("New Link = " + TrueLink);
				} catch( Exception e ) {
					e.printStackTrace();
				}
			} 
			else {
				String TrueLink = getTrueLink( Link );
				String PageName = TempMap.get(Link);
				PaperLink.put(TrueLink, PageName);
				System.out.println("New Link = " + TrueLink);
			}
		}
		// 	获得所有跳转后的页面连接	
	}
	
	public void getAllPage() {
		for ( String Link:PaperLink.keySet() ) {
			System.out.println(PaperLink.get(Link)+"\t\t"+Link);
		}
	}
	
	//获取Link对应的最终跳转链接，返回最终链接
	public String getTrueLink( String Link ) {
		try {
			System.out.println(Link);
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
			e.printStackTrace();
		}
		return Link;
	}
}
