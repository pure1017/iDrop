package models;

public class Message {

  private boolean moveValidity;

  private int code;

  private String message;
  
  /** Create Message.
   * 
   * @param messageMoveValidity moveValidity
   * @param messageCode status
   * @param messageMessage message content
   */
  public Message(boolean messageMoveValidity, int messageCode, String messageMessage) {
    moveValidity = messageMoveValidity;
    code = messageCode;
    message = messageMessage;
  }
  
  /*
   * check whether the move is valid
   */
  public boolean isMoveValidity() {
    return moveValidity;
  }
  
  /*
   * set the validity of the move
   */
  public void setMoveValidity(boolean moveValidity) {
    this.moveValidity = moveValidity;
  }
  
  /*
   * get the status code
   */
  public int getCode() {
    return code;
  }
  
  /*
   * set the status code
   */
  public void setCode(int code) {
    this.code = code;
  }
  
  /*
   * get the message
   */
  public String getMessage() {
    return message;
  }

  /*
   * set the message
   */
  public void setMessage(String message) {
    this.message = message;
  }

}
