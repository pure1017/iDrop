package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import entity.Item;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import controllers.Start;
import database.MysqlRealData;
import database.MysqlTableCreation;
import recommendation.BookRecommend;

public class RecommendTest {
  /**
   * This is to test book recommendation function.
   */
  
  @BeforeAll
  public static void init() {
    // Start Server
    MysqlTableCreation.main(null);
    System.out.println("Before All");
  }
  
  /**
   * This is to test method recommend.
   */
  @Test
  public void testRecommend() {
    
    String userId = "11111";
    Set<String> category = new HashSet<>();
    category.add("female");
    Set<Item> check = BookRecommend.recommendItems(userId);
    for (Item ck : check) {
      assertEquals("444", ck.getItemId());
      assertEquals("hao", ck.getAuthor());
      assertEquals("image url", ck.getImageUrl());
      assertEquals(4.0, ck.getRating());
      assertEquals("url", ck.getUrl());
      assertEquals(category, ck.getCategories());
    }
  }
  
  /**
   * This is to test method recommend.
   */
  @Test
  public void testRecommendInvalid() {
    String userId = null;
    Set<String> category = new HashSet<>();
    category.add("female");
    Set<Item> check = BookRecommend.recommendItems(userId);
    assertEquals(null, check);
  }
}