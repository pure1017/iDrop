package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import database.MysqlConnection;
import database.MysqlTableCreation;
import entity.Item;
import entity.Item.ItemBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;



public class MysqlConnectionTest {
  /**
   * This is to test method close().
   */
  @Test
  public void testClose() {
    MysqlConnection mc = new MysqlConnection();
    boolean check = mc.close();
    assertEquals(true, check);
  }
  
  /**
   * This is to test method setFavoriteItems().
   */
  @Test
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
  public void testGetItemsOnIds() {
    Set<String> itemIds = new HashSet<String>();
    itemIds.add("1111");
    MysqlConnection mc = new MysqlConnection();
    Set<Item> check = mc.getItemsOnIds(itemIds);
    assertEquals(HashSet.class, check.getClass());
  }
  
  /**
   * This is to test method saveItem().
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
    
    MysqlConnection mc = new MysqlConnection();
    boolean check = mc.saveItem(item);
    assertEquals(true, check);
  }
}







