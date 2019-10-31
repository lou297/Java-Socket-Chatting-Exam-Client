package com.pingpong.chatclientexam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ChatRoomActivity extends AppCompatActivity {

    TextView tv_roomNo;
    TextView tv_chat;
    Queue<String> messageQueue;
    Button btn_send;
    EditText et_message;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        initView();

        readSocket connectSocket = new readSocket();
        connectSocket.start();

        writeSocket writeSocket = new writeSocket();
        writeSocket.start();
    }

    class writeSocket extends Thread {
        String host = "192.168.0.12";
        int port = 8888;

        @Override
        public void run() {
            try {
                Socket socket = new Socket(host, port);
                Log.d("test","채팅 연결2");
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

                while (true) {
                    sleep(100);
                    if(!messageQueue.isEmpty()) {
                        String message = messageQueue.poll();
                        Log.d("messageQueue", message+", 메세지 출력");
                        outputStream.writeObject(message);
                        outputStream.flush();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class readSocket extends Thread {
        String host = "192.168.0.12";
        int port = 7777;

        @Override
        public void run() {
            try {
                Socket socket = new Socket(host, port);
                Log.d("test","채팅 연결1");
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    sleep(100);

//                    Log.d("messageQueue", " 루프 도는중 ");
//                    if(!messageQueue.isEmpty()) {
//                        String message = messageQueue.poll();
//                        Log.d("messageQueue", message+", 메세지 출력");
//                        outputStream.writeObject(message);
//                        outputStream.flush();
//                    }
                    Log.d("Client", "대기중");
                    Object input = inputStream.readObject();
                    Log.d("Client", "출력");
                    final String str = input.toString();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            tv_chat.append(str+"\n");
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("messageQueue", " 루프 도는중13 ");
            } finally {
                Log.d("messageQueue", " 빠져나옴 ");
            }
        }
    }

    public void initView() {
        tv_roomNo = findViewById(R.id.tv_room_no);
        tv_chat = findViewById(R.id.tv_chat);
        et_message = findViewById(R.id.et_message);
        btn_send = findViewById(R.id.btn_send);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        loadRoomInfo();
    }

    public void sendMessage() {
        String message = et_message.getText().toString();
        messageQueue.add(message);
        Log.d("test","메세지 전송 : "+message);
    }

    public void loadRoomInfo() {
        Intent intent = getIntent();

        int roomNo = intent.getIntExtra("roomNo", 0);
        tv_roomNo.setText(roomNo+" 번방");
        messageQueue = new LinkedList<>();
    }
}
