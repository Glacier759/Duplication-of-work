
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Created by glacier on 14-8-28.
 */
public class xmlFormat {
    private static class Format {
        public String date, text;
    }

    static public String start( String text, String date ) {
        XStream xstream = new XStream( new DomDriver());
        Format format = new Format();
        xstream.alias("root", format.getClass());
        format.date = date;
        format.text = text;
        String XML = xstream.toXML(format);
        return XML;
    }
}
