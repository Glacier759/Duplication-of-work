import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Extractor {
	private  List<String> lines = new ArrayList<String>();
	private final  int blocksWidth = 3;
	private  int threshold = 86;
	private  String html;
	private  boolean flag = false;
	private  int start;
	private  int end;
	private  StringBuilder text = new StringBuilder();
	private  ArrayList<Integer> indexDistribution = new ArrayList<Integer>();
	
	
	public void setthreshold(int value) {
		threshold = value;
	}
	
	public void start() throws Exception {
		long start = System.currentTimeMillis();
		File Dir = new File("Data");
		for ( File file : Dir.listFiles() ) {
			String HTML = FileUtils.readFileToString(file);
			String newHTML = parse(HTML);
			System.out.println(file.getName());
			FileUtils.writeStringToFile(new File("result", file.getName()), newHTML);
		}
		long end = System.currentTimeMillis();
		System.out.println("end - start = " +(end-start));
	}
	
	public String parse(String _html) {
		return parse(_html, false);
	}
	public String parse(String _html, boolean _flag) {
		flag = _flag;
		html = _html;
		preProcess();
//		System.out.println(html);
		return getText();
	}
	
	public String preProcess() {
		html = html.replaceAll("(?is)<!DOCTYPE.*?>", "");
		html = html.replaceAll("(?is)<!--.*?-->", "");				// remove html comment
		html = html.replaceAll("(?is)<script.*?>.*?</script>", ""); // remove javascript
		html = html.replaceAll("(?is)<style.*?>.*?</style>", "");   // remove css
		html = html.replaceAll("&.{2,5};|&#.{2,5};", " ");			// remove special char
		html = html.replaceAll("(?is)<.*?>", "");
		return html;
		//<!--[if !IE]>|xGv00|9900d21eb16fa4350a3001b3974a9415<![endif]--> 
	}
	
	private String getText() {
		lines = Arrays.asList(html.split("\n"));
		indexDistribution.clear();
		
		for (int i = 0; i < lines.size() - blocksWidth; i++) {
			int wordsNum = 0;
			for (int j = i; j < i + blocksWidth; j++) { 
				lines.set(j, lines.get(j).replaceAll("\\s+", ""));
				wordsNum += lines.get(j).length();
			}
			indexDistribution.add(wordsNum);
			//System.out.println(wordsNum);
		}
		
		start = -1; end = -1;
		boolean boolstart = false, boolend = false;
		text.setLength(0);
		
		for (int i = 0; i < indexDistribution.size() - 1; i++) {
			if (indexDistribution.get(i) > threshold && ! boolstart) {
				if (indexDistribution.get(i+1).intValue() != 0 
					|| indexDistribution.get(i+2).intValue() != 0
					|| indexDistribution.get(i+3).intValue() != 0) {
					boolstart = true;
					start = i;
					continue;
				}
			}
			if (boolstart) {
				if (indexDistribution.get(i).intValue() == 0 
					|| indexDistribution.get(i+1).intValue() == 0) {
					end = i;
					boolend = true;
				}
			}
			StringBuilder tmp = new StringBuilder();
			if (boolend) {
				//System.out.println(start+1 + "\t\t" + end+1);
				for (int ii = start; ii <= end; ii++) {
					if (lines.get(ii).length() < 5) continue;
					tmp.append(lines.get(ii) + "\n");
				}
				String str = tmp.toString();
				//System.out.println(str);
				if (str.contains("Copyright")  || str.contains("版权所有") ) continue; 
				text.append(str);
				boolstart = boolend = false;
			}
		}
		return text.toString();
	}
	
	public String getPtag( String html ) {
		this.html = html;
		//preProcess();
		Document Doc = Jsoup.parse(this.html);
		//System.out.println(Doc.toString());
		Elements PTags = Doc.select("p");
		StringBuffer buffer = new StringBuffer();
		for ( Element PTag:PTags ) {
			String Text = PTag.text();
			if (Text.toUpperCase().contains("COPYRIGHT")  || Text.contains("版权所有") ) continue;
			buffer.append("\n"+Text);
		}
		buffer.append("\n");
		return buffer.toString();
	}

}
