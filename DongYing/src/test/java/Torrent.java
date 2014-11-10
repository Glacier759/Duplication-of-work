import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by glacier on 14-11-10.
 */
public class Torrent {
    public static void main(String[] agrs) {
        try {
            URL url = new URL("http://www.xixizhan.com/forum.php?mod=attachment&aid=MjIxNTN8YWJiZjBlNTl8MTQxNTYxMTUwM3wwfDM0Mzg5");
            InputStream is = url.openStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] temp = new byte[1000];

            int i = 0;
            while( (i = is.read(temp, 0, 100)) > 0 ) {
                baos.write(temp, 0, i);
            }
            FileUtils.writeByteArrayToFile(new File("test.torrent"), baos.toByteArray());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
