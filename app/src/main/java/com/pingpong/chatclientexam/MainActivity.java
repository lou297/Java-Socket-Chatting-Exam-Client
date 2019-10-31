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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    Button button;
    Button button2;
    TextView textView;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();


    }

    class ClientThread extends Thread {

        String nickName;
        public ClientThread(String nickName) {
            this.nickName = nickName;
        }

        @Override
        public void run() {
//            String host = "localhost";
            String host = "192.168.0.12";
            int port = 9999;
            Log.d("ExamClientThread", "run");
            try {
//                Socket socket = new Socket("10.0.2.2",10000);
                Socket socket = new Socket(host, port);
                Log.d("ExamClientThread", "run2");

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(nickName);
                outputStream.flush();
                Log.d("ClientThread", "서버로 아이디 전송");

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                Object input = inputStream.readObject();
//                char strInput = inputStream.readChar();
                Log.d("ClientThread", input+"");

                if(input.equals("LoginSuccess")){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.append(nickName+"님이 채팅방에 접속하셨습니다.\n");
                        }
                    });
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initView() {
        editText = findViewById(R.id.chatRoomNickName);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        textView = findViewById(R.id.tv_socket_chat_area);

        handler = new Handler();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickName = loginCheck();
                if(nickName != null) {
                    ClientThread thread = new ClientThread(nickName);
                    thread.start();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatRoomListAcitivity.class);
                startActivity(intent);
            }
        });
    }

    public String loginCheck() {
        String nickName = editText.getText().toString();

        if(nickName != null || nickName.length() > 0) {
            return nickName;
        }
        else
            return null;
    }
}
