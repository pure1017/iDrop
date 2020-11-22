package database;

import java.util.ArrayList;
import java.util.List;



public class MysqlRealData {

  /**
   * main method for creating and adding data.
   * @param args default
   */
  public static void main(String[] args) {

    MysqlTableCreation.createTables();
    addRealData();
  }
  
  /**
   * Add some real data from OL.
   * @return
   */
  public static boolean addRealData() {
    List<String> bookName = new ArrayList<>();
    bookName.add("the lord of the king");
    bookName.add("A Song of Ice and Fire");
    bookName.add("Greak Mythology");
    bookName.add("Aesop's Fables");
    bookName.add("Homeric");
    bookName.add("Bible");
    bookName.add("The Tale of Genji");
    bookName.add("Decameron");
    bookName.add("the Little Prince");
    bookName.add("Cinderella");
    bookName.add("Hamlet");
    bookName.add("Don Quixote");
    bookName.add("Robinson Crusoe");
    bookName.add("Gulliver's Travels");
    bookName.add("Pride and Prejudice");
    bookName.add("Notre Dame de Paris");
    bookName.add("Red and black");
    bookName.add("Count of Monte Cristo");
    bookName.add("Harry Potter and the Philosopher's Stone");
    bookName.add("Harry Potter and the Chamber of Secrets");
    bookName.add("Harry Potter and the Half-Blood Prince");
    bookName.add("Madame Bovary");
    MysqlConnection conn = new MysqlConnection();
    for (int i = 0; i < bookName.size(); i++) {
      conn.searchItems(bookName.get(i), "title");   
    }
    return true;
  }
}
