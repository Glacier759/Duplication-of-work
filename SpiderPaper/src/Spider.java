
import java.net.URL;
import java.util.HashMap;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Spider {

	Extractor extractor = new Extractor();
	
	private HashMap<String,String> paperMap = new HashMap<String,String>(); {
		paperMap.put("http://rmfyb.chinacourt.org/", "paper/");
	}
	
	public static void main(String[] args) throws Exception {
		
		String SeedURL = "http://paper.chinaso.com/quanbubaokan.html";
		Spider obj = new Spider();
		obj.firstFilter(SeedURL);
		//obj.secondFilter("http://paper.chinaso.com/rmzxb.html");
	}
	
	public void firstFilter( String Url ) throws Exception {
		
		try {
			String Host = new URL(Url).getHost();
			Document Doc = Jsoup.connect(Url).get();
			Elements qgbz = Doc.select("div[class=bk_cd_qgbz]");
			Elements links = qgbz.select("a[href]");
			for ( Element link : links ) {
				String url = link.attr("abs:href");
				String host = new URL(url).getHost();
				if ( Host.equals(host) ) {
					//System.out.println(url);
					secondFilter( url );
				}
			}
		} catch( Exception e ) {
			System.out.println(e);
		}
	}
	
	public void secondFilter( String Url ) throws Exception {
		
		try {
			Document Doc = Jsoup.connect(Url).get();
			Elements electronic = Doc.select("div[class=newpaper_con]");
			String url = electronic.select("a[href]").attr("abs:href");
			//System.out.println(url);
			thirdFilter( url );
		} catch( Exception e ) {
			System.out.println(e);
		}
	}
	
	public void thirdFilter( String Url ) {
		
		if ( paperMap.containsKey(Url) )
			Url = Url + paperMap.get(Url);
		try {
			Document Doc = null;
			while(true) {
				Doc = Jsoup.connect(Url).get();
				/*
				Elements Scripts = Doc.select("script");
				String script = "";
				for ( Element Script:Scripts ) {
					if ( Script.html().length() > script.length() )
						script = Script.html();
				}
				getScriptAns(script);
				*/
				Elements Meta = Doc.select("meta[http-equiv=REFRESH]");
				if ( Meta.size() > 0 ) {
					String sub = Url.substring(Url.lastIndexOf("/"));
					URL url = null;
					if ( sub.length() != 1 )
						url = new URL(Url.substring(0, Url.lastIndexOf("/")+1)+getTrueURL(Meta));
					else
						url = new URL(Url+getTrueURL(Meta));
					Url = url.toString();
				}
				else
					break;
			}
			System.out.println("URL = " + Url);
			Elements Areas = Doc.select("Area");
			/*
			 * 获取下一版
			Elements Nexts = Doc.select("a[href]");
			for ( Element Next:Nexts ) {
				if ( Next.text().contains("下一版") || Next.text().contains("下一页") ) {
					System.out.println("Next = " + Next.attr("abs:href"));
				}
			}
			*/
			forthFilter( Areas );
		} catch( Exception e ) {
			System.out.println(e);
		}
		System.out.println();
	}
	
	public void forthFilter( Elements Areas ) {
		
		if ( Areas.size() == 0 )
			return;
		
		System.out.println("forthFilter");
		
		for ( Element Area:Areas ) {
			//System.out.println(Area);
			try {
				System.out.println(Area.attr("abs:href"));
				Document Doc = Jsoup.connect(Area.attr("abs:href")).get();
				System.out.println(extractor.parse(Doc.toString()));
			} catch( Exception e ) {
				System.out.println(e);
			}
			
		}
	}
	
	public String getTrueURL( Elements Meta ) {
		String content = Meta.attr("content");
		//System.out.println(content);
		int firstindex = content.toUpperCase().indexOf("URL")+4;
		String url = content.substring(firstindex);
		if ( url.indexOf('\\') >= 0 )
			url = url.replace('\\', '/');
		//System.out.println(url);
		return url;
	}

	public void getScriptAns( String script ) throws Exception {
		
		
		System.out.println(script);
		script = "function hello(name) { return 'Hello,' + name;}";
		
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		ScriptEngine scrpitEngine = scriptEngineManager.getEngineByName("JavaScript");
		scrpitEngine.eval(script);
		Invocable inv = (Invocable) scrpitEngine;
		String ans = (String) inv.invokeFunction("hello", "Scripting");
		System.out.println(ans);
	}
}
