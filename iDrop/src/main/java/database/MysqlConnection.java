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
  public void close() {
    // TODO Auto-generated method stub
    if (conn != null) {
      try {
        conn.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * set user's liked items.
   * @param userId user id
   * @param itemIds items id
   */
  public void setFavoriteItems(String userId, List<String> itemIds) {
    // TODO Auto-generated method stub
    if (conn == null) {
      return;
    }
    
    try {
      String sql = "INSERT INTO history (user_id, item_id) VALUES (?, ?)";
      //ignore checks primary key
      PreparedStatement stmt = conn.prepareStatement(sql);
      for (String itemId : itemIds) {
        stmt.setString(1, userId);
        stmt.setString(2, itemId);
        stmt.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * this is to unset favorite items.
   * @param userId .
   * @param itemIds .
   */
  public void unsetFavoriteItems(String userId, List<String> itemIds) {
    // TODO Auto-generated method stub
    if (conn == null) {
      return;
    }

    try {
      String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
      PreparedStatement stmt = conn.prepareStatement(sql);
      for (String itemId : itemIds) {
        stmt.setString(1, userId);
        stmt.setString(2, itemId);
        stmt.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
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

    try {
      String sql = "SELECT item_id from history where user_id = ?";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, userId);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        String itemId = rs.getString("item_id");
        favoriteItemIds.add(itemId);
      }

    } catch (SQLException e) {
      e.printStackTrace();
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

    try {
      String sql = "SELECT * FROM items WHERE item_id = ?";
      PreparedStatement stmt = conn.prepareStatement(sql);
      for (String itemId : itemIds) {
        stmt.setString(1, itemId);
        ResultSet rs = stmt.executeQuery();

        ItemBuilder builder = new ItemBuilder();

        while (rs.next()) {
          builder.setItemId(rs.getString("item_id"));
          //builder.setAuthor(rs.getString("author"));
          builder.setImageUrl(rs.getString("cover_url"));
          builder.setUrl(rs.getString("url"));
          builder.setCategories(getCategories(itemId));
          favoriteItems.add(builder.build());
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
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
    try {
      String sql = "SELECT category from categories WHERE item_id = ? ";
      PreparedStatement statement = conn.prepareStatement(sql);
      statement.setString(1, itemId);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        categories.add(rs.getString("category"));
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
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
  public void saveItem(Item item) {
    // TODO Auto-generated method stub
    if (conn == null) {
      return;
    }

    try {
      String sql = "INSERT INTO items VALUES (?, ?, ?, ?, ?, ?, ?)";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, item.getItemId());
      stmt.setString(2, item.getTitle());
      stmt.setString(3, item.getAuthor());
      stmt.setDouble(4, item.getRating());
      stmt.setString(5, item.getDescribe());
      stmt.setString(6, item.getImageUrl());
      stmt.setString(7, item.getUrl());
      stmt.execute();

      sql = "INSERT INTO categories VALUES (?, ?)";
      stmt = conn.prepareStatement(sql);
      for (String category : item.getCategories()) {
        stmt.setString(1, item.getItemId());
        stmt.setString(2, category);
        stmt.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * This is to get the items based on categories.
   * @param category The category of books
   * @return Three item ids with the highest ratings
   */
  public Set<String> getItemsOnCat(String category) {
    // TODO Auto-generated method stub
    if (conn == null) {
      return null;
    }
    Set<String> itemList = new HashSet<>();
    try {
      String sql = "SELECT item_id from categories join items on categories.id = items.id"
          + " WHERE categories.category = ? sort by item_id.rating desc limit 3";
      PreparedStatement statement = conn.prepareStatement(sql);
      statement.setString(1, category);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        itemList.add(rs.getString("item_id"));
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return itemList;
  }

  /**
   * This is to get the full user name.
   * @param userId .
   * @return
   */
  public String getFullname(String userId) {
    // TODO Auto-generated method stub
    if (conn == null) {
      return null;
    }
    String name = "";
    try {
      String sql = "SELECT first_name, last_name from users WHERE user_id = ?";
      PreparedStatement statement = conn.prepareStatement(sql);
      statement.setString(1, userId);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        name = String.join(" ", rs.getString("first_name"), rs.getString("last_name"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return name;
  }

  /**
   * This is to verify login.
   * @param userId .
   * @param password .
   * @return
   */
  public boolean verifyLogin(String userId, String password) {
    // TODO Auto-generated method stub
    if (conn == null) {
      return false;
    }
    try {
      String sql = "SELECT user_id from users WHERE user_id = ? and password = ?";
      PreparedStatement statement = conn.prepareStatement(sql);
      statement.setString(1, userId);
      statement.setString(2, password);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

}