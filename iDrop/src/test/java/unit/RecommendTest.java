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
  public void testRecommend(){
    String userId = "11111";
    Set<Item> check = BookRecommend.recommendItems(userId);
    assertEquals(HashSet.class, check.getClass());
  }
  
}