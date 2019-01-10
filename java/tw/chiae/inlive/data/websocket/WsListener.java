package tw.chiae.inlive.data.websocket;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public interface WsListener<Data> {
    /**
     * Handle the data, often display it.
     * <p>This method would be called on main thread.</p>
     */
    void handleData(Data data);
}
