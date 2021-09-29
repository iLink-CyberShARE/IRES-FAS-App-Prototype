package edu.utep.cs.floodalertsystem.Model;

/**
 * <h1> User Info </h1>
 *
 * This class has the constructor, getter and setter for the user ID information.
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


public class UserInfo {
    private final String TAG="Flood";
    private final String ACTIVITY="UserInfo: ";

    private static UserInfo userInfo = null;

    private String userID;

    private UserInfo(){
    }

    public static UserInfo getInstance(){
        if(userInfo ==null){
            userInfo =new UserInfo();
        }
        return userInfo;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

}
