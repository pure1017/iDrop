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
    
    if (typeKey != "author" && typeKey != "title") {
      typeKey = "q";
    }
    
    String query = String.format("?%s=%s", typeKey, keyword);
    
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(BOOKURL + query).openConnection();
      connection.setRequestMethod("GET");
      
      int responseCode = connection.getResponseCode();
      System.out.println("Response Code" + responseCode);
      
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
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
    String query = new String();
    String id = new String();
    
    if (!doc.isNull("key")) {
      id = doc.getString("key");
      query = String.format("%s.json", id);
    } else {
      return new String();
    }
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(DESURL + query).openConnection();
      connection.setRequestMethod("GET");
    
      int responseCode = connection.getResponseCode();
      System.out.println("Response Code" + responseCode);

      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
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
      String description = new String();
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
    return new String();
  }
  
  private List<Item> getItemList(JSONArray docs) throws JSONException {
    List<Item> itemList = new ArrayList<>();

    for (int i = 0; i < docs.length(); ++i) {
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
        System.out.println("Bad connection to DB when saveItem");
        return;
      }
      //get info from item
      String itemid = item.getItemId();
      itemid = itemid.replaceAll("/", "");
      String author = item.getAuthor();
      Double rating = item.getRating();
      String title = item.getTitle();
      String description = item.getDescribe();
      String coverurl = item.getImageUrl();
      String url = item.getUrl();
      System.out.println("item_id : " + itemid);
      /*
      //check if the user exists
      Statement stmt = conn.createStatement();
      String sql = String.format("SELECT item_id from items where item_id = %s", itemid);
      ResultSet rs = stmt.executeQuery(sql);
      //if the user do not exits, rs.next() return false.
      if (!rs.next()) {
        sql = String.format("INSERT INTO items "
          + "(item_id,title,author,rating,description,cover_url,url)"
          + "VALUES ('%s', '%s', '%s', %f, '%s', '%s', '%s')",
          itemid, title, author, rating, description, coverurl, url);
        stmt.executeUpdate(sql);  
        for (String category : item.getCategories()) {
          sql = String.format("INSERT IGNOGE INTO categories (item_id,category)" 
            + "VALUES ('%s', '%s')", itemid, category);
          stmt.executeUpdate(sql);
        }   
      }
      */
      String sql = "INSERT OR IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, itemid);
      stmt.setString(2, title);
      stmt.setString(3, author);
      stmt.setDouble(4, rating);
      stmt.setString(5, description);
      stmt.setString(6, coverurl);
      stmt.setString(7, url);
      stmt.execute();
      
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

