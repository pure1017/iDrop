package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlTableCreation {
  // Run this as Java application to reset db schema.
  /**
   * This is to create tables.
   * @param args args
   */
  public static void main(String[] args) {
    try {
      // This is java.sql.Connection. Not com.mysql.jdbc.Connection.
      Connection conn = null;

      // Step 1 Connect to MySQL.
      try {
        System.out.println("Connecting to jdbc:sqlite:ase.db");
        //Dynamically get reflection of data at runtime. 
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:ase.db");
      } catch (SQLException e) {
        e.printStackTrace();
      }
      if (conn == null) {
        return;
      }

      // Step2 Drop tables in case they exist
      Statement stmt = conn.createStatement();
      String sql = "DROP TABLE IF EXISTS categories";
      stmt.executeUpdate(sql);

      sql = "DROP TABLE IF EXISTS history";
      stmt.executeUpdate(sql);
      
      sql = "DROP TABLE IF EXISTS items";
      stmt.executeUpdate(sql);
      
      sql = "DROP TABLE IF EXISTS users";
      stmt.executeUpdate(sql);

      // Step 3 Create new tables
      sql = "CREATE TABLE items ("
        + "item_id VARCHAR(255) NOT NULL,"
        + "author VARCHAR(255),"
        + "title VARCHAR(255),"
        + "rating FLOAT,"
        + "describe VARCHAR(255),"
        + "cover_url VARCHAR(255),"
        + "url VARCHAR(255),"
        + "PRIMARY KEY (item_id))";
      stmt.executeUpdate(sql);
      
      sql = "CREATE TABLE authors ("
        + "item_id VARCHAR(255) NOT NULL,"
        + "author VARCHAR(255) NOT NULL,"
        + "PRIMARY KEY (item_id, author),"
        + "FOREIGN KEY (item_id) REFERENCES items(item_id))";
      stmt.executeUpdate(sql);
      
      sql = "CREATE TABLE categories ("
        + "item_id VARCHAR(255) NOT NULL,"
        + "category VARCHAR(255) NOT NULL,"
        + "PRIMARY KEY (item_id, category),"//multiple to multiple
        + "FOREIGN KEY (item_id) REFERENCES items(item_id))";
      stmt.executeUpdate(sql);

      sql = "CREATE TABLE users ("
        + "user_id VARCHAR(255) NOT NULL,"
        + "password VARCHAR(255) NOT NULL,"
        + "first_name VARCHAR(255),"
        + "last_name VARCHAR(255),"
        + "PRIMARY KEY (user_id))";
      stmt.executeUpdate(sql);

      sql = "CREATE TABLE history ("
        + "user_id VARCHAR(255) NOT NULL,"
        + "item_id VARCHAR(255) NOT NULL,"
        + "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
        + "PRIMARY KEY (user_id, item_id),"
        + "FOREIGN KEY (item_id) REFERENCES items(item_id),"
        + "FOREIGN KEY (user_id) REFERENCES users(user_id))";
      stmt.executeUpdate(sql);

      // Step 4: insert test data
      sql = "INSERT INTO users VALUES ("
        + "'1111', '3229c1097c00d497a0fd282d586be050', 'Hankun', 'Cao')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);

      System.out.println("Tables created successfully.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}