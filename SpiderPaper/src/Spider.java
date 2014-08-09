
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Spider {

	Extractor extractor = new Extractor();
	HashMap<String,String> PaperLink = new HashMap<String,String>();
	SaveXML savexml = new SaveXML();
	
	public static void main(String[] args) throws Exception {
		
		String SeedURL = "http://paper.chinaso.com/quanbubaokan.html";
		Spider obj = new Spider();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		obj.savexml.format.crawltime = sdf.format(new Date());
		obj.savexml.format.encode = "UTF-8";
		obj.savexml.format.language = "中文";
		
		obj.getAllURL(SeedURL);
		obj.updateAllURL();
		
		//URL url = new URL("http://zjrb.zjol.com.cn/html/2014-08/09/content_2779543.htm?div=-1");
		//System.out.println(url.toString().substring(0, url.toString().indexOf('?')));
		
		/*Document Doc = Jsoup.connect("http://epaper.jinghua.cn/html/2014-08/09/content_113078.htm").get();
		String str = obj.extractor.parse(Doc.toString());
		System.out.println(str);
		//System.out.println(Doc.toString());*/
		System.out.println("end");
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
			this.savexml.format.newspaper = PageName;
			getPage(TrueLink, PageName);
		}
		// 	获得所有跳转后的页面连接	
	}
	
	//获取到所有版面链接
	//获取到一个报刊的所有版面
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
					NodeMap.put(Href.attr("abs:href"), Href.text());
				}
			}
			HashSet<String> UrlBuffer = new HashSet<String>();
			for ( String NodeLink:NodeMap.keySet() ) {
				getLayout( NodeLink, NodeMap.get(NodeLink), getDate(Link), UrlBuffer );
			}
			UrlBuffer.clear();
				
		} catch( Exception e ) {
			System.out.println(e);
		}
	}
	
	//获取到版面下的所有报刊正文
	public void getLayout( String NodeLink, String NodeTitle,  String Date, HashSet<String> UrlBuffer ) {
		try {
			this.savexml.format.page = NodeTitle;
			this.savexml.format.title = "标题";
			this.savexml.format.publishtime = Date;
			
			Document Doc = Jsoup.connect(NodeLink)
					  .userAgent("Mozilla")
					  .cookie("auth", "token")
					  .timeout(3000)
					  .get();
			Elements Areas = Doc.select("area");
			for ( Element Area:Areas ) {
				String AreaLink = Area.attr("abs:href");
				if ( AreaLink.contains("?") )
					AreaLink = AreaLink.substring(0, AreaLink.indexOf('?'));
				UrlBuffer.add(AreaLink);
			}
			for ( String AreaLink:UrlBuffer ) {
				this.savexml.format.source = AreaLink;
				Document doc = Jsoup.connect(AreaLink)
						  .userAgent("Mozilla")
						  .cookie("auth", "token")
						  .timeout(3000)
						  .get();
				//String Text = extractor.parse(doc.toString());
				//if ( Text.length() == 0 )
						String Text = extractor.getPtag(doc.toString());
				//else
					//System.out.println(extractor.preProcess());
				this.savexml.format.body = Text;
				this.savexml.save();
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
