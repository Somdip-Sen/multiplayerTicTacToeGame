package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.MediaPlayer;
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
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Game_multi extends AppCompatActivity{

    Board_multi board;
    String player1,player2,text;
    TextView tv1,tv2,score1,score2,turn;
    private Socket socket;
    BroadcastReceiver receiver;
    Server server;
    Client client;
    private boolean is_server;
    private final String TAG = "Game_multi";
    private int port, player;
    InetAddress grpOwnerAddress;
    private volatile int player_start;
    private boolean flag = true, flag2 = false;
    private WifiP2pManager.Channel channel;
    private  WifiP2pManager manager;
    IntentFilter intentFilter;
    LottieAnimationView celebration, badge1, badge2, cup1, cup2,  play_again_button_multi;
    private MediaPlayer win_music;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_multi);
        receiver = new GameWifiBroadcastReceiver(manager, channel, this, this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(receiver, intentFilter);
        tv1 = findViewById(R.id.tv5);
        tv2 = findViewById(R.id.tv6);
        score1 = findViewById(R.id.score_player1_multi);
        score2 = findViewById(R.id.score_player2_multi);
//        winner = findViewById(R.id.TotalWinner2);
        turn = findViewById(R.id.turn_multi);
        celebration = findViewById(R.id.win);
        badge1 = findViewById(R.id.player1Badge);
        badge2 = findViewById(R.id.player2Badge);
        cup1 = findViewById(R.id.cup1);
        cup2 = findViewById(R.id.cup2);
        play_again_button_multi = findViewById(R.id.play_again);
        win_music = MediaPlayer.create(this, R.raw.win);
        badge1.setVisibility(View.INVISIBLE);
        badge2.setVisibility(View.INVISIBLE);
        Intent i = getIntent();
        play_again_button_multi.setOnClickListener(v -> {
            // play again
            play_again_button_multi.playAnimation();
            playAgain();

        });
        is_server = i.getBooleanExtra("is_server" , false);
        port = i.getIntExtra("port", 0);
        player = i.getIntExtra("me",0);
        if (player == 1) {
            player1 = i.getStringExtra("name");
            player2 = i.getStringExtra("OtherPlayerName");
        }
        else{
            player2 = i.getStringExtra("name");
            player1 = i.getStringExtra("OtherPlayerName");
        }
        board = (Board_multi)findViewById(R.id.board4);
        cup1.addAnimatorListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                cup1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (flag2) {
                    cup1.setSpeed(-1);
                    cup1.playAnimation();
                    flag2 = false;
                }
                else{
                    cup1.setSpeed(1);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) { }
            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
        cup2.addAnimatorListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                cup2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (flag2) {
                    cup2.setSpeed(-1);
                    cup2.playAnimation();
                    flag2 = false;
                }
                else{
                    cup2.setSpeed(1);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });

        if(is_server) {
            socket = ServerSocketClass.getSocketInstance();
            server = new Server(getApplicationContext());
            server.start();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Log.e(TAG, "onCreate: ",e );
            }
            Random rand = new Random();
            player_start = rand.nextInt(2) + 1;
            set_turn();
            if (player_start == 1) {
                send(player1);
            }
            else {
                send(player2);
            }
            try {
                board.is_server = is_server;
            }
            catch(Exception e) {
                Log.e(TAG, "onCreate: ",e );
            }
        }
        else {
            socket = ClientSocketClass.getSocketInstance();
            grpOwnerAddress = (InetAddress)i.getSerializableExtra("grpOwnerAddress");
            client = new Client(grpOwnerAddress,getApplicationContext());
            client.start();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Log.e(TAG, "onCreate: ",e );
            }
        }
        board.player = player;


        Handler handler = new Handler();
        @SuppressLint("SetTextI18n") Runnable runnable = () -> {
            try {
                while(true) {
                    if (board.got_winner && board.flag == 1) {
                        //winner();
                        win_music.start();
                        handler.post(() -> {
                            // match winner update
                            if (board.match_winner == 1)
                            {
                                turn.setText(player1 + " Won");
                                flag2 = true;
                                cup1.playAnimation();

                                if (player == board.match_winner)
                                    celebration.playAnimation();
                                board.score1++;
                            }
                            else if (board.match_winner == 2 ) {
                                turn.setText(player2 + " Won");
                                flag2 = true;
                                cup2.playAnimation();
                                if (player == board.match_winner)
                                    celebration.playAnimation();
                                board.score2++;
                            }

                            // match score update
                            score1.setText(String.valueOf(board.score1));
                            score2.setText(String.valueOf(board.score2));

                            // Game winner update
                            if (board.score1 > board.score2) {
                                badge2.pauseAnimation();
                                badge2.setVisibility(View.INVISIBLE);
                                badge1.setVisibility(View.VISIBLE);
                                badge1.playAnimation();
//                                winner.setText(text);
//                                winner.setTextColor(getResources().getColor(R.color.player1));
//                                winner.setTextSize(17);
//                                winner.setTypeface(null, Typeface.NORMAL);
                            } else if (board.score1 < board.score2) {
                                badge1.pauseAnimation();
                                badge1.setVisibility(View.INVISIBLE);
                                badge2.setVisibility(View.VISIBLE);
                                badge2.playAnimation();
//                                winner.setText(text);
//                                winner.setTextColor(getResources().getColor(R.color.player2));
//                                winner.setTextSize(17);
//                                winner.setTypeface(null, Typeface.NORMAL);
                            } else if (board.score2 != 0) {
                                badge2.playAnimation();
                                badge2.setVisibility(View.VISIBLE);
                                badge1.setVisibility(View.VISIBLE);
                                badge1.playAnimation();
//                                winner.setText("!!! Match is Draw !!!");
//                                winner.setTextColor(getResources().getColor(R.color.teal_700));
//                                winner.setTextSize(17);
//                                winner.setTypeface(null, Typeface.NORMAL);
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

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
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
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Log.e(TAG, "reGame: ",e );
        }
        set_turn();
        if (player_start == 1) {
            send(player1);
        } else {
            send(player2);
        }
    }

    public void playAgain() {
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
            Log.d(TAG, "onClick: running");
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        send("quit");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Log.e(TAG, "onBackPressed: ",e );
        }
        if(is_server) {
            channel = ServerSocketClass.channel;
            manager = ServerSocketClass.manager;
            ServerSocketClass.newDevice = true;

        }
        else {
            channel = ClientSocketClass.channel;
            manager = ClientSocketClass.manager;
            ClientSocketClass.newDevice = true;
        }

        // close connection
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "onFailure: ");
            }
        });
        Intent i = new Intent(this, Menu.class);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(is_server) {
            channel = ServerSocketClass.channel;
            manager = ServerSocketClass.manager;
            ServerSocketClass.newDevice = true;

        }
        else {
            channel = ClientSocketClass.channel;
            manager = ClientSocketClass.manager;
            ClientSocketClass.newDevice = true;
        }

        // close connection
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    private void send(String msg){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "messaging: " + msg);
                if (is_server) server.write(msg.getBytes());
                else client.write(msg.getBytes());
            }
        });
    }



    public class Client extends Thread{
        String hostAdd;
        private InputStream inputStream;
        private OutputStream outputStream;
        String msg;
        Context context;
        private MediaPlayer opponent_click;

        public void write(byte[] bytes){

            try{
                outputStream.write(bytes);
                outputStream.flush();
            } catch (Exception e) {
                Log.e(TAG, "Exception", e);
            }
        }

        public Client(InetAddress hostAddress, Context context) {
            this.context = context;
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
            opponent_click = MediaPlayer.create(context, R.raw.enemy_click);
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
                                Log.d(TAG, "run: " + msg);
                                if(msg.equals("quit")) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Game_multi.this,"Your friend has left the match",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                }
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
                                    board.game.alter(msg_int / 10, msg_int % 10,  (player==1)?2:1);
                                    opponent_click.start();
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
        private MediaPlayer opponent_click;
        private Context context;
        public Server(Context context){
            this.context = context;
        }

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
            opponent_click = MediaPlayer.create(context, R.raw.enemy_click);
            Handler handler = new Handler(Looper.getMainLooper());
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[1024];
                    int bytes;
                    while(socket!=null){
                        try{
                            bytes = inputStream.read(buffer);
                            if (bytes > 0) {
                                int finalBytes = bytes;
                                String msg;
                                int location;
                                msg = new String(buffer, 0, finalBytes);
                                Log.d(TAG, "run: " + msg);
                                if (msg.equals("quit")) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Game_multi.this, "Your friend has left the match", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                }
                                if (msg.equals("player_choose")) {
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
                                Log.d(TAG, "run: location = " + location);
                                opponent_click.start();
                                board.game.alter(location / 10, location % 10, (player == 1) ? 2 : 1);
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



