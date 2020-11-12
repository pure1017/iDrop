package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import models.GameBoard;
import models.Move;
import models.Player;

public class DatabaseJDBC {

  /**
   * Create new connection.
   * @return Connection object
   */
  public Connection createConnection() {
    Connection c = null;

    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:ase.db");
    } catch (Exception e) {
      System.err.println("createConnection  " + e.getClass().getName() + ": " + e.getMessage());
      //System.exit(0);
    }
    System.out.println("Opened database successfully");
    return c;
  }

  /**
   * Create new move table.
   * @param c Connection object
   * @param tableName table name
   * @return Boolean true if table created successfully, and false if an error occurred
   */
  public boolean createMoveTable(Connection c, String tableName) {
    Statement stmt = null;
    
    try {
      c = DriverManager.getConnection("jdbc:sqlite:ase.db");
      stmt = c.createStatement();
      String sql = "CREATE TABLE IF NOT EXISTS ASE_I3_MOVE "
                + "(PLAYER_ID INT NOT NULL,"
                + "MOVE_X INT NOT NULL,"
                + "MOVE_Y INT NOT NULL)"; 
      stmt.executeUpdate(sql);
      stmt.close();
      c.close();
    } catch (Exception e) {
      System.err.println("createMoveTable  " + e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
          c.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    System.out.println("Table created successfully");
    return true;
  }
  
  /**
   * Create new board table.
   * @param c Connection object
   * @param tableName table name
   * @return Boolean true if table created successfully, and false if an error occurred
   */
  public boolean createBoardTable(Connection c, String tableName) {
    Statement stmt = null;

    try {
      c = DriverManager.getConnection("jdbc:sqlite:ase.db");
      stmt = c.createStatement();
      String sql = "CREATE TABLE IF NOT EXISTS ASE_I3_BOARD"
                + "(GETSTARTED BOOLEAN NOT NULL,"
                + "TURN INT NOT NULL,"
                + "BOARDSTATE STRING NOT NULL,"
                + "WINNER INT NOT NULL,"
                + "ISDRAW BOOLEAN NOT NULL)"; 
      stmt.executeUpdate(sql);
      stmt.close();
      c.close();
    } catch (Exception e) {
      System.err.println("createBoardTable  " + e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
          c.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    System.out.println("Table created successfully");
    return true;
  }
  
  /**
   * Create new player table.
   * @param c Connection object
   * @param tableName table name
   * @return Boolean true if table created successfully, and false if an error occurred
   */
  public boolean createPlayerTable(Connection c, String tableName) {
    Statement stmt = null;

    try {
      c = DriverManager.getConnection("jdbc:sqlite:ase.db");
      stmt = c.createStatement();
      String sql = "CREATE TABLE IF NOT EXISTS ASE_I3_PLAYER "
                + "(PLAYER_ID INT NOT NULL,"
                + "TYPE STRING NOT NULL)"; 
      stmt.executeUpdate(sql);
      stmt.close();
      c.close();
    } catch (Exception e) {
      System.err.println("createPlayerTable  " + e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
          c.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    System.out.println("Table created successfully");
    return true;
  }

  /**
   * Clear the table.
   * @param c Connection object
   * @param tableName table name
   * @return Boolean true if table cleared successfully, and false if an error occurred
   */
  public boolean cleanTable(Connection c, String tableName) {
    Statement stmt = null;
    
    try {
      c = DriverManager.getConnection("jdbc:sqlite:ase.db");
      stmt = c.createStatement();
      String sql = "DELETE FROM " + tableName + ";";
      stmt.executeUpdate(sql);
      stmt.close();
      c.close();
    } catch (Exception e) {
      System.err.println("cleanTable  " + e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
          c.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    System.out.println("Table cleared successfully");
    return true;
  }
  
  /**
   * Adds move data to the database table.
   * @param c Connection object
   * @param move Move object containing data
   * @return Boolean true if data added successfully, and false if an error occurred
   */
  public boolean addMoveData(Connection c, Move move) {
    Statement stmt = null;

    try {
      c = DriverManager.getConnection("jdbc:sqlite:ase.db");
      stmt = c.createStatement();
      String sql = "INSERT INTO ASE_I3_MOVE (PLAYER_ID,MOVE_X,MOVE_Y) "
          + "VALUES (" + move.getPlayer().getId() + ", " + move.getMoveX() 
          + ", " + move.getMoveY() + ");"; 
      stmt.executeUpdate(sql);
      stmt.close();
      c.close();
    } catch (Exception e) {
      System.err.println("addMoveData  " + e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
          c.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return true;
  }
  
  /**
   * Convert array to string.
   * @param boardState board state
   * @return string type of board state
   */
  public String arrayToString(char[][] boardState) {
    String result = "";
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < boardState.length; i++) {
      for (int j = 0; j < boardState[i].length; j++) {
        if (boardState[i][j] == '\u0000') {
          buf.append("N");
        } else {
          buf.append(boardState[i][j]);
        }
      }
    }
    result = buf.toString();
    return result;
  }
  
  /**
   * Convert string to array.
   * @param result string type of board state
   * @return array type of board state
   */
  public char[][] stringToArray(String result) {
    char[][] board = new char[3][3];
    int index = 0;
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        if (result.charAt(index) == 'N') {
          board[i][j] = '\u0000';
        } else {
          board[i][j] = result.charAt(index);
        }
        index += 1;
      }
    }
    return board;
  }
  
  /**
   * Adds board data to the database table.
   * @param c Connection object
   * @return Boolean true if data added successfully, and false if an error occurred
   */
  public boolean addBoardData(Connection c, GameBoard board) {
    Statement stmt = null;

    try {
      c = DriverManager.getConnection("jdbc:sqlite:ase.db");
      stmt = c.createStatement();
      String sqlCommand = String.format("INSERT INTO ASE_I3_BOARD (GETSTARTED,TURN,BOARDSTATE"
            + ",WINNER,ISDRAW) VALUES ( %b, %o, '%s', %o, %b);", board.isGameStarted(),
             board.getTurn(), arrayToString(board.getBoardState()),  board.getWinner(),
             board.isDraw());
      stmt.executeUpdate(sqlCommand);
      stmt.close();
      c.close();
    } catch (Exception e) {
      System.err.println("addBoardData  " + e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
          c.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return true;
  }
  
  /**
   * Adds player data to the database table.
   * @param c Connection object
   * @return Boolean true if data added successfully, and false if an error occurred
   */
  public boolean addPlayerData(Connection c, Player player) {
    Statement stmt = null;

    try {
      c = DriverManager.getConnection("jdbc:sqlite:ase.db");
      stmt = c.createStatement();
      String sql = "INSERT INTO ASE_I3_PLAYER (PLAYER_ID,TYPE) "
          + "VALUES (" + player.getId() + ", '" + player.getType() + "');"; 
      stmt.executeUpdate(sql);
      stmt.close();
      c.close();
    } catch (Exception e) {
      System.err.println("addPlayerData  " + e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
          c.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return true;
  }
  
  /**
   * Select player data from player table.
   * @param c Connection object
   * @param element the selected element
   * @param whereClause where clause
   * @return the value
   */
  public char selectPlayerData(Connection c, String element, String whereClause) {
    Statement stmt = null;
    ResultSet  rs = null;
    char result = '\u0000';
    try {
      c = DriverManager.getConnection("jdbc:sqlite:ase.db");
      stmt = c.createStatement();
      rs = stmt.executeQuery(new StringBuilder("SELECT ").append(element)
          .append(" FROM ASE_I3_PLAYER ").append(whereClause).append(";").toString());
      while (rs.next()) {
        result = rs.getString("TYPE").charAt(0);
      }
      rs.close();
      stmt.close();
      c.close();
    } catch (Exception e) {
      System.err.println("selectPlayerData  " + e.getClass().getName() + ": " + e.getMessage());
      return '\u0000';
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if (stmt != null) {
        try {
          stmt.close();
          c.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return result;
  }
  
  /**
   * Select player data from board table.
   * @param c Connection object
   * @param element the selected element
   * @param whereClause where clause
   * @return the value
   */
  public String selectBoardData(Connection c, String element, String whereClause) {
    Statement stmt = null;
    ResultSet rs = null;
    String result = "null";
    try {
      c = DriverManager.getConnection("jdbc:sqlite:ase.db");
      stmt = c.createStatement();
      rs = stmt.executeQuery("SELECT " + element + " FROM ASE_I3_BOARD "
          + whereClause + ";");
      while (rs.next()) {
        result = rs.getString("BOARDSTATE");
      }
      rs.close();
      stmt.close();
      c.close();
    } catch (Exception e) {
      System.err.println("selectBoardData  " + e.getClass().getName() + ": " + e.getMessage());
      return "null";
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if (stmt != null) {
        try {
          stmt.close();
          c.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return result;
  }
  
  /**
   * Select player data from board table.
   * @param c Connection object
   * @param element the selected element
   * @param whereClause where clause
   * @return the value
   */
  public int selectBoardIntData(Connection c, String element, String whereClause) {
    Statement stmt = null;
    ResultSet rs = null;
    int result = 0;
    try {
      c = DriverManager.getConnection("jdbc:sqlite:ase.db");
      stmt = c.createStatement();
      rs = stmt.executeQuery("SELECT " + element + " FROM ASE_I3_BOARD "
          + whereClause + ";");
      while (rs.next()) {
        result = rs.getInt(element);
      }
      rs.close();
      stmt.close();
      c.close();
    } catch (Exception e) {
      System.err.println("selectBoardIntData  " + e.getClass().getName() + ": " + e.getMessage());
      return 0;
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if (stmt != null) {
        try {
          stmt.close();
          c.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return result;
  }
  
  /**
   * Select player data from board table.
   * @param c Connection object
   * @param element the selected element
   * @param whereClause where clause
   * @return the value
   */
  public boolean selectBoardBooleanData(Connection c, String element, String whereClause) {
    Statement stmt = null;
    ResultSet rs = null;
    boolean result = false;
    try {
      c = DriverManager.getConnection("jdbc:sqlite:ase.db");
      stmt = c.createStatement();
      rs = stmt.executeQuery("SELECT " + element + " FROM ASE_I3_BOARD "
          + whereClause + ";");
      while (rs.next()) {
        result = rs.getBoolean(element);
      }
      rs.close();
      stmt.close();
      c.close();
    } catch (Exception e) {
      System.err.println("selectBoardIntData  " + e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if (stmt != null) {
        try {
          stmt.close();
          c.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return result;
  }
  
  /**
   * Select player data from move table.
   * @param c Connection object
   * @param element the selected element
   * @param whereClause where clause
   * @return the value
   */
  public int selectMoveData(Connection c, String element, String whereClause) {
    Statement stmt = null;
    ResultSet rs = null;
    int result = 0;
    try {
      c = DriverManager.getConnection("jdbc:sqlite:ase.db");
      stmt = c.createStatement();
      rs = stmt.executeQuery("SELECT " + element + " FROM ASE_I3_MOVE "
          + whereClause + ";");
      while (rs.next()) {
        result = rs.getInt(element);
      }
      rs.close();
      stmt.close();
      c.close();
    } catch (Exception e) {
      System.err.println("selectMoveData  " + e.getClass().getName() + ": " + e.getMessage());
      return 0;
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if (stmt != null) {
        try {
          stmt.close();
          c.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return result;
  }
}