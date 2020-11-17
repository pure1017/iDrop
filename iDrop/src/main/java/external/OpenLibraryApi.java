package external;

import entity.Item;
import entity.Item.ItemBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class OpenLibraryApi {
  private static final String BOOKURL = "https://openlibrary.org/search.json";
  private static final String DESURL = "http://openlibrary.org/";
  private static final String DEFAULTRES = "Can't find";
  
  /**
   * search book item from OpenLibrary API.
   * @param keyword search keyword
   * @param typeKey search type
   * @return List Item
   */
  
  public List<Item> search(String keyword, String typeKey) {
    if (keyword == null) {
      keyword = "";
    }
    try {
      keyword = java.net.URLEncoder.encode(keyword, "UTF-8");
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    if (typeKey.equals("author") && typeKey.equals("title")) {
      typeKey = "q";
    }
    
    String query = String.format("?%s=%s", typeKey, keyword);
    
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(BOOKURL + query).openConnection();
      connection.setRequestMethod("GET");
      
      int responseCode = connection.getResponseCode();
      System.out.println("Response Code" + responseCode);
      
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
      StringBuilder response = new StringBuilder();
      String inputLine = "";
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      
      JSONObject obj = new JSONObject(response.toString());
      if (obj.isNull("docs")) {
        return new ArrayList<>();
      }
      
      JSONArray docs = obj.getJSONArray("docs");
      return getItemList(docs);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }
  
  private String getAuthor(JSONObject doc) throws JSONException {
    String author = null;
    if (!doc.isNull("author_name")) {
      JSONArray nameSub = doc.getJSONArray("author_name");
      author = nameSub.getString(0);
      //for (int i = 0; i < nameSub.length(); ++i) {
      //  String name = nameSub.getString(i);
      //  authors.add(name);
      //}
    }
    return author;
  }
  
  private Set<String> getCategories(JSONObject doc) throws JSONException {
    Set<String> categories = new HashSet<>();
    if (!doc.isNull("subject")) {
      JSONArray nameSub = doc.getJSONArray("subject");
      for (int i = 0; i < nameSub.length(); ++i) {
        String name = nameSub.getString(i);
        categories.add(name);
      }
    }
    return categories;
  }
  
  private String getDescribe(JSONObject doc) throws JSONException {

    if (doc.isNull("key")) {
      return "";
    }
    try {
      String id = doc.getString("key");
      String query = String.format("%s.json", id);
      HttpURLConnection connection = (HttpURLConnection) new URL(DESURL + query).openConnection();
      connection.setRequestMethod("GET");
    
      int responseCode = connection.getResponseCode();
      System.out.println("Response Code" + responseCode);

      BufferedReader in = new BufferedReader(new InputStreamReader(
          connection.getInputStream(), "UTF-8"));
      StringBuilder response = new StringBuilder();
      String inputLine = "";
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      
      JSONObject obj = new JSONObject(response.toString());
      if (obj.isNull("description")) {
        String res = String.format("%s description from Open Library", DEFAULTRES);
        System.out.println("Description of " + id + " " + res);
        return res;
      }
      String description = "";
      try { 
        description = obj.getString("description"); 
        System.out.println("Description of " + id + " " + description);
      //String describe = description.getString("value");
        
      } catch (Exception e) {
        JSONObject describe = obj.getJSONObject("description");
        description = describe.getString("value"); 
        System.out.println("Description of " + id + " " + description);
      } 
      return description;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }
  
  private List<Item> getItemList(JSONArray docs) throws JSONException {
    List<Item> itemList = new ArrayList<>();
    int readLimit = 1;
    for (int i = 0; i < docs.length(); ++i) {
      if (i == readLimit) {
        break;
      }
      JSONObject doc = docs.getJSONObject(i);
      
      ItemBuilder builder = new ItemBuilder();
      
      if (!doc.isNull("key")) {
        builder.setItemId(doc.getString("key"));
        String url = String.format("https://openlibrary.org%s.json", doc.getString("key"));
        builder.setUrl(url);
      }
      
      if (!doc.isNull("title")) {
        builder.setTitle(doc.getString("title"));
      }
      
      if (!doc.isNull("cover_i")) {
        String coverUrl = String.format("https://covers.openlibrary.org/b/id/%s-L.jpg", String.valueOf(doc.getDouble("cover_i")));
        builder.setImageUrl(coverUrl);
      }
      
      builder.setAuthor(getAuthor(doc));
      builder.setCategories(getCategories(doc));
      builder.setDescribe(getDescribe(doc));
      
      Item unit = builder.build();
      saveItem(unit);
      itemList.add(unit);
    }
    
    return itemList;
  }
  
  /**
   * Save item and categories to the DB.
   * @param item book item
   */
  
  public void saveItem(Item item) {
    //set data to database
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      // This is java.sql.Connection. Not com.mysql.jdbc.Connection.
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
        System.out.println("Bad connection to DB when saveItem");
        return;
      }
      //get info from item
      String itemid = item.getItemId();
      itemid = itemid.replaceAll("/", "");
      System.out.println("item_id : " + itemid);
      String sql = "INSERT OR IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)";
      stmt = conn.prepareStatement(sql);
      String title = item.getTitle();
      stmt.setString(1, itemid);
      stmt.setString(2, title);
      String author = item.getAuthor();
      stmt.setString(3, author);
      Double rating = item.getRating();
      stmt.setDouble(4, rating);
      String description = item.getDescribe();
      stmt.setString(5, description);
      String coverurl = item.getImageUrl();
      stmt.setString(6, coverurl);
      String url = item.getUrl();
      stmt.setString(7, url);
      stmt.execute();
      if (stmt != null) {
        stmt.close();
      }
      sql = "INSERT OR IGNORE INTO categories VALUES (?, ?)";
      stmt = conn.prepareStatement(sql);
      for (String category : item.getCategories()) {
        stmt.setString(1, itemid);
        stmt.setString(2, category);
        stmt.execute();
      }
      System.out.println("Good saveItem to DB");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      try {
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }  
  }
  
  /**
   * For test get data from Open Library.
   * @param args default
   */
  
  public static void main(String[] args) {
    OpenLibraryApi bookApi = new OpenLibraryApi();
    String keyword = "the lord of the rings";
    String typeKey = "title";
    bookApi.search(keyword, typeKey);
  }
}

