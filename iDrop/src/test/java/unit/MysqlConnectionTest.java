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
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;



public class MysqlConnectionTest {
  /**
   * This is to test method close().
   */
  
  @Test
  @Order(1)
  public void testClose() {
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
   * This is to test method getItemsOnCat().
   */
  @Test
  @Order(8)
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
   * This is to test method saveItem().
   */
  @Test
  @Order(10)
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
  
  @Test
  @Order(11)
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
  }
  
  @Test
  @Order(12)
  public void testgetRatingAndComments() {
    String itemId = "333";
    MysqlConnection mc = new MysqlConnection();
    List<List<String>> rc = mc.getRatingsAndComments(itemId);
    System.out.println(rc.size());
    assertEquals(rc.get(0).get(0), "44444");
    assertEquals(rc.get(0).get(1), "4.5");
    assertEquals(rc.get(0).get(2), "comment1");
  }
  
  // This is to test handleJoinRequests
  @Test
  @Order(13)
  public void testHandleJoinRequests() {
    MysqlConnection mc = new MysqlConnection();
    String userId1 = "miao";
    String userId2 = "liu";
    String groupName1 = "'group2'";
    String groupName2 = "'group3'";
    boolean check1 = mc.handleJoinRequests(userId1, groupName1);
    boolean check2 = mc.handleJoinRequests(userId2, groupName2);
    assertEquals(false, check1);
    assertEquals(true, check2);
  }
}







