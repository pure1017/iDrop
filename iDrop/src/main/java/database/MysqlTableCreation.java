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
    Connection conn = null;
    Statement stmt = null;
    try {
      // This is java.sql.Connection. Not com.mysql.jdbc.Connection.

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
      stmt = conn.createStatement();
      String sql = "DROP TABLE IF EXISTS categories";
      stmt.executeUpdate(sql);

      sql = "DROP TABLE IF EXISTS history";
      stmt.executeUpdate(sql);
      
      sql = "DROP TABLE IF EXISTS items";
      stmt.executeUpdate(sql);
      
      sql = "DROP TABLE IF EXISTS users";
      stmt.executeUpdate(sql);
      
      sql = "DROP TABLE IF EXISTS groups";
      stmt.executeUpdate(sql);
      
      sql = "DROP TABLE IF EXISTS ratings";
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
      
      sql = "CREATE TABLE groups ("
        + "group_id INT AUTO_INCREMENT,"
        + "group_name VARCHAR(255) NOT NULL,"
        + "book_name VARCHAR(255) NOT NULL,"
        + "host VARCHAR(255) NOT NULL,"
        + "begin_date VARCHAR(255) NOT NULL,"
        + "group_size VARCHAR(255) NOT NULL,"
        + "group_description VARCHAR(255) NOT NULL,"
        + "member_1 VARCHAR(255),"
        + "message_1 VARCHAR(255),"
        + "member_2 VARCHAR(255),"
        + "message_2 VARCHAR(255),"
        + "member_3 VARCHAR(255),"
        + "message_3 VARCHAR(255),"
        + "member_4 VARCHAR(255),"
        + "message_4 VARCHAR(255),"
        + "current_size INT NOT NULL,"
        + "PRIMARY KEY (group_id),"
        + "FOREIGN KEY (host) REFERENCES users(user_id),"
        + "FOREIGN KEY (member_1) REFERENCES users(user_id),"
        + "FOREIGN KEY (member_2) REFERENCES users(user_id),"
        + "FOREIGN KEY (member_3) REFERENCES users(user_id),"
        + "FOREIGN KEY (member_4) REFERENCES users(user_id))";
      stmt.executeUpdate(sql);
      
      sql = "CREATE TABLE ratings ("
        + "rating_no INT AUTO_INCREMENT,"
        + "user_id VARCHAR(225) NOT NULL,"
        + "item_id VARCHAR(225) NOT NULL,"
        + "rating FLOAT NOT NULL,"
        + "comment VARCHAR(1000),"
        + "PRIMARY KEY (rating_no),"
        + "FOREIGN KEY (user_id) REFERENCES users(user_id),"
        + "FOREIGN KEY (item_id) REFERENCES items(item_id))";
      stmt.executeUpdate(sql);

      // Step 4: insert test data
      sql = "INSERT INTO users VALUES ("
        + "'1111', '3229c1097c00d497a0fd282d586be050', 'Hankun', 'Cao')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      stmt.close();
      conn.close();
      System.out.println("Tables created successfully.");
      
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return true;
  }
  
  /**
   * This is to add some fake data for testing.
   */
  public static boolean addFakeData() {
    Connection conn = null;
    Statement stmt = null;
    try {
      // This is java.sql.Connection. Not com.mysql.jdbc.Connection.

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
      stmt = conn.createStatement();
      String sql = "INSERT INTO users VALUES ("
          + "'11111', '11111@columbia.edu', 'Hankun', 'Cao')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO users VALUES ("
          + "'22222', '22222@columbia.edu', 'Tingyi', 'Wang')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO users VALUES ("
              + "'33333', '33333@columbia.edu', 'Feiqiang', 'Shen')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO users VALUES ("
              + "'44444', '44444@columbia.edu', 'Miao', 'Liu')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO users VALUES ("
              + "'55555', '55555@columbia.edu', 'XinPei', 'Ma')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO users VALUES ("
              + "'66666', '66666@columbia.edu', 'Hao', 'Lin')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      

      //add 4 fake books
      sql = "INSERT INTO items VALUES ('111', 'book1', 'tingyi', 1.0, "
          + "'some description', 'image url', 'url')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO items VALUES ('222', 'book2', 'miao', 2.0, "
              + "'some description', 'image url', 'url')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
          
      sql = "INSERT INTO items VALUES ('333', 'book3', 'xinpei', 3.0, "
              + "'some description', 'image url', 'url')";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO items VALUES ('444', 'book4', 'hao', 4.0, "
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
      
      //add fake group
      sql = "INSERT INTO groups (group_id, group_name, book_name, host, begin_date,"
         + "group_size, group_description, member_1, message_1, current_size) "
         + "VALUES (1, 'group1', 'book1', '11111', '2020', "
         + "5, 'description1', '22222', 'message1', 1)";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);

      sql = "INSERT INTO groups (group_id, group_name, book_name, host, begin_date,"
         + "group_size, group_description, member_1, message_1, member_2, message_2, current_size) "
         + "VALUES (2, 'group2', 'book2', '11111', '2020', "
         + "5, 'description1', '33333', 'message1', '44444', 'message2', 2)";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO groups (group_id, group_name, book_name, host, begin_date,"
         + "group_size, group_description, member_1, message_1, member_2, message_2, current_size) "
         + "VALUES (3, 'group3', 'book3', '22222', '2020', "
         + "5, 'description1', '11111', 'message1', '33333', 'message2', 2)";
      System.out.println("Executing query: " + sql);
      stmt.executeUpdate(sql);
      
      stmt.close();
      conn.close();
      System.out.println("added fake data");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return true;
  }
}











