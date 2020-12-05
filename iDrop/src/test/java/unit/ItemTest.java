package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import entity.Item;
import entity.Item.ItemBuilder;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;



public class ItemTest {
  
  /**
   * to test method toJsonObject.
   */

  @Test
  public void testToJsonObject() {
    
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
    JSONObject obj = item.toJsonObject();
    assertEquals(obj.getDouble("rating"), 5.0);
  }
  
  @Test
  public void testToJsonObjectinvalid() {
    ItemBuilder builder = new ItemBuilder();
    Item item = builder.build();
    JSONObject obj = item.toJsonObject();
    assertEquals(obj.isNull("url"), true);
    assertEquals(obj.getDouble("rating"), 0.0);
  }
}
