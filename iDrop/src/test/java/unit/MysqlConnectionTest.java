package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import database.MysqlConnection;
import database.MysqlTableCreation;
import entity.Item;
import entity.Item.ItemBuilder;

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
  public void testGetCatefories() {
    String itemId = "222";
    MysqlConnection mc = new MysqlConnection();
    Set<String> check = mc.getCategories(itemId);
    assertEquals(HashSet.class, check.getClass());
  }
  
//  /**
//   * This is to test method searchItems().
//   */
//  @Test
//  public void testSearchItems() {
//    String keyword = "";
//    String typeKey = "";
//    MysqlConnection mc = new MysqlConnection();
//    List<Item> check = mc.searchItems(keyword, typeKey);
//    assertEquals(List.class, check.getClass());
//  }
  
  /**
   * This is to test method getItemsOnCat().
   */
  @Test
  public void testGetItemsOnCat() {
    String category = "fiction";
    MysqlConnection mc = new MysqlConnection();
    Set<String> check = mc.getItemsOnCat(category);
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
    MysqlTableCreation.createTables();
    MysqlConnection mc = new MysqlConnection();
    boolean check = mc.saveItem(item);
    assertEquals(true, check);
  }
}







