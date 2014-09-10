/**
 * Created by glacier on 14-9-10.
 */
public class weiboSearch {
    private String weiboText;
    private String weiboSender, senderURL;
    private String weiboForward, forwardReason;

    public void setWeiboText( String weiboText ) {     this.weiboText = weiboText; }
    public void setWeiboSender( String weiboSender ) {     this.weiboSender = weiboSender; }
    public void setSenderURL( String senderURL ) { this.senderURL = senderURL;  }
    public void setWeiboForward( String weiboForward ) {       this.weiboForward = weiboForward; }
    public void setForwardReason( String forwardReason ) {     this.forwardReason = forwardReason; }

    public String getWeiboText() { return weiboText;    }
    public String getWeiboSender() {       return weiboSender; }
    public String getSenderURL() {     return senderURL;    }
    public String getWeiboForward() {      return weiboForward; }
    public String getForwardReason() {     return forwardReason; }
}
