package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlTableCreation {
  /**
   * This is the main function.	
   * @param args .
   */
  public static void main(String[] args) {
    createTables();
    addFakeData();
  }
  
  /**
   * This is to create tables.
   */
  public static boolean createTables() {
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
        return false;
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
        + "title VARCHAR(255),"
        + "author VARCHAR(255),"
        + "rating FLOAT,"
        + "description VARCHAR(10000),"
        + "cover_url VARCHAR(255),"
        + "url VARCHAR(255),"
        + "PRIMARY KEY (item_id))";
      stmt.executeUpdate(sql);
      
      sql = "CREATE TABLE categories ("
        + "item_id VARCHAR(255) NOT NULL,"
        + "category VARCHAR(255) NOT NULL,"
        + "PRIMARY KEY (item_id, category),"//multiple to multiple
        + "FOREIGN KEY (item_id) REFERENCES items(item_id))";
      stmt.executeUpdate(sql);

      sql = "CREATE TABLE users ("
        + "user_id VARCHAR(255) NOT NULL,"
        + "email VARCHAR(255) NOT NULL,"
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
    return true;
  }
  
  /**
   * This is to add some fake data for testing.
   */
  public static boolean addFakeData() {
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
        return true;
      }
      
      //add 2 fake users
      Statement stmt = conn.createStatement();
      String sql = "INSERT INTO users VALUES ("
          + "'11111', '1111@columbia.edu', 'Xinpei', 'Ma')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO users VALUES ("
          + "'22222', '2222@columbia.edu', 'Feiqiang', 'Shen')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);

      //add 4 fake books
      sql = "INSERT INTO items VALUES ('111', 'wangtingyi', 'tingyi', 5.0, "
          + "'some description', 'image url', 'url')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO items VALUES ('222', 'liumiao', 'miao', 5.0, "
              + "'some description', 'image url', 'url')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
          
      sql = "INSERT INTO items VALUES ('333', 'maxinpei', 'xinpei', 5.0, "
              + "'some description', 'image url', 'url')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO items VALUES ('444', 'linhao', 'hao', 5.0, "
              + "'some description', 'image url', 'url')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      //add fake categories
      sql = "INSERT INTO categories VALUES ('111', 'male')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO categories VALUES ('222', 'male')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO categories VALUES ('333', 'female')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO categories VALUES ('444', 'female')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      //add fake history
      sql = "INSERT INTO history (user_id, item_id) VALUES ('11111', '111')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO history (user_id, item_id) VALUES ('11111', '333')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO history (user_id, item_id) VALUES ('22222', '222')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO history (user_id, item_id) VALUES ('22222', '444')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);

      sql = "SELECT catefory from categories WHERE item_id = '222'";
      
      System.out.println("added fake data");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }
}











