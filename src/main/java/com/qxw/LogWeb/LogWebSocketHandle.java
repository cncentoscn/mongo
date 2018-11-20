package com.qxw.LogWeb;
import java.io.InputStream;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * WebSocket + tail命令实现Web实时日志
 * @author qinxuewu
 * @create 18/7/10下午11:09
 * @since 1.0.0
 */
@ServerEndpoint("/log")
public class LogWebSocketHandle {

    private Process process;
    private InputStream inputStream;

    /**
     * 新的WebSocket请求开启
     */
    @OnOpen
    public void onOpen(Session session) {
         System.out.println("----------webSocket--------------");

    }

    /**
     * WebSocket请求关闭
     */
    @OnClose
    public void onClose() {
        try {
            if (inputStream != null)
                inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (process != null)
            process.destroy();
    }

    @OnError
    public void onError(Throwable thr) {
        thr.printStackTrace();
    }
}
