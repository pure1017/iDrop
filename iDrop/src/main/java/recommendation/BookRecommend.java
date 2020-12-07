package recommendation;

import database.MysqlConnection;
import entity.Item;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


// Recommendation based on geo distance and similar categories.
public class BookRecommend {
  /**
   * collaboratIve filtering based on items.
   * @param userId user's id
   * @return
   */
  public static Set<Item> recommendItems(String userId) {
    
    MysqlConnection conn = new MysqlConnection();
    
    // Step 1 Get all favorite items
    Set<String> favoriteItemIds = conn.getFavoriteItemIds(userId);
    // Step 2 Get all categories of favorite items, sort by count
    Map<String, Integer> allCategories = new HashMap<>();
    
    for (String itemId : favoriteItemIds) {
      
      Set<String> categories = conn.getCategories(itemId);
      
      for (String category : categories) {
        allCategories.put(category, allCategories.getOrDefault(category, 0) + 1);
      }
    }
    
    List<Entry<String, Integer>> categoryList =
            new ArrayList<Entry<String, Integer>>(allCategories.entrySet());
    Collections.sort(categoryList, new Comparator<Entry<String, Integer>>() {
        @Override
        public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
            return Integer.compare(o2.getValue(), o1.getValue());
        }
    });
    
    // Step 3, do search based on category, filter out favorite items
    Set<String> visitedItems = new HashSet<>();
    Entry<String, Integer> category = categoryList.get(0);
    System.out.println(category.getKey());
    Set<String> itemList = conn.getItemsOnCat(category.getKey());
    List<String> filteredItemids = new ArrayList<>();
      
    for (String itemId : itemList) {
      if (!favoriteItemIds.contains(itemId)
              && !visitedItems.contains(itemId)) {
        filteredItemids.add(itemId);
      }
    }
      
    visitedItems.addAll(itemList);
    
    Set<String> recommendedItemids = new HashSet<>();
    recommendedItemids.addAll(filteredItemids);
    
    Set<Item> recomdItems = new HashSet<>();
    recomdItems = conn.getItemsOnIds(recommendedItemids);
    return recomdItems;
  }
}