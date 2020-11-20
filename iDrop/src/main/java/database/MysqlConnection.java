package database;

import entity.Item;
import entity.Item.ItemBuilder;
import external.OpenLibraryApi;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
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
      String sql = "INSERT INTO history (user_id, item_id) VALUES (?, ?)";
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
<<<<<<< HEAD
=======
          builder.setTitle(rs.getString("title"));
>>>>>>> 7140b33b4493476ff07f16e30264718a85078a40
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
    OpenLibraryApi ol = new OpenLibraryApi();
    List<Item> items = ol.search(keyword, typeKey);
    for (Item item : items) {
      saveItem(item);
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
      String sql = "INSERT INTO items VALUES (?, ?, ?, ?, ?, ?, ?)";
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
      
      sql = "INSERT INTO categories VALUES (?, ?)";
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
  
}