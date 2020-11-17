package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import entity.Item;
import external.OpenLibraryApi;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.Test;


public class OpenLibraryTest {
  
  @Test
  public void testSearch() {
    String title = "the lord of rings";  
    OpenLibraryApi olApi = new OpenLibraryApi();
    List<Item> items = olApi.search(title, "title");
    Item item = items.get(0);
    assertEquals("/works/OL27448W", item.getItemId());
    assertEquals("The Lord of the Rings", item.getTitle());
    assertEquals("J.R.R Tolkien", item.getAuthor());
  }
  
  @Test
  public void testSave() {
    String title = "the lord of rings";
    OpenLibraryApi olApi = new OpenLibraryApi();
    List<Item> items = olApi.search(title, "title");
    Connection conn = null;
    Statement stmt = null;
    try {

      try {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:ase.db");
      } catch (SQLException e) {
        e.printStackTrace();
      }
      if (conn == null) {
        System.out.println("Bad connection to DB when test saveItem");
        return;
      }
      stmt = conn.createStatement();
      String sql = "SELECT item_id, title, author FROM items where item_id = 'worksOL27448W'";
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        assertEquals("worksOL27448W", rs.getString("item_id"));
        assertEquals("The Lord of the Rings", rs.getString("title"));
        assertEquals("J.R.R Tolkien", rs.getString("author"));
      }
      rs.close();
     
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      try {
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

  }
}
