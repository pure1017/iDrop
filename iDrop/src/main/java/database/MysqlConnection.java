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
    if (userId == null) {
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
    if (userId == null) {
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
    if (userId == null) {
      return null;
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
    if (userId == null) {
      return null;
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
          builder.setTitle(rs.getString("title").toLowerCase());
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
    if (keyword == null) {
      return null;
    }
    List<Item> items = new ArrayList<>();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      String sql = "SELECT * FROM items WHERE title = ?";
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, keyword.toLowerCase());
      rs = stmt.executeQuery();
      if (rs.next()) {
        System.out.println("Searching in DB");
        ItemBuilder builder = new ItemBuilder();
        String itemId = rs.getString("item_id");
        builder.setItemId(itemId);
        builder.setTitle(rs.getString("title").toLowerCase());
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
        items = ol.search(keyword.toLowerCase(), typeKey);
        for (Item item : items) {
          ol.saveItem(item);
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
    if (item == null) {
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
    Set<String> itemList = new HashSet<>();
    PreparedStatement statement = null;
    ResultSet rs = null;
    try {
      String sql = "SELECT items.item_id from items inner join categories "
          + "ON categories.item_id = items.item_id"
          + " WHERE categories.category = ? ORDER BY items.rating DESC LIMIT 10";
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
   * This is to get items based on item ids. 
   * @param itemIds the item ids
   * @return the items
   */
  public Set<Item> getItemsOnIds(Set<String> itemIds) {
    // TODO Auto-generated method stub
    if (itemIds == null) {
      return null;
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
          builder.setRating(rs.getDouble("rating"));
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
    int flag = 0;
    if (hostId == null) {
      flag = 1;
    }
    if (bookName == null) {
      flag = 1;
    }
    if (groupName == null) {
      flag = 1;
    }
    if (beginDate == null) {
      flag = 1;
    }
    if (groupSize == null) {
      flag = 1;
    }
    if (groupDescription == null) {
      flag = 1;
    }
    if (flag == 1) {
      return false;
    }
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      //check if group name exists.
      String sql = String.format("SELECT * FROM groups WHERE group_name = ", groupName);
      stmt = conn.prepareStatement(sql);
      rs = stmt.executeQuery();
      if (rs.next()) {
        return false;
      }
      stmt.close();
      
      //if the book does not exits, search the book with ol.
      sql = String.format("SELECT * FROM items WHERE book_name = %s", bookName);
      stmt = conn.prepareStatement(sql);
      rs = stmt.executeQuery();
      if (!rs.next()) {
        searchItems(bookName, "title");
      }
      stmt.close();
      
      sql = "INSERT INTO groups (group_name, book_name, host,"
          + "begin_date, group_size, group_description, current_size)"
          + " VALUES (?, ?, ?, ?, ?, ?, ?)";
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, groupName);
      stmt.setString(2, bookName.toLowerCase());
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
    int groupId;
    try {
      //check if the group exists or full
      String sql = "SELECT * FROM groups WHERE group_name = ?";
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, groupName);
      rs = stmt.executeQuery();
      if (rs.next() == false) {
        return 1;
      }
      int currentSize = rs.getInt("current_size");
      if (currentSize >= 4) {
        return 2;
      }
      groupId = rs.getInt("group_id");
      if (stmt != null) {
        stmt.close();
      }
      sql = "INSERT OR IGNORE INTO applications (group_id, group_name, member, message, validm)"
        + " VALUES (?, ?, ?, ?, ?)";
      stmt = conn.prepareStatement(sql);
      stmt.setInt(1, groupId);
      stmt.setString(2, groupName);
      stmt.setString(3, userId);
      stmt.setString(4, joinMessage);
      stmt.setString(5, "undecided");
      stmt.execute();
      stmt.close();

      //check if the group exists or full
      //String sql = "SELECT * FROM groups WHERE group_name = ?";
      //stmt = conn.prepareStatement(sql);
      //stmt.setString(1, groupName);
      //rs = stmt.executeQuery();
      //if (rs.next() == false) {
      //  return 1;
      //}
      //int currentSize = rs.getInt("current_size");
      //if (currentSize == 4) {
      //  return 2;
      //}
      
      //find which member is empty
      //int i = 1;
      //String member = "member_1";
      
      //while (rs.getString(member) != null && i <= 4) {
      //  i++;
      //  member = String.format("member_%s", Integer.toString(i));
      //}
      //stmt.close();
      
      //String message = String.format("message_%s", Integer.toString(i));
      //sql = String.format("UPDATE groups SET %s = '%s', %s = '%s', %s = %d WHERE 
      //group_name = '%s'",
      //    member, userId, message, joinMessage, "current_size", currentSize + 1, groupName);
      //stmt = conn.prepareStatement(sql);
      //stmt.execute();
      //stmt.close();
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
   * This is to get a list of groups where the user is the host.
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
        String bookName = rs.getString("book_name").toLowerCase();
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
      String sql = "SELECT * FROM groups WHERE member_1 = ? "
          + "OR member_2 = ?  OR member_3 = ? "
          + "OR member_4 = ?";
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
  public List<Map<String, List<Map<String, String>>>> getJoinMessages(String userId) {
    PreparedStatement stmt = null;
    ResultSet rs = null;
    List<Map<String, List<Map<String, String>>>> result = new ArrayList<>();
    try {
      String sql = "SELECT * FROM groups WHERE host = ?";
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, userId);
      rs = stmt.executeQuery();
      int groupId;
      String groupName = "";
      String sql2 = "";
      while (rs.next()) {
        PreparedStatement stmt2 = null;
        ResultSet rs2 = null;
        groupId = rs.getInt("group_id");
        groupName = rs.getString("group_name");
        try {
          sql2 = "SELECT * FROM applications WHERE group_id = ?";
          stmt2 = conn.prepareStatement(sql2);
          stmt2.setInt(1, groupId);
          rs2 = stmt2.executeQuery();
          List<Map<String, String>> applicationForEach = new ArrayList<>();
          while (rs2.next()) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", rs2.getString("member"));
            map.put("message", rs2.getString("message"));
            map.put("validm", rs2.getString("validm"));
            applicationForEach.add(map);
          }
          
          Map<String, List<Map<String, String>>> maplist = new HashMap<>();
          maplist.put(groupName, applicationForEach);
          result.add(maplist);
          stmt2.close();
          rs2.close();
        } catch (SQLException e) {
          e.printStackTrace();
        } finally {
          if (stmt2 != null) {
            try {
              stmt2.close();
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
        //List<Map<String, String>> mapList = new ArrayList<>();
        //Map<String, Map<String, String>> mapmap = new HashMap<>();
        //Map<String, String> map1 = new HashMap<>();
        
        //map1.put("userId", rs.getString("member_1"));
        //map1.put("message", rs.getString("message_1"));
        //mapmap.put("joinmessage1", map1);
        
        //Map<String, String> map2 = new HashMap<>();
        //map2.put("userId", rs.getString("member_2"));
        //map2.put("message", rs.getString("message_2"));
        //mapmap.put("joinmessage2", map2);
        
        //Map<String, String> map3 = new HashMap<>();
        //map3.put("userId", rs.getString("member_3"));
        //map3.put("message", rs.getString("message_3"));
        //mapmap.put("joinmessage3", map3);
        
        //Map<String, String> map4 = new HashMap<>();
        //map4.put("userId", rs.getString("member_4"));
        //map4.put("message", rs.getString("message_4"));
        //mapmap.put("joinmessage4", map4);
        
        //Map<String, Map<String, Map<String, String>>> mapmapmap = new HashMap<>();
        //mapmapmap.put(rs.getString("group_name"), mapmap);
        //result.add(mapmapmap);
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
   * This is to insert book rating into table ratings. 
   */
  public boolean ratingBook(String userId, String itemId, 
      String time, float rating, String comment) {
    int flag = 0;
    if (rating < 1.0 || rating > 5.0) {
      flag = 1;
    }
    if (userId == null) {
      flag = 1;
    }
    if (itemId == null) {
      flag = 1;
    }
    if (time == null) {
      flag = 1;
    }
    if (comment == null) {
      flag = 1;
    }
    if (flag == 1) {
      return false;
    }
    PreparedStatement stmt = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String sql = "SELECT * FROM ratings WHERE user_Id = ? and item_id = ?";
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, userId);
      stmt.setString(2, itemId);
      rs = stmt.executeQuery();
      boolean exist = rs.next();
      stmt.close();
      if (exist) {
        sql = String.format("UPDATE ratings SET time = '%s', rating = %f, comment = '%s'"
            + "WHERE user_id = '%s' and item_id = '%s'", time, rating, comment, userId, itemId);
        stmt = conn.prepareStatement(sql);
        stmt.execute();
      } else {
        String sqlInsert = "INSERT INTO ratings (user_id, item_id, time, rating, comment)"
            + " VALUES (?, ?, ?, ?, ?)";
        stmt = conn.prepareStatement(sqlInsert);
        stmt.setString(1, userId);
        stmt.setString(2, itemId);
        stmt.setString(3, time);
        stmt.setFloat(4, rating);
        stmt.setString(5, comment);
      }
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
      String sql = "SELECT * FROM ratings WHERE item_id = ?";
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, itemId);
      rs = stmt.executeQuery();
      ResultSet rs2 = null;
      while (rs.next()) {
        List<String> row = new ArrayList<>();
        row.add(rs.getString("user_id"));
        row.add(Float.toString(rs.getFloat("rating")));
        row.add(rs.getString("comment"));
        row.add(rs.getString("time"));
        sql = "SELECT * FROM users WHERE user_id = ?";
        stmt = conn.prepareStatement(sql);
        stmt.setString(1, rs.getString("user_id"));
        rs2 = stmt.executeQuery();
        String firstname = "";
        String lastname = "";
        if (rs2.next()) {
          firstname = rs2.getString("first_name");
          lastname = rs2.getString("last_name");
        }
        row.add(firstname);
        row.add(lastname);
        result.add(row);
        rs2.close();
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
  
  /**
   * This is to handle joining group requests.
   */
  public boolean handleJoinRequests(String applicantId, String groupName) {
    if (conn == null) {
      return false;
    }
    boolean add = false;
    PreparedStatement stmt = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String sql = "SELECT group_id, current_size, group_size FROM groups "
          + "WHERE group_name = ?"; 
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, groupName);
      rs = stmt.executeQuery();
      int groupId = rs.getInt("group_id");
      int currentSize = Integer.parseInt(rs.getString("current_size"));
      int groupSize = Integer.parseInt(rs.getString("group_size"));
      if (currentSize < groupSize) {
        String s = Integer.toString(currentSize);
        currentSize = currentSize + 1;
        String sqlUpdate = "UPDATE groups SET member_" + s 
            + " = ?, current_size = ?  WHERE group_name = ?";
        ps = conn.prepareStatement(sqlUpdate);
        ps.setString(1, applicantId);
        ps.setInt(2, currentSize);
        ps.setString(3, groupName);
        ps.execute();
        ps.close();
        add = true;
        PreparedStatement stmt2 = null;
        try {
          String sqlUpdate2 = "UPDATE applications SET validm = ? "
              + "WHERE group_id = ? AND member = ?";
          stmt2 = conn.prepareStatement(sqlUpdate2);
          stmt2.setString(1, "proved");
          stmt2.setInt(2, groupId);
          stmt2.setString(3, applicantId);
          stmt2.execute();
          stmt2.close();
        } catch (SQLException e) {
          e.printStackTrace();
        } finally {
          if (stmt2 != null) {
            try {
              stmt2.close();
            } catch (SQLException e) {
              e.printStackTrace();
            }
          }
        }
      } else {
        add = false;
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
    return add;
  }
  
  /**
   * This is to reject joining group requests.
   */
  
  public boolean rejectJoinRequests(String applicantId, String groupName) {
    if (conn == null) {
      return false;
    }
    int flag = 0;
    if (applicantId == null) {
      flag = 1;
    }
    if (groupName == null) {
      flag = 1;
    }
    if (flag == 1) {
      return false;
    }
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      String sql = "SELECT group_id, current_size, group_size FROM groups "
          + "WHERE group_name = ?"; 
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, groupName);
      rs = stmt.executeQuery();
      int groupId = rs.getInt("group_id");
      PreparedStatement stmt2 = null;
      try {
        String sqlUpdate2 = "UPDATE applications SET validm = ? WHERE group_id = ? AND member = ?";
        stmt2 = conn.prepareStatement(sqlUpdate2);
        stmt2.setString(1, "Denied");
        stmt2.setInt(2, groupId);
        stmt2.setString(3, applicantId);
        stmt2.execute();
        stmt2.close();
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        if (stmt2 != null) {
          try {
            stmt2.close();
          } catch (SQLException e) {
            e.printStackTrace();
          }
        }
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
    return true;
  }
  
  /**
   * This is to check if the user has rated the book.
   * @param userId .
   * @return
   */
  public boolean ifRerating(String userId, String bookName) {
    int flag = 0;
    if (userId == null) {
      flag = 1;
    }
    if (bookName == null) {
      flag = 1;
    }
    if (flag == 1) {
      return false;
    }
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      String sql = "SELECT * FROM ratings JOIN items ON (ratings.item_id = items.item_id)"
          + "WHERE user_id = ? and title = ?";
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, userId);
      stmt.setString(2, bookName);
      rs = stmt.executeQuery();
      boolean result = rs.next();
      stmt.close();
      rs.close();
      return result;
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
    return false;
  }
}

