package models;

public class Move {

  private Player player;

  private int moveX;

  private int moveY;
  
  /** Create Move.
   * 
   * @param movePlayer current player
   * @param mx x_coordinate
   * @param my y_coordinate
   */
  public Move(Player movePlayer, int mx, int my) {
    player = movePlayer;
    moveX = mx;
    moveY = my;
  }
  
  /** When the move is valid, conduct the move.
   * 
   * @return state for nextBoardState
   */
  public char[][] conduct(GameBoard board) {
    int coordinateX = getMoveX();
    int coordinateY = getMoveY();
    char currentType = getPlayer().getType();
    char[][] currentBoardState = board.getBoardState();
    if (currentBoardState[coordinateX][coordinateY] == '\u0000') {
      currentBoardState[coordinateX][coordinateY] = currentType;
    }
    return currentBoardState;
  }
  
  /*
   * get current player
   */
  public Player getPlayer() {
    return player;
  }

  /*
   * set current player
   */
  public void setPlayer(Player player) {
    this.player = player;
  }
  
  /*
   * get x_coordinate of move
   */
  public int getMoveX() {
    return moveX;
  }
  
  /*
   * set x_coordinate of move
   */
  public void setMoveX(int moveX) {
    this.moveX = moveX;
  }

  /*
   * get y_coordinate of move
   */
  public int getMoveY() {
    return moveY;
  }
  
  /*
   * set y_coordinate of move
   */
  public void setMoveY(int moveY) {
    this.moveY = moveY;
  }

}
