package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import entity.Item;
import entity.Item.ItemBuilder;
import external.OpenLibraryApi;
import java.sql.Connection;
import java.sql.DriverManager;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import controllers.Start;
import database.MysqlRealData;
import database.MysqlTableCreation;


public class OpenLibraryTest {
  
  /**
   * Before all.
   */
  @BeforeAll
  public static void init() {
    // Start Server
    MysqlTableCreation.main(null);
    MysqlRealData.main(null);
    System.out.println("Before All");
  }
  
  /**
   * to test method search. 
   */
  
  @Test
  public void testSearch() {
    String title = "the lord of rings";  
    OpenLibraryApi olApi = new OpenLibraryApi();
    List<Item> items = olApi.search(title, "title");
    Item item = items.get(0);
    assertEquals("worksOL27448W", item.getItemId());
    assertEquals("the lord of the rings", item.getTitle());
    assertEquals("J.R.R. Tolkien", item.getAuthor());
  }
  
  @Test
  public void testSearchinvalid() {
    String title = "";
    OpenLibraryApi olApi = new OpenLibraryApi();
    List<Item> items = olApi.search(title, "title");
    assertEquals(items, new ArrayList<>());
  }
  
  /**
   * to test method getCategories.
   */
  
  @Test
  public void testGetCategories() {
   
    JSONObject obj = new JSONObject();
    try {
      obj.put("key", "/works/OL27448W");
      List<String> categories = new ArrayList<>();
      categories.add("English");
      categories.add("Engineer");
      obj.put("subject", new JSONArray(categories)); 
    } catch (JSONException e) {
      e.printStackTrace();
    }
    OpenLibraryApi olApi = new OpenLibraryApi();
    assertEquals(true, olApi.getCategories(obj).contains("English"));
  }
  
  @Test 
  public void testGetCategoriesinvalid1() {
    JSONObject obj = new JSONObject();
    try {
      obj.put("key", "/works/OL27448W");
      List<String> categories = new ArrayList<>();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    OpenLibraryApi olApi = new OpenLibraryApi();
    assertEquals(true, olApi.getCategories(obj).isEmpty());
  }
  
  @Test
  public void testGetCategoriesinvalid2() {
    JSONObject obj = new JSONObject();
    try {
      obj.put("key", "/works/OL27448W");
      List<String> categories = new ArrayList<>();
      obj.put("subject", new JSONArray(categories)); 
    } catch (JSONException e) {
      e.printStackTrace();
    }
    OpenLibraryApi olApi = new OpenLibraryApi();
    assertEquals(new HashSet<>(), olApi.getCategories(obj));
  }
  
  /**
   * to test method getDescribe.
   */
  
  @Test
  public void testGetDescribe() {
    JSONObject obj = new JSONObject();
    try {
      obj.put("key", "/works/OL27448W");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    OpenLibraryApi olApi = new OpenLibraryApi();
    assertEquals(String.class, olApi.getDescribe(obj).getClass());
  }
  
  @Test 
  public void testGetDescribeinvalid1() {
    JSONObject obj = new JSONObject();
    try {
      obj.put("key", "");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    OpenLibraryApi olApi = new OpenLibraryApi();
    assertEquals("", olApi.getDescribe(obj));
  }
  
  @Test
  public void testGetDescribeinvalid2() {
    JSONObject obj = new JSONObject();
    OpenLibraryApi olApi = new OpenLibraryApi();
    assertEquals("", olApi.getDescribe(obj));
  }
  
  /**
   * to test method getItemList.
   */
  
  @Test
  public void testGetItemList1() {
    JSONObject obj = new JSONObject();
    try {
      List<String> author = new ArrayList<>();
      author.add("author");
      obj.put("key", "/works/OL27448W");
      obj.put("author_name", author);
      obj.put("title", "title");
      obj.put("rating", 5.0);
      List<String> categories = new ArrayList<>();
      categories.add("English");
      categories.add("Engineer");
      obj.put("subject", new JSONArray(categories));
      obj.put("cover_i", 2222);
      
      
    } catch (JSONException e) {
      e.printStackTrace();
    }
    JSONArray objs = new JSONArray();
    objs.put(obj);
    OpenLibraryApi olApi = new OpenLibraryApi();
    List<Item> items = olApi.getItemList(objs);
    assertEquals(items.get(0).getImageUrl(), "https://covers.openlibrary.org/b/id/2222-L.jpg");
  }
  
  @Test
  public void testGetItemList2() {
    JSONObject obj = new JSONObject();
    JSONObject obj2 = new JSONObject();
    try {
      List<String> author = new ArrayList<>();
      author.add("author");
      obj.put("key", "/works/OL27448W");
      obj.put("author_name", author);
      obj.put("title", "title");
      obj.put("rating", 5.0);
      List<String> categories = new ArrayList<>();
      categories.add("English");
      categories.add("Engineer");
      obj.put("subject", new JSONArray(categories));
      obj.put("cover_i", 1111);
      
      List<String> author2 = new ArrayList<>();
      author2.add("author2");
      obj.put("key", "/works/OL27448W2");
      obj.put("author_name", author2);
      obj.put("title", "title2");
      obj.put("rating", 5.0);
      List<String> categories2 = new ArrayList<>();
      categories2.add("English2");
      categories2.add("Engineer2");
      obj.put("subject", new JSONArray(categories2));
      obj.put("cover_i", 2222);
      
    } catch (JSONException e) {
      e.printStackTrace();
    }
    JSONArray objs = new JSONArray();
    objs.put(obj);
    objs.put(obj2);
    OpenLibraryApi olApi = new OpenLibraryApi();
    List<Item> items = olApi.getItemList(objs);
    assertEquals(items.size(), 1);
  }
  
  @Test
  public void testGetItemListinvalid() {
    JSONObject obj = new JSONObject();
    JSONArray objs = new JSONArray();
    objs.put(obj);
    OpenLibraryApi olApi = new OpenLibraryApi();
    List<Item> items = olApi.getItemList(objs);
    assertEquals(items.get(0).getAuthor(), null);
  }
  
  /**
   * to test method getAuthor.
   */
  
  @Test
  public void testGetAuthor1() {
    JSONObject obj = new JSONObject();
    
    try {
      List<String> author = new ArrayList<>();
      author.add("author");
      obj.put("author_name", new JSONArray(author));

    } catch (JSONException e) {
      e.printStackTrace();
    }      
    OpenLibraryApi olApi = new OpenLibraryApi();  
    assertEquals("author", olApi.getAuthor(obj));
  }
  
  @Test
  public void testGetAuthor2() {
    JSONObject obj = new JSONObject();
    
    try {
      List<String> author = new ArrayList<>();
      author.add("author1");
      author.add("author2");
      obj.put("author_name", new JSONArray(author));

    } catch (JSONException e) {
      e.printStackTrace();
    }      
    OpenLibraryApi olApi = new OpenLibraryApi();  
    assertEquals("author1", olApi.getAuthor(obj));
  }
  
  @Test
  public void testGetAuthorinvalid1() {
    JSONObject obj = new JSONObject();
    try {
      List<String> author = new ArrayList<>();
      obj.put("author_name", new JSONArray(author));

    } catch (JSONException e) {
      e.printStackTrace();
    }      
    OpenLibraryApi olApi = new OpenLibraryApi();
    assertEquals("", olApi.getAuthor(obj));
  }
  
  @Test
  public void testGetAuthorinvalid2() {
    JSONObject obj = new JSONObject();
    OpenLibraryApi olApi = new OpenLibraryApi();
    assertEquals("", olApi.getAuthor(obj));
  }
  
  /**
   * to test method saveItem.
   */
  
  @Test
  public void testSaveItem() {
    
    ItemBuilder builder = new ItemBuilder();
    builder.setAuthor("author");
    Set<String> types = new HashSet<>();
    builder.setCategories(types);
    builder.setDescribe("describe");
    builder.setImageUrl("imageUrl");
    builder.setItemId("itemId");
    builder.setRating(5.0);
    builder.setTitle("title");
    builder.setUrl("url");
    Item item = builder.build();
    OpenLibraryApi olApi = new OpenLibraryApi();
    olApi.saveItem(item);
    Connection conn = null;
    Statement stmt = null;
    try {

      try {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:ase.db");
      } catch (SQLException e) {
        e.printStackTrace();
      }
      if (conn == null) {
        System.out.println("Bad connection to DB when test saveItem");
        return;
      }
      stmt = conn.createStatement();
      String sql = "SELECT item_id, title, author FROM items where item_id = 'itemId'";
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        assertEquals("itemId", rs.getString("item_id"));
        assertEquals("title", rs.getString("title"));
        assertEquals("author", rs.getString("author"));
      }
      rs.close();
     
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
  
  @Test
  public void testSaveIteminvalid() {
    
    ItemBuilder builder = new ItemBuilder();
    Item item = builder.build();
    OpenLibraryApi olApi = new OpenLibraryApi();
    olApi.saveItem(item);
    Connection conn = null;
    Statement stmt = null;
    try {

      try {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:ase.db");
      } catch (SQLException e) {
        e.printStackTrace();
      }
      if (conn == null) {
        System.out.println("Bad connection to DB when test saveItem");
        return;
      }
      stmt = conn.createStatement();
      String sql = "SELECT item_id, title, author FROM items where item_id = 'null'";
      ResultSet rs = stmt.executeQuery(sql);
      assertEquals(true, rs.wasNull());
      rs.close();
     
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
}
