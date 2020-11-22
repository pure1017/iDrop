package database;

import entity.Item;
import entity.Item.ItemBuilder;
import external.OpenLibraryApi;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import external.TicketMasterAPI;

public class MysqlConnection {
  private Connection conn;
  
  /**
   * constructor.
   */
  public MysqlConnection() {
    try {
      Class.forName("org.sqlite.JDBC");
      conn = DriverManager.getConnection("jdbc:sqlite:ase.db");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * close the connection.
   */
  public boolean close() {
    // TODO Auto-generated method stub
    if (conn != null) {
      try {
        conn.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return true;
  }

  /**
   * set user's liked items.
   * @param userId user id
   * @param itemIds items id
   */
  public boolean setFavoriteItems(String userId, List<String> itemIds) {
    // TODO Auto-generated method stub
    if (conn == null) {
      return false;
    }
    PreparedStatement stmt = null;
    try {
      String sql = "INSERT OR IGNORE INTO history (user_id, item_id) VALUES (?, ?)";
      //ignore checks primary key
      stmt = conn.prepareStatement(sql);
      for (String itemId : itemIds) {
        stmt.setString(1, userId);
        stmt.setString(2, itemId);
        stmt.execute();
      }
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return true;
  }

  /**
   * this is to unset favorite items.
   * @param userId .
   * @param itemIds .
   */
  public boolean unsetFavoriteItems(String userId, List<String> itemIds) {
    // TODO Auto-generated method stub
    if (conn == null) {
      return false;
    }
    PreparedStatement stmt = null;
    try {
      String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
      stmt = conn.prepareStatement(sql);
      for (String itemId : itemIds) {
        stmt.setString(1, userId);
        stmt.setString(2, itemId);
        stmt.execute();
      }
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return true;
  }

  /**
   * This is to get favorite item ids.
   * @param userId .
   * @return
   */
  public Set<String> getFavoriteItemIds(String userId) {
    // TODO Auto-generated method stub
    if (conn == null) {
      return new HashSet<>();
    }

    Set<String> favoriteItemIds = new HashSet<>();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      String sql = "SELECT item_id from history where user_id = ?";
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, userId);
      rs = stmt.executeQuery();
      while (rs.next()) {
        String itemId = rs.getString("item_id");
        favoriteItemIds.add(itemId);
      }
      stmt.close();
      rs.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return favoriteItemIds;
  }

  /**
   * This is to get favorite items.
   * @param userId .
   * @return
   */
  public Set<Item> getFavoriteItems(String userId) {
    // TODO Auto-generated method stub
    if (conn == null) {
      return new HashSet<>();
    }

    Set<Item> favoriteItems = new HashSet<>();
    Set<String> itemIds = getFavoriteItemIds(userId);
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      String sql = "SELECT * FROM items WHERE item_id = ?";
      stmt = conn.prepareStatement(sql);
      for (String itemId : itemIds) {
        stmt.setString(1, itemId);
        rs = stmt.executeQuery();

        ItemBuilder builder = new ItemBuilder();

        while (rs.next()) {
          builder.setItemId(rs.getString("item_id"));
          builder.setTitle(rs.getString("title"));
          builder.setAuthor(rs.getString("author"));
          builder.setImageUrl(rs.getString("cover_url"));
          builder.setUrl(rs.getString("url"));
          builder.setCategories(getCategories(itemId));
          favoriteItems.add(builder.build());
        }
      }
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return favoriteItems;
  }

  /**
   * This is to get the categories of items.
   * @param itemId .
   * @return
   */
  public Set<String> getCategories(String itemId) {
    // TODO Auto-generated method stub
    if (conn == null) {
      return null;
    }
    Set<String> categories = new HashSet<>();
    PreparedStatement statement = null;
    ResultSet rs = null;
    try {
      String sql = "SELECT category from categories WHERE item_id = ?";
      statement = conn.prepareStatement(sql);
      statement.setString(1, itemId);
      rs = statement.executeQuery();
      while (rs.next()) {
        categories.add(rs.getString("category"));
      }
      rs.close();
      statement.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      if (statement != null) {
        try {
          statement.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return categories;
  }

  /**
   * This is to search books.
   * @return
   */
  public List<Item> searchItems(String keyword, String typeKey) {
    // TODO Auto-generated method stub
    if (conn == null) {
      return null;
    }
    List<Item> items = new ArrayList<>();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {

      String sql = "SELECT * FROM items WHERE title = ?";
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, keyword);
      rs = stmt.executeQuery();
      if (rs.next()) {
        System.out.println("Searching in DB");
        ItemBuilder builder = new ItemBuilder();
        String itemId = rs.getString("item_id");
        builder.setItemId(itemId);
        builder.setTitle(rs.getString("title"));
        builder.setAuthor(rs.getString("author"));
        builder.setImageUrl(rs.getString("cover_url"));
        builder.setUrl(rs.getString("url"));
        if (rs.getString("description").length() < 250) {
          builder.setDescribe(rs.getString("description"));
        } else {
          builder.setDescribe(rs.getString("description").substring(0, 250) + "...");
        }
        builder.setCategories(getCategories(itemId));
        items.add(builder.build());
        rs.close();
        stmt.close();
      } else {
        System.out.println("Searching in OL");
        OpenLibraryApi ol = new OpenLibraryApi();
        items = ol.search(keyword, typeKey);
        for (Item item : items) {
          saveItem(item);
        }
      }
        

    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }	
    return items;
  }

  /**
   * This is to save items.
   * @param item .
   */
  public boolean saveItem(Item item) {
    // TODO Auto-generated method stub
    if (conn == null) {
      return false;
    }
    PreparedStatement stmt = null;

    try {
      String sql = "INSERT OR IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)";
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, item.getItemId());
      stmt.setString(2, item.getTitle());
      stmt.setString(3, item.getAuthor());
      stmt.setDouble(4, item.getRating());
      stmt.setString(5, item.getDescribe());
      stmt.setString(6, item.getImageUrl());
      stmt.setString(7, item.getUrl());
      stmt.execute();
      stmt.close();
      
      sql = "INSERT OR IGNORE INTO categories VALUES (?, ?)";
      stmt = conn.prepareStatement(sql);
      for (String category : item.getCategories()) {
        stmt.setString(1, item.getItemId());
        stmt.setString(2, category);
        stmt.execute();
      }
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return true;
  }
  
  /**
   * This is to get the items based on categories.
   * @param category The category of books
   * @return Item ids with the highest ratings
   */
  public Set<String> getItemsOnCat(String category) {
    // TODO Auto-generated method stub
    if (conn == null) {
      return null;
    }
    Set<String> itemList = new HashSet<>();
    PreparedStatement statement = null;
    ResultSet rs = null;
    try {
      String sql = "SELECT items.item_id from items inner join categories "
          + "ON categories.item_id = items.item_id"
          + " WHERE categories.category = ? ORDER BY items.rating DESC LIMIT 5";
      statement = conn.prepareStatement(sql);
      statement.setString(1, category);
      rs = statement.executeQuery();
      while (rs.next()) {
        itemList.add(rs.getString("item_id"));
      }
      rs.close();
      statement.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      if (statement != null) {
        try {
          statement.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return itemList;
  }
  
  /**
   * This is to create a group.
   * @param hostId .
   * @param bookName .
   * @param groupName .
   * @param beginDate .
   * @param groupSize .
   * @param groupDescription .
   * @return
   */
  public boolean createGroup(String hostId, String bookName, String groupName,
      String beginDate, String groupSize, String groupDescription) {
    if (conn == null) {
      return false;
    }
    PreparedStatement stmt = null;
    try {
      String sql = "INSERT INTO groups (group_name, book_name, host,"
          + "begin_date, group_size, group_description, current_size)"
          + " VALUES (?, ?, ?, ?, ?, ?, ?)";
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, groupName);
      stmt.setString(2, bookName);
      stmt.setString(3, hostId);
      stmt.setString(4, beginDate);
      stmt.setString(5, groupSize);
      stmt.setString(6, groupDescription);
      stmt.setInt(7, 0);
      stmt.execute();
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return true;
  }
  
  /**
   * This is to join a group.
   * @param userId .
   * @param groupName .
   * @return
   */
  public int joinGroup(String userId, String groupName, String joinMessage) {
    if (conn == null) {
      return 3;
    }
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      //check if the group exists or full
      String sql = "SELECT * FROM groups WHERE group_name = ?";
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, groupName);
      rs = stmt.executeQuery();
      rs.next();
      String name = rs.getString("group_name");
      int currentSize = rs.getInt("current_size");
      if (name == null || !name.equals(groupName)) {
        return 1;
      }
      if (currentSize == 4) {
        return 2;
      }
      
      //find which member is empty
      int i = 1;
      String member = "member_1";
      
      while (rs.getString(member) != null && i <= 4) {
        i++;
        member = String.format("member_%s", Integer.toString(i));
      }
      stmt.close();
      
      String message = String.format("message_%s", Integer.toString(i));
      sql = String.format("UPDATE groups SET %s = '%s', %s = '%s', %s = %d WHERE group_name = '%s'",
          member, userId, message, joinMessage, "current_size", currentSize + 1, groupName);
      stmt = conn.prepareStatement(sql);
      stmt.execute();
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return 0;
  }
  
  /**
   * This is to get a list of groups which the user is the host.
   * @param userId .
   * @return
   */
  public List<Map<String, String>> getGroupsByHost(String userId) {
    if (conn == null) {
      return null;
    }
    PreparedStatement stmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    List<Map<String, String>> groups = new ArrayList<>();
    try {
      String sql = "SELECT * FROM groups WHERE host = ?";
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, userId);
      rs = stmt.executeQuery();
      while (rs.next()) {
        String bookName = rs.getString("book_name");
        sql = "SELECT cover_url FROM items WHERE title = ?";
        stmt = conn.prepareStatement(sql);
        stmt.setString(1, bookName);
        rs2 = stmt.executeQuery();
        rs2.next();
        String coverUrl = rs2.getString("cover_url");
        rs2.close();
        
        Map<String, String> map = new HashMap<>();
        map.put("cover_url", coverUrl);
        map.put("Group Name", rs.getString("group_name"));
        map.put("Begin Date", rs.getString("begin_date"));
        map.put("Group Description", rs.getString("group_description"));
        groups.add(map);
      }
      stmt.close();
      rs.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (rs2 != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return groups;
  }
  
  /**
   * This is to get a list of groups which the user is the member.
   * @param userId .
   * @return
   */
  public List<Map<String, String>> getGroupsByMember(String userId) {
    if (conn == null) {
      return null;
    }
    PreparedStatement stmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    List<Map<String, String>> groups = new ArrayList<>();
    try {
      String sql = "SELECT * FROM groups WHERE member_1 = ?"
          + "OR member_2 = ? OR member_3 = ? OR member_4 = ?";
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, userId);
      stmt.setString(2, userId);
      stmt.setString(3, userId);
      stmt.setString(4, userId);
      rs = stmt.executeQuery();
      while (rs.next()) {
        String bookName = rs.getString("book_name");
        sql = "SELECT cover_url FROM items WHERE title = ?";
        stmt = conn.prepareStatement(sql);
        stmt.setString(1, bookName);
        rs2 = stmt.executeQuery();
        rs2.next();
        String coverUrl = rs2.getString("cover_url");
        rs2.close();
          
        Map<String, String> map = new HashMap<>();
        map.put("cover_url", coverUrl);
        map.put("Group Name", rs.getString("group_name"));
        map.put("Begin Date", rs.getString("begin_date"));
        map.put("Group Description", rs.getString("group_description"));
        groups.add(map);
      }
      stmt.close();
      rs.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (rs2 != null) {
        try {
          rs2.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return groups;
  }
  
  /**
   * This is to get a join messages.
   * @param userId .
   * @return
   */
  public List<Map<String, List<String>>> getJoinMessages(String userId) {
    if (conn == null) {
      return null;
    }
    PreparedStatement stmt = null;
    ResultSet rs = null;
    List<Map<String, List<String>>> result = new ArrayList<>();
    try {
      String sql = "SELECT * FROM groups WHERE host = ?";
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, userId);
      rs = stmt.executeQuery();
      while (rs.next()) {
        List<String> messages = new ArrayList<>();
        messages.add(rs.getString("message_1"));
        messages.add(rs.getString("message_2"));
        messages.add(rs.getString("message_3"));
        messages.add(rs.getString("message_4"));
        
        String group = rs.getString("group_name");
        Map<String, List<String>> map = new HashMap<>();
        map.put(group, messages);
        result.add(map);
      }
      stmt.close();
      rs.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return result;
  }
  
  
  
  
  /**
   * This is to get items based on item ids. 
   * @param itemIds the item ids
   * @return the items
   */
  public Set<Item> getItemsOnIds(Set<String> itemIds) {
    // TODO Auto-generated method stub
    if (conn == null) {
      return new HashSet<>();
    }

    Set<Item> recomdItems = new HashSet<>();
    //Set<String> itemIds = getFavoriteItemIds(userId);
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      String sql = "SELECT * FROM items WHERE item_id = ?";
      stmt = conn.prepareStatement(sql);
      for (String itemId : itemIds) {
        stmt.setString(1, itemId);
        rs = stmt.executeQuery();

        ItemBuilder builder = new ItemBuilder();

        while (rs.next()) {
          builder.setItemId(rs.getString("item_id"));
          builder.setTitle(rs.getString("title"));
          builder.setAuthor(rs.getString("author"));
          builder.setImageUrl(rs.getString("cover_url"));
          builder.setUrl(rs.getString("url"));
          if (rs.getString("description").length() < 250) {
            builder.setDescribe(rs.getString("description"));
          } else {
            builder.setDescribe(rs.getString("description").substring(0, 250) + "...");
          }
          builder.setCategories(getCategories(itemId));
          recomdItems.add(builder.build());
        }
      }
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return recomdItems;
  }
  
  /**
   * This is to insert book rating into table ratings. 
   */
  public boolean ratingBook(String userId, String itemId, float rating, String comment) {
    if (conn == null) {
      return false;
    }
    PreparedStatement stmt = null;
    PreparedStatement ps = null;
    try {
      String sqlInsert = "INSERT INTO ratings (user_id, item_id, rating, comment)"
          + " VALUES (?, ?, ?, ?)";
      stmt = conn.prepareStatement(sqlInsert);
      stmt.setString(1, userId);
      stmt.setString(2, itemId);
      stmt.setFloat(3, rating);
      stmt.setString(4, comment);
      String sqlUpdate = "UPDATE items SET rating = (SELECT AVG(rating) AS avg_rating "
          + "FROM ratings GROUP BY item_id HAVING item_id = ?) WHERE item_id = ?";
      ps = conn.prepareStatement(sqlUpdate);
      ps.setString(1, itemId);
      ps.setString(2, itemId);
      stmt.execute();
      stmt.close();
      ps.execute();
      ps.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return true;
  }
  
  /**
   * This is to insert book rating into table ratings. 
   */
  public List<List<String>> getRatingsAndComments(String itemId) {
    if (conn == null) {
      return null;
    }
    PreparedStatement stmt = null;
    ResultSet rs = null;
    List<List<String>> result = new ArrayList<>();
    try {
      String sql = String.format("SELECT * FROM ratings WHERE item_id = %s", itemId);
      stmt = conn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        List<String> row = new ArrayList<>();
        row.add(rs.getString("user_id"));
        row.add(Float.toString(rs.getFloat("rating")));
        row.add(rs.getString("comment"));
        result.add(row);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (rs != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return result;
  }
}