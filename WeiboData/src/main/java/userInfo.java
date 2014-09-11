/**
 * Created by glacier on 14-9-11.
 */
public class userInfo {
    private String userName, userPicURL;
    private String confirmInfo;
    private String userSex, userAddr, userBirth;
    private String userResume, userTag;

    public void setUserName( String userName ) {    this.userName = userName;   }
    public void setUserPicURL( String userPicURL ) {    this.userPicURL = userPicURL;   }
    public void setConfirmInfo( String confirmInfo ) {  this.confirmInfo = confirmInfo; }
    public void setUserSex( String userSex ) {  this.userSex = userSex; }
    public void setUserAddr( String userAddr ) {    this.userAddr = userAddr;   }
    public void setUserBirth( String userBirth ) {  this.userBirth = userBirth; }
    public void setUserResume( String userResume ) {    this.userResume = userResume;   }
    public void setUserTag( String userTag ) {  this.userTag = userTag; }

    public String getUserName() {   return userName;    }
    public String getUserPicURL() { return userPicURL;  }
    public String getConfirmInfo() {    return confirmInfo; }
    public String getUserSex() {    return userSex; }
    public String getUserAddr() {   return userAddr;    }
    public String getUserBirth() {  return userBirth;   }
    public String getUserResume() { return userResume;  }
    public String getUserTag() {    return userTag; }

}
