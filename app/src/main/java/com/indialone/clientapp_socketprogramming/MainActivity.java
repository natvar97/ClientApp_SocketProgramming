package com.indialone.clientapp_socketprogramming;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;

public class MainActivity extends AppCompatActivity {

    private EditText etMessage, etServerAddress;
    private Button btnSendMessage, btnConnect;

    private TextView tvMessages;

    private static final int PORT = 8080;

    private Socket socket;
    private PrintWriter printWriter;
    private String serverAddress;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        btnSendMessage.setOnClickListener((view) -> {
            message = etMessage.getText().toString().trim();
            String address = etServerAddress.getText().toString().trim();
            if (!message.isEmpty() && !address.isEmpty()) {
                new Thread(new ServerConnectThread(address)).start();
            }
        });

//        new Thread(new ListenServerMessagesThread()).start();

    }

    private void initializeViews() {
        etMessage = findViewById(R.id.et_message);
        etServerAddress = findViewById(R.id.et_server_address);
        btnSendMessage = findViewById(R.id.btn_send_message);
        btnConnect = findViewById(R.id.btn_connect);
        tvMessages = findViewById(R.id.tv_messages);
    }

    private class ServerConnectThread implements Runnable {

        public ServerConnectThread(String address) {
            serverAddress = address;
        }

        @Override
        public void run() {
            try {
                socket = new Socket(serverAddress, PORT);
                printWriter = new PrintWriter(socket.getOutputStream(), true);
                printWriter.write("Client: " + message);
                printWriter.flush();
                printWriter.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ListenServerMessagesThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Socket socket = new Socket(serverAddress, PORT);
                    socket.bind(socket.getLocalSocketAddress());
                    InputStreamReader reader = new InputStreamReader(socket.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String message = bufferedReader.readLine();
                    if (message.equalsIgnoreCase("over")) break;
                    else {
                        runOnUiThread(() -> {
                            tvMessages.append(message);
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}