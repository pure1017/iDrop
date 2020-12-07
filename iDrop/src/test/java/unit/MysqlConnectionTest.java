package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import database.MysqlConnection;
import database.MysqlTableCreation;
import entity.Item;
import entity.Item.ItemBuilder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;


public class MysqlConnectionTest {
  
  
  @BeforeAll
  public static void init() {
    // Start Server
    MysqlTableCreation.main(null);
    System.out.println("Before All");
  }
  
  /**
   * This is to test method close().
   */
  @Test
  @Order(1)
  public void testClose() {
    MysqlTableCreation.main(null);
    MysqlConnection mc = new MysqlConnection();
    boolean check = mc.close();
    assertEquals(true, check);
  }
  
  /**
   * This is to test method setFavoriteItems().
   */
  @Test
  @Order(2)
  public void testSetFavoriteItems() {
    String userId = "11111";
    List<String> itemIds = new ArrayList<>();
    itemIds.add("222");
    MysqlConnection mc = new MysqlConnection();
    boolean check = mc.setFavoriteItems(userId, itemIds);
    assertEquals(true, check);
  }
  
  /**
   * This is to test method unsetFavoriteItems().
   */
  @Test
  @Order(3)
  public void testunsetFavoriteItems() {
    String userId = "11111";
    List<String> itemIds = new ArrayList<>();
    itemIds.add("222");
    MysqlConnection mc = new MysqlConnection();
    boolean check = mc.unsetFavoriteItems(userId, itemIds);
    assertEquals(true, check);
  }
  
  /**
   * This is to test method getFavoriteItemIds().
   */
  @Test
  @Order(4)
  public void testGetFavoriteItemIds() {
    String userId = "11111";
    MysqlConnection mc = new MysqlConnection();
    Set<String> check = mc.getFavoriteItemIds(userId);
    assertEquals(HashSet.class, check.getClass());
  }
  
  /**
   * This is to test method getFavoriteItems().
   */
  @Test
  @Order(5)
  public void testGetFavoriteItems() {
    String userId = "11111";
    MysqlConnection mc = new MysqlConnection();
    Set<Item> check = mc.getFavoriteItems(userId);
    assertEquals(HashSet.class, check.getClass());
  }
 
  /**
   * This is to test method getCategories().
   */
  @Test
  @Order(6)
  public void testGetCategories() {
    String itemId = "222";
    MysqlConnection mc = new MysqlConnection();
    Set<String> check = mc.getCategories(itemId);
    assertEquals(HashSet.class, check.getClass());
  }
  
  /**
   * This is to test method searchItems().
   */
  @Test
  @Order(7)
  public void testSearchItems() {
    MysqlConnection mc = new MysqlConnection();
    String title = "book1";  
    List<Item> items = mc.searchItems(title, "title");
    Item item = items.get(0);
    assertEquals("111", item.getItemId());
    assertEquals("book1", item.getTitle());
    assertEquals("tingyi", item.getAuthor());
    
  }
  
  /**
   * This is to test method saveItem().
   */
  @Test
  @Order(8)
  public void testSaveItem() {
    ItemBuilder builder = new ItemBuilder();
    builder.setAuthor("author");
    Set<String> types = new HashSet<>();
    builder.setCategories(types);
    builder.setDescribe("describe");
    builder.setImageUrl("imageUrl");
    builder.setItemId("itemId");
    builder.setRating(0.0);
    builder.setTitle("title");
    builder.setUrl("url");
    Item item = builder.build();
    
    MysqlConnection mc = new MysqlConnection();
    boolean check = mc.saveItem(item);
    assertEquals(true, check);
  }
  
  /**
   * This is to test method getItemsOnIds().
   */
  @Test
  @Order(9)
  public void testGetItemsOnIds() {
    Set<String> itemIds = new HashSet<String>();
    itemIds.add("222");
    MysqlConnection mc = new MysqlConnection();
    Set<Item> check = mc.getItemsOnIds(itemIds);
    for (Item ck : check) {
      assertEquals("miao", ck.getAuthor());
      assertEquals("222", ck.getItemId());
      assertEquals("book2", ck.getTitle());
      assertEquals("some description", ck.getDescribe());
      assertEquals("image url", ck.getImageUrl());
      assertEquals("url", ck.getUrl());
    }
  }
  
  /**
   * This is to test method getItemsOnCat().
   */
  @Test
  @Order(10)
  public void testGetItemsOnCat() {
    String category = "female";
    
    MysqlConnection mc = new MysqlConnection();
    Set<String> check = mc.getItemsOnCat(category);
    Set<String> sample = new HashSet<>();
    sample.add("333");
    sample.add("444");
    assertEquals(sample, check);
  }
  
  /**
   * This is to test method createGroup().
   */
  @Test
  @Order(11)
  public void testCreateGroup() {
    MysqlConnection conn = new MysqlConnection();
    boolean check = conn.createGroup("hostId", "book1", "groupName", 
        "beginDate", "groupSize", "groupDescription");
    assertEquals(true, check);
  }
  
  /**
   * This is to test method joinGroup().
   */
  @Test
  @Order(12)
  public void testJoinGroup() {
    MysqlConnection conn = new MysqlConnection();
    int check = conn.joinGroup("userId1", "group1", "joinMessage1");
    assertEquals(0, check);
    check = conn.joinGroup("userId1", "groupgroup", "joinMessage");
    assertEquals(1, check);
    conn.joinGroup("userId2", "group1", "joinMessage");
    conn.joinGroup("userId3", "group1", "joinMessage");
    String userId1 = "miao";
    String userId2 = "liu";
    String groupName1 = "group2";
    String groupName2 = "group3";
    conn.joinGroup(userId1, groupName1, "testfalse");
    conn.joinGroup(userId2, groupName2, "testtrue");
    check = conn.joinGroup("userId4", "group2", "joinMessage");
    assertEquals(2, check);
  }
  
  /**
   * This is to test method getGroupByHost().
   */
  @Test
  @Order(13)
  public void testGetGroupByHost() {
    MysqlTableCreation.main(null);
    MysqlConnection conn = new MysqlConnection();
    List<Map<String, String>> check = conn.getGroupsByHost("11111");
    assertEquals("group1", check.get(0).get("Group Name"));
    conn.close();
  }
  
  /**
   * This is to test method getGroupByMember().
   */
  @Test
  @Order(14)
  public void testGetGroupByMember() {
    MysqlConnection conn = new MysqlConnection();
    List<Map<String, String>> check = conn.getGroupsByHost("22222");
    assertEquals("group3", check.get(0).get("Group Name"));
    conn.close();
  }
  
  /**
   * This is to test method getJoinMessage().
   */
  @Test
  @Order(15)
  public void testGetJoinMessages() {
    MysqlTableCreation.main(null);
    MysqlConnection conn = new MysqlConnection();
    conn.joinGroup("userId1", "group1", "joinMessage1");
    List<Map<String, List<Map<String, String>>>> check = conn.getJoinMessages("11111");
    assertEquals("joinMessage1", check.get(0).get("group1").get(0).get("message"));
    conn.close();
  }
  
  @Test
  @Order(16)
  public void testRatingBook() {
    MysqlConnection mc = new MysqlConnection();
    float rating = (float) 4.5;
    boolean check = mc.ratingBook("44444", "333", "2020-11-12", rating, "Good");
    assertEquals(true, check);
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
        return;
      }
      stmt = conn.createStatement();
      String sql = "SELECT * FROM items where item_id = '333'";
      ResultSet rs = stmt.executeQuery(sql);
      if (rs.next()) {
        assertEquals(4.5, rs.getFloat("rating"));
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
    mc.close();
  }
  
  @Test
  @Order(17)
  public void testgetRatingAndComments() {
    String itemId = "333";
    MysqlConnection mc = new MysqlConnection();
    List<List<String>> rc = mc.getRatingsAndComments(itemId);
    assertEquals(rc.get(0).get(0), "44444");
    assertEquals(rc.get(0).get(1), "4.5");
    assertEquals(rc.get(0).get(2), "comment1");
    assertEquals(rc.get(0).get(4), "Miao");
    assertEquals(rc.get(0).get(5), "Liu");
    mc.close();
  }
  
  // This is to test handleJoinRequests
  @Test
  @Order(18)
  public void testHandleJoinRequests() {
    MysqlConnection mc = new MysqlConnection();
    String userId1 = "miao";
    String userId2 = "liu";
    String groupName1 = "group2";
    String groupName2 = "group3";
    boolean check1 = mc.handleJoinRequests(userId1, groupName1);
    boolean check2 = mc.handleJoinRequests(userId2, groupName2);
    assertEquals(false, check1);
    assertEquals(true, check2);
    mc.close();
  }
  
  
  @Test
  @Order(19)
  public void testRejectJoinRequests() {
    MysqlConnection mc = new MysqlConnection();
    boolean check1 = mc.rejectJoinRequests("userId1", "group1");
    assertEquals(true, check1);
    mc.close();
  }
  
  
  // This is to test method ifRerating()
  @Test
  @Order(20)
  public void testIfRerating() {
    MysqlConnection mc = new MysqlConnection();
    String userId = "44444";
    String bookName = "book3";
    String bookName2 = "book4";
    boolean check1 = mc.ifRerating(userId, bookName);
    boolean check2 = mc.ifRerating(userId, bookName2);
    assertEquals(true, check1);
    assertEquals(false, check2);
    mc.close();
  }
}







