package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import database.MysqlConnection;
import database.MysqlTableCreation;
import entity.Item;
import entity.Item.ItemBuilder;
import recommendation.BookRecommend;

public class RecommendTest {
  /**
   * This is to test book recommendation function.
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
      assertEquals(0.0, ck.getRating());
      assertEquals("url", ck.getUrl());
      assertEquals(category, ck.getCategories());
    }
    
  }
  
}