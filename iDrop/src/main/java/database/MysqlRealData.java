package database;

import java.util.ArrayList;
import java.util.List;



public class MysqlRealData {

  /**
   * main method for creating and adding data.
   * @param args default
   */
  public static void main(String[] args) {

    //MysqlTableCreation.createTables();
    addRealData();
  }
  
  /**
   * Add some real data from OL.
   * @return
   */
  public static boolean addRealData() {
    List<String> bookName = new ArrayList<>();
    bookName.add("the lord of the king".toLowerCase());
    bookName.add("A Song of Ice and Fire".toLowerCase());
    bookName.add("Greak Mythology".toLowerCase());
    bookName.add("Aesop's Fables".toLowerCase());
    bookName.add("Homeric".toLowerCase());
    bookName.add("Bible".toLowerCase());
    bookName.add("The Tale of Genji".toLowerCase());
    bookName.add("Decameron".toLowerCase());
    bookName.add("the Little Prince".toLowerCase());
    bookName.add("Cinderella".toLowerCase());
    bookName.add("Hamlet".toLowerCase());
    bookName.add("Don Quixote".toLowerCase());
    bookName.add("Robinson Crusoe".toLowerCase());
    bookName.add("Gulliver's Travels".toLowerCase());
    bookName.add("Pride and Prejudice".toLowerCase());
    bookName.add("Notre Dame de Paris".toLowerCase());
    bookName.add("Red and black".toLowerCase());
    bookName.add("Count of Monte Cristo".toLowerCase());
    bookName.add("Harry Potter and the Philosopher's Stone".toLowerCase());
    bookName.add("Harry Potter and the Chamber of Secrets".toLowerCase());
    bookName.add("Harry Potter and the Half-Blood Prince".toLowerCase());
    bookName.add("Madame Bovary");
    MysqlConnection conn = new MysqlConnection();
    for (int i = 0; i < bookName.size(); i++) {
      conn.searchItems(bookName.get(i), "title");   
    }
    return true;
  }
}
