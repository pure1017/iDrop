package login;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;


public class GoogleApiLogin {
  /**
   * login function.
 * @throws IOException .
 * @throws FileNotFoundException .
   */
  public static String login(String authCode) throws FileNotFoundException, IOException {

    System.out.println("2");
    
    String clientId = "236055320521-rsf99kh834fv176d1u5sm9a3oinskia7.apps.googleusercontent.com";
    String clientSecret = "AWMEtkmHgmHSHArhhBqVo3_c";
    String redirectUri = "http://localhost:8080";
    GoogleTokenResponse tokenResponse =
        new GoogleAuthorizationCodeTokenRequest(
        new NetHttpTransport(),
        JacksonFactory.getDefaultInstance(),
        "https://oauth2.googleapis.com/token",
        //clientSecrets.getDetails().getClientId(),
        //clientSecrets.getDetails().getClientSecret(),
        clientId,
        clientSecret,
        authCode,
        redirectUri)  // Specify the same redirect URI that you use with your web
        // app. If you don't have a web version of your app, you can
        // specify an empty string.
        .execute();
    
    Long expiresAt = System.currentTimeMillis() + (tokenResponse.getExpiresInSeconds() * 1000);
    tokenResponse.setExpiresInSeconds(expiresAt);
    
    System.out.println(3);
    //String accessToken = tokenResponse.getAccessToken();


    // Get profile info from ID token
    GoogleIdToken idToken = tokenResponse.parseIdToken();
    GoogleIdToken.Payload payload = idToken.getPayload();
    String userId = payload.getSubject();  // Use this value as a key to identify a user.
    String email = payload.getEmail();
    String familyName = (String) payload.get("family_name");
    String givenName = (String) payload.get("given_name");
    
    //set data to database
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      // Step 1 Connect to MySQL.
      try {
        System.out.println("Connecting to jdbc:sqlite:ase.db");
        //Dynamically get reflection of data at runtime. 
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:ase.db");
      } catch (SQLException e) {
        e.printStackTrace();
      }
      if (conn == null) {
        return null;
      }
      //check if the user exists
      stmt = conn.createStatement();
      String sql = String.format("SELECT user_id from users where user_id = %s", userId);
      rs = stmt.executeQuery(sql);
      //if the user do not exits, rs.next() return false.
      if (rs.next() == true) {
        return userId;
      }
      if (!rs.next()) {
        sql = String.format("INSERT INTO users (user_id,email,first_name,last_name)"
          + "VALUES ('%s', '%s', '%s', '%s')", userId, email, givenName, familyName);
        stmt.executeUpdate(sql);  
      }
      rs.close();
      stmt.close();
      conn.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }  
    return userId;
  }
}
