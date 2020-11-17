package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import database.MysqlTableCreation;
import org.junit.jupiter.api.Test;

public class MysqlTableCreationTest {
  /**
   * This is to test method createConnection.
   */
  @Test
  public void testCreateTables() {
    boolean check = MysqlTableCreation.createTables();
    assertEquals(true, check);
  }

  /**
   * This is to test the method createPlayerTable.
   */
  @Test
  public void testCreatPlayerTable() {
    boolean check = MysqlTableCreation.addFakeData();
    assertEquals(true, check);
  }
}
