package login;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
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
   */
  public static void login(String idTokenString) {
    //make some preparation for google api
    HttpTransport transport = new NetHttpTransport();
    JsonFactory jsonFactory = new GsonFactory();
    String clientId = "some id";
    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
        // Specify the CLIENT_ID of the app that accesses the backend:
        .setAudience(Collections.singletonList(clientId))
        .build();

    // (Receive idTokenString by HTTPS POST)
    GoogleIdToken idToken = null;
    try {
      idToken = verifier.verify(idTokenString);
    } catch (GeneralSecurityException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    //take useful data from token
    if (idToken == null) {
      System.out.println("Invalid ID token.");
      return;
    }
    Payload payload = idToken.getPayload();
    
    // Print user identifier
    String userId = payload.getSubject();
    System.out.println("User ID: " + userId);

    // Get profile information from payload
    String email = payload.getEmail();
    //boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
    //String name = (String) payload.get("name");
    //String pictureUrl = (String) payload.get("picture");
    String locale = (String) payload.get("locale");
    String familyName = (String) payload.get("family_name");
    String givenName = (String) payload.get("given_name");
    
    //set data to database
    try {
      // This is java.sql.Connection. Not com.mysql.jdbc.Connection.
      Connection conn = null;

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
        return;
      }
      //check if the user exists
      Statement stmt = conn.createStatement();
      String sql = String.format("SELECT user_id from users where user_id = %s", userId);
      ResultSet rs = stmt.executeQuery(sql);
      //if the user do not exits, rs.next() return false.
      if (!rs.next()) {
        sql = String.format("INSERT INTO users (user_id,email,first_name,last_name)"
          + "VALUES (%s, %s, '%s', %s)", userId, email, givenName, familyName);
        stmt.executeUpdate(sql);  
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
