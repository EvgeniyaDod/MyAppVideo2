package com.example.myappvideo2;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientConnectClass {

    private static final String TAG="myServerAPP";
    private static String serverIp = "46.0.199.93";
    int serverPort = 5000;
    Socket mySocket = null;
    ClientConnectClass(){}

    public void openConnection() throws Exception {
        closeConnection();
        try {
            mySocket = new Socket(serverIp, serverPort);
        } catch (IOException e) {
            Log.d(TAG, "Нельзя создать сокет");
        }
    }

    public void closeConnection() {
        if(mySocket!=null && !mySocket.isClosed()){
            try {
                mySocket.close();
            } catch (IOException e) {
                Log.d(TAG, "Нельзя закрыть сокет");
            }finally {
                mySocket=null;
            }
        }
        mySocket=null;
    }

    public void sendData(byte[] data) throws Exception {
        if(mySocket==null || mySocket.isClosed()){
            Log.d(TAG, "Сокет не создан или закрыт");
        }
        try {
            mySocket.getOutputStream().write(data);
            mySocket.getOutputStream().flush();
        } catch (IOException e) {
            Log.d(TAG, "Нельзя отправить данные");
        }
    }

    /*public String getData() throws IOException {
        String res = null;
        try {
            res = "";
            Socket socket = new Socket(serverIp,serverPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            res = in.readLine();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }*/


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        closeConnection();
    }
}
