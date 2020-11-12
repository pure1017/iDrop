package controllers;

import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsHandler;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import org.eclipse.jetty.websocket.api.Session;

/** Web socket class: DO NOT MODIFY.
 * @author Shirish Singh
 *
 */
public class UiWebSocket implements Consumer<WsHandler>  {

  // Store sessions to broadcast a message to all users
  private static final Queue<Session> SESSIONS = new ConcurrentLinkedQueue<>();

  @Override
  public void accept(final WsHandler t) {

    // On Connect
    t.onConnect(new WsConnectHandler() {

      @Override
      public void handleConnect(final WsConnectContext ctx) throws Exception {
        // TODO Auto-generated method stub
        SESSIONS.add(ctx.session);
      }

    });

    // On Close
    t.onClose(new WsCloseHandler() {

      @Override
      public void handleClose(final WsCloseContext ctx) throws Exception {
        SESSIONS.remove(ctx.session);
      }
    });
  }

  public static Queue<Session> getSessions() {
    return SESSIONS;
  }

}
