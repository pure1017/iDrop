package external;

import entity.Item;
import entity.Item.ItemBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class OpenLibraryApi {
  private static final String BOOKURL = "https://openlibrary.org/search.json";
  private static final String DESURL = "http://openlibrary.org/works/";
  
  /**
   * search book item from OpenLibrary API.
   * @param keyword search keyword
   * @param typeKey search type
   * @return List Item
   */
  
  public List<Item> search(String keyword, String typeKey) {
    if (keyword == null) {
      keyword = "";
    }
    try {
      keyword = java.net.URLEncoder.encode(keyword, "UTF-8");
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    if (typeKey != "author" && typeKey != "title") {
      typeKey = "q";
    }
    
    String query = String.format("%s?=%s", typeKey, keyword);
    
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(BOOKURL + query).openConnection();
      connection.setRequestMethod("GET");
      
      int responseCode = connection.getResponseCode();
      System.out.println("Response Code" + responseCode);
      
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder response = new StringBuilder();
      String inputLine = "";
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      
      JSONObject obj = new JSONObject(response.toString());
      if (obj.isNull("docs")) {
        return new ArrayList<>();
      }
      
      JSONArray docs = obj.getJSONArray("docs");
      return getItemList(docs);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }
  
  private Set<String> getAuthors(JSONObject doc) throws JSONException {
    Set<String> authors = new HashSet<>();
    if (!doc.isNull("author_name")) {
      JSONArray nameSub = doc.getJSONArray("author_name");
      for (int i = 0; i < nameSub.length(); ++i) {
        String name = nameSub.getString(i);
        authors.add(name);
      }
    }
    return authors;
  }
  
  private Set<String> getCategories(JSONObject doc) throws JSONException {
    Set<String> categories = new HashSet<>();
    if (!doc.isNull("subject")) {
      JSONArray nameSub = doc.getJSONArray("subject");
      for (int i = 0; i < nameSub.length(); ++i) {
        String name = nameSub.getString(i);
        categories.add(name);
      }
    }
    return categories;
  }
  
  private String getDescribe(JSONObject doc) throws JSONException {
    String query = new String();
    if (!doc.isNull("key")) {
      String id = doc.getString("key");
      query = String.format("%s.json", id);
    } else {
      return new String();
    }
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(DESURL + query).openConnection();
      connection.setRequestMethod("GET");
    
      int responseCode = connection.getResponseCode();
      System.out.println("Response Code" + responseCode);

      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder response = new StringBuilder();
      String inputLine = "";
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
        
      JSONObject obj = new JSONObject(response.toString());
        
      JSONObject description = obj.getJSONObject("description");
      String describe = description.getString("value");
      return describe;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new String();
  }
  
  private List<Item> getItemList(JSONArray docs) throws JSONException {
    List<Item> itemList = new ArrayList<>();
    
    for (int i = 0; i < docs.length(); ++i) {
      JSONObject doc = docs.getJSONObject(i);
      
      ItemBuilder builder = new ItemBuilder();
      
      if (!doc.isNull("key")) {
        builder.setItemId(doc.getString("key"));
        String url = String.format("https://openlibrary.org%s.json", doc.getString("key"));
        builder.setUrl(url);
      }
      
      if (!doc.isNull("title")) {
        builder.setTitle(doc.getString("title"));
      }
      
      if (!doc.isNull("cover_i")) {
        String coverUrl = String.format("https://covers.openlibrary.org/b/id/%s-L.jpg", doc.getString("cover_i"));
        builder.setImageUrl(coverUrl);
      }
      
      builder.setAuthor(getAuthors(doc));
      builder.setCategories(getCategories(doc));
      builder.setDescribe(getDescribe(doc));
    
      itemList.add(builder.build());
    }
    
    return itemList;
  }
}

