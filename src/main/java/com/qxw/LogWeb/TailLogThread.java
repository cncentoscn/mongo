package com.qxw.LogWeb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.websocket.Session;

/**
 * 针对每个WebSocket连接都会创建一个新的LogWebSocketHandle实例
 * 以可以不用像Servlet一样考虑线程安全问题。由于tail -f命令的输入流会阻塞当前线程，
 * 所以一定要创建一个新的线程来读取tail -f命令的返回结果：
 *
 * @author qinxuewu
 * @create 18/7/10下午11:12
 * @since 1.0.0
 */


public class TailLogThread extends Thread {

    private BufferedReader reader;
    private Session session;

    public TailLogThread(InputStream in, Session session) {
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.session = session;

    }


    @Override
    public void run() {
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                // 将实时日志通过WebSocket发送给客户端，给每一行添加一个HTML换行
                session.getBasicRemote().sendText(line + "<br>");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
