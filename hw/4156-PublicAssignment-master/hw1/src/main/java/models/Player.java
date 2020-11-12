package models;

public class Player {

  private char type;

  private int id;
  
  /** Create Player.
   * 
   * @param playerType player using X or O
   * @param playerId player id
   */
  public Player(char playerType, int playerId) {
    type = playerType;
    id = playerId;
  }
  
  /*
   * get player type
   */
  public char getType() {
    return type;
  }
  
  /*
   * set player type
   */
  public void setType(char type) {
    this.type = type;
  }
  
  /*
   * get player id
   */
  public int getId() {
    return id;
  }
  
  /*
   * set player id
   */
  public void setId(int id) {
    this.id = id;
  }

}
