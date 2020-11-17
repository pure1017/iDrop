package entity;

import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Item {
  private String itemId;
  private String author;
  private String title;
  private double rating;
  private Set<String> categories;
  private String imageUrl;
  private String url;
  private String describe;
  
  public String getItemId() {
    return itemId;
  }
  
  public String getAuthor() {
    return author;
  }
  
  public String getTitle() {
    return title;
  }
  
  public double getRating() {
    return rating;
  }
  
  public Set<String> getCategories() {
    return categories;
  }
  
  public String getImageUrl() {
    return imageUrl;
  }
  
  public String getUrl() {
    return url;
  }
  
  public String getDescribe() {
    return describe;
  }
  
  /**
   * to get the JSON version of item.
   * @return
   */
  
  public JSONObject toJsonObject() {
    JSONObject obj = new JSONObject();
    try {
      obj.put("item_id", itemId);
      obj.put("author", author);
      obj.put("title", title);
      obj.put("rating", rating);
      obj.put("subject", new JSONArray(categories));
      obj.put("cover_url", imageUrl);
      obj.put("url", url);
      obj.put("description", describe);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return obj;
  }
  
  public static class ItemBuilder {

    public void setItemId(String itemId) {
      this.itemId = itemId;
    }
    
    public void setAuthor(String author) {
      this.author = author;
    }
    
    public void setTitle(String title) {
      this.title = title;
    }
    
    public void setRating(double rating) {
      this.rating = rating;
    }
    
    public void setCategories(Set<String> categories) {
      this.categories = categories;
    }
    
    public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
    }
    
    public void setUrl(String url) {
      this.url = url;
    }
    
    public void setDescribe(String describe) {
      this.describe = describe;
    }
    
    private double rating;
    private String itemId;
    private String author;
    private String title;
    private Set<String> categories;
    private String imageUrl;
    private String url;
    private String describe;
    
    public Item build() {
      return new Item(this);
    }
  }
  
  private Item(ItemBuilder builder) {
    this.itemId = builder.itemId;
    this.author = builder.author;
    this.title = builder.title;
    this.rating = builder.rating;
    this.categories = builder.categories;
    this.imageUrl = builder.imageUrl;
    this.url = builder.url;
    this.describe = builder.describe;
  }

}
