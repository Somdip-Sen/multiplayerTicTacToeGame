package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Game_multi_bluetooth extends AppCompatActivity implements View.OnClickListener{
    Button play_again_button;
    Board_multi board;
    String player1,player2,text;
    TextView tv1,tv2,score1,score2,winner,turn;
    private Socket socket;
    Server server;
    Client client;
    private boolean is_server;
    private final String TAG = "Game_multi";
    private int port, player;
    InetAddress grpOwnerAddress;
    private volatile int player_start;
    private boolean flag = true;
    private WifiP2pManager.Channel channel;
    private  WifiP2pManager manager;
    private String name;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_multi_bluetooth);
        tv1 = findViewById(R.id.tv7);
        tv2 = findViewById(R.id.tv8);
        score1 = findViewById(R.id.score_player1_multi_bluetooth);
        score2 = findViewById(R.id.score_player2_multi_bluetooth);
        winner = findViewById(R.id.TotalWinner3);
        turn = findViewById(R.id.turn_multi_bluetooth);
        Intent i = getIntent();
        is_server = i.getBooleanExtra("is_server" , false);
        player = i.getIntExtra("me", 0);
        if (player == 1) {
            player1 = i.getStringExtra("name");
            player2 = i.getStringExtra("OtherPlayerName");
        }
        else{
            player2 = i.getStringExtra("name");
            player1 = i.getStringExtra("OtherPlayerName");
        }
        play_again_button = findViewById(R.id.play_again3);
        play_again_button.setOnClickListener(this);
        board = (Board_multi)findViewById(R.id.board5);
//        if(is_server) {
//            socket = ServerSocketClass.getSocketInstance();
//            server = new Server();
//            server.start();
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                Log.e(TAG, "onCreate: ",e );
//            }
//            Random rand = new Random();
//            player_start = rand.nextInt(2) + 1;
//            set_turn();
//            if (player_start == 1) {
//                send(player1);
//            }
//            else {
//                send(player2);
//            }
//            try {
//                board.is_server = is_server;
//            }
//            catch(Exception e) {
//                Log.e(TAG, "onCreate: ",e );
//            }
//        }
//        else {
//            socket = ClientSocketClass.getSocketInstance();
//            grpOwnerAddress = (InetAddress)i.getSerializableExtra("grpOwnerAddress");
//            client = new Client(grpOwnerAddress);
//            client.start();
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                Log.e(TAG, "onCreate: ",e );
//            }
//        }
        board.player = player;


        Handler handler = new Handler();
        @SuppressLint("SetTextI18n") Runnable runnable = () -> {
            try {
                while(true) {
                    if (board.got_winner && board.flag == 1) {
                        //winner();
                        handler.post(() -> {
                            // match winner update
                            if (board.match_winner == 1)
                            {
                                turn.setText(player1 + " Won");
                                board.score1++;
                            }
                            else if (board.match_winner == 2 ) {
                                turn.setText(player2 + " Won");
                                board.score2++;
                            }

                            // match score update
                            score1.setText(String.valueOf(board.score1));
                            score2.setText(String.valueOf(board.score2));

                            // Game winner update
                            if (board.score1 > board.score2) {
                                text = "!!! " + player1 + " is Winning !!!";
                                winner.setText(text);
                                winner.setTextColor(getResources().getColor(R.color.player1));
                                winner.setTextSize(17);
                                winner.setTypeface(null, Typeface.NORMAL);
                            } else if (board.score1 < board.score2) {
                                text = "!!! " + player2 + " is Winning !!!";
                                winner.setText(text);
                                winner.setTextColor(getResources().getColor(R.color.player2));
                                winner.setTextSize(17);
                                winner.setTypeface(null, Typeface.NORMAL);
                            } else if (board.score2 != 0) {
                                winner.setText("!!! Match is Draw !!!");
                                winner.setTextColor(getResources().getColor(R.color.teal_700));
                                winner.setTextSize(17);
                                winner.setTypeface(null, Typeface.NORMAL);
                            }
                        });
                        board.got_winner = false;
                        board.match_winner = 0;
                    }
                }
            }
            catch(Exception e)
            {
                Log.e(TAG, "onCreate: ",e );
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
        tv1.setText(player1);
        tv1.setTextColor(ContextCompat.getColor(this, R.color.player1));
        tv2.setText(player2);
        tv2.setTextColor(ContextCompat.getColor(this, R.color.player2));
        score1.setTextColor(ContextCompat.getColor(this, R.color.player1));
        score2.setTextColor(ContextCompat.getColor(this, R.color.player2));
        score1.setText(String.valueOf(board.score1));
        score2.setText(String.valueOf(board.score2));
        sendLocation();
    }

    private void sendLocation(){
        Handler handler2 = new Handler();
        Runnable runnable1 = () -> {  // constant check for send own turn
            while(true){
                if (board.row != 9 && board.col != 9 && !board.sent ){
                    String location;
                    location = Integer.toString(board.row) + Integer.toString(board.col);
                    send(location);
                    board.sent = true;
                    board.correct = false;
                    board.myTurn = false;
                    // resetting the values for next my turn
                    board.row = 9;
                    board.col = 9;
                    handler2.post(new Runnable() {
                        @Override
                        public void run() {
                            turn.setText("Opponent's turn");
                        }
                    });
                }
                if(board.got_winner) break;
            }
        };

        Thread thread1 = new Thread(runnable1);
        thread1.start();

    }


    @SuppressLint("SetTextI18n")
    protected void set_turn()
    {
        turn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        if (player_start==player) {
            turn.setText("You will start");
        }
        else {
            turn.setText("Opponent will start");
        }
        board.player_start = player_start;
        if (player_start == player) board.myTurn = true;
        sendLocation();
    }

    private void reGame(){
        Random rand = new Random();
        player_start = rand.nextInt(2) + 1;
        set_turn();
        if (player_start == 1) {
            send(player1);
        } else {
            send(player2);
        }
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if(is_server) {
            send("Regame");
            board.game_reset();
            board.invalidate();
            reGame();
        }
        else{
            board.game_reset();
            board.invalidate();
            send("player_choose");
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (is_server) {
            SetServerSocket.newDevice = true;
            SetServerSocket.close();
        }
        else {
            SetClientSocket.newDevice = true;
            SetClientSocket.close();
        }
//        SetClientSocket.close();
    }

    private void send(String msg){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {

//                Write messages
//                if (is_server) server.write(msg.getBytes());
//                else client.write(msg.getBytes());
            }
        });
    }



    public class Client extends Thread{
        String hostAdd;
        private InputStream inputStream;
        private OutputStream outputStream;
        String msg;

        public void write(byte[] bytes){
     // Write mechanism
//            try{
//                outputStream.write(bytes);
//                outputStream.flush();
//            } catch (Exception e) {
//                Log.e(TAG, "Exception", e);
//            }
        }

        public Client(InetAddress hostAddress) {
            hostAdd = hostAddress.getHostAddress();
            socket = ClientSocketClass.getSocketInstance();
        }

        @Override
        public void run() {
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "run: ",e );
            }

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[1024];
                    int bytes;
                    while(socket!=null){
                        try{

                            bytes = inputStream.read(buffer);
                            if (bytes > 0){
                                int finalBytes = bytes;
                                int msg_int;
                                msg = new String(buffer, 0 , finalBytes);
                                if(msg.equals("Regame")) {
                                    flag = true;
                                    board.game_reset();
                                    board.invalidate();
                                    continue;
                                }
                                if (flag) {
                                    // flag ON -> player starting details
                                    player_start = msg.equals(player1)?1:2;
                                    // update who will start match
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            set_turn();
                                        }
                                    });
                                    flag = false;
                                }
                                else{
                                    // flag OFF -> msg is a location
                                    msg_int = Integer.parseInt(msg);
                                    Log.d(TAG, "run: player = "  + player);
                                    board.game.alter(msg_int / 10, msg_int % 10,  (player==1)?2:1);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            turn.setText("Your turn");
                                        }
                                    });
                                    board.invalidate();
                                    board.myTurn = true;
                                }
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Exception", e);
                        }
                    }

                }
            });
        }
    }

    public class Server extends Thread {
        private InputStream inputStream;
        private OutputStream outputStream;

        public void write(byte[] bytes){
            try{
                outputStream.write(bytes);
                outputStream.flush();
            } catch (Exception e) {
                Log.e(TAG, "write: ",e );
            }
        }

        @Override
        public void run() {
            try {
                socket = ServerSocketClass.getSocketInstance();
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[1024];
                    int bytes;
                    while(socket!=null){
                        try{
                            bytes = inputStream.read(buffer);
                            if (bytes > 0){
                                int finalBytes = bytes;
                                String msg;
                                int location;
                                msg = new String(buffer, 0 , finalBytes);
                                if(msg.equals("player_choose")) {
                                    send("Regame");
                                    board.game_reset();
                                    board.invalidate();
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            reGame();
                                        }
                                    });

                                    continue;
                                }
                                location = Integer.parseInt(msg);
                                Log.d(TAG, "run: location = " + location );
                                board.game.alter(location / 10, location % 10,  (player==1)?2:1);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        turn.setText("Your turn");
                                    }
                                });
                                board.invalidate();
                                board.myTurn = true;
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Exception", e);
                        }
                    }
                }
            });

        }
    }
}



