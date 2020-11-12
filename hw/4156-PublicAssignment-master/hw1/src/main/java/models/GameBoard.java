package models;

public class GameBoard {

  private Player p1;

  private Player p2;

  private boolean gameStarted;

  private int turn;

  private char[][] boardState;

  private int winner;

  private boolean isDraw;
 
  
  /** Create GameBoard.
   * 
   * @param gbp1 playerP1
   * @param gbp2 playerP2
   * @param gbStarted gameStarted
   * @param gbturn turn
   * @param gbState boardState
   * @param gbwinner winner
   * @param gbisDraw isDraw
   */
  public GameBoard(Player gbp1, Player gbp2, boolean gbStarted,
      int gbturn, char[][] gbState, int gbwinner, boolean gbisDraw) {
    p1 = gbp1;
    p2 = gbp2;
    gameStarted = gbStarted;
    turn = gbturn;
    boardState = gbState.clone();
    winner = gbwinner;
    isDraw = gbisDraw;
  }
  
  /** To check who is the winner.
   * 
   * @param currentTurn current turn 
    @return winner player 1 or 2. if return 3, the game is draw.
   */
  public int checkWinner(int currentTurn) {
    boolean isFull = false;
    char[][] boardState = getBoardState();
    for (int i = 0; i < boardState.length; i++) {
      if ((boardState[i][0] == boardState[i][1] && boardState[i][1] == boardState[i][2]
          && boardState[i][0] != '\u0000') || (boardState[0][i] == boardState[1][i]
          && boardState[1][i] == boardState[2][i] && boardState[0][i] != '\u0000')) {
        return currentTurn;
      }
      for (int j = 0; j < boardState[i].length; j++) {
        isFull = true;
        if (boardState[i][j] == '\u0000') {
          isFull = false;
          break;
        }
      }
      if (isFull == false) {
        break;
      }
    }
    if ((boardState[0][0] == boardState[1][1] && boardState[1][1] == boardState[2][2]
        && boardState[0][0] != '\u0000') || (boardState[0][2] == boardState[1][1]
        && boardState[1][1] == boardState[2][0] && boardState[0][2] != '\u0000')) {
      return currentTurn;
    }
    
    if (isFull) {
      return 3;
    }
    return 0;
  }
  
  /** Update winner.
   * 
   * @param currentWinner value from checkWinner
   */
  public void updateWinner(int currentWinner) {
    if (currentWinner == 1 || currentWinner == 2) {
      setWinner(currentWinner);
    } else if (currentWinner == 3) {
      setDraw(true);
    }
  }
  
  /*
   * get object Player1
   */
  public Player getP1() {
    return p1;
  }
  
  /*
   * set object Player1
   */
  public void setP1(Player p1) {
    this.p1 = p1;
  }
  
  /*
   * get object Player2
   */
  public Player getP2() {
    return p2;
  }
  
  /*
   * set object Player2
   */
  public void setP2(Player p2) {
    this.p2 = p2;
  }
  
  /*
   * get whether the game is started
   */
  public boolean isGameStarted() {
    return gameStarted;
  }
  
  /*
   * set game status, whether it is started
   */
  public void setGameStarted(boolean gameStarted) {
    this.gameStarted = gameStarted;
  }
  
  /*
   * get current turn
   */
  public int getTurn() {
    return turn;
  }

  /*
   * set current turn
   */
  public void setTurn(int turn) {
    this.turn = turn;
  }
  
  /*
   * get current board state
   */
  public char[][] getBoardState() {
    return boardState.clone();
  }
  
  /*
   * set current board state
   */
  public void setBoardState(char[][] boardState) {
    this.boardState = boardState.clone();
  }
  
  /*
   * get which player is the winner
   */
  public int getWinner() {
    return winner;
  }
  
  /*
   * set which player is the winner
   */
  public void setWinner(int winner) {
    this.winner = winner;
  }
  
  /*
   * get whether the game is draw
   */
  public boolean isDraw() {
    return isDraw;
  }
  
  /*
   * set draw for the game
   */
  public void setDraw(boolean isDraw) {
    this.isDraw = isDraw;
  }
}
