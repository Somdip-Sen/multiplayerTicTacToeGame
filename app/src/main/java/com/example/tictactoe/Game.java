package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.DialogTitle;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class Game extends AppCompatActivity{
    Button play_again_button, switch_player_button;
    Board board;
    String player1,player2,text;
    TextView tv1,tv2,score1,score2,winner,turn;
    Thread a;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        tv1 = (TextView)findViewById(R.id.tv1);
        tv2 = (TextView)findViewById(R.id.tv2);
        score1 = (TextView)findViewById(R.id.score_player1);
        score2 = (TextView)findViewById(R.id.score_player2);
        winner = (TextView)findViewById(R.id.TotalWinner);
        turn = (TextView)findViewById(R.id.turn);
        Intent i = getIntent();
        player1 = i.getStringExtra("player1");
        player2 = i.getStringExtra("player2");
        play_again_button = (Button)findViewById(R.id.play_again);
        switch_player_button = (Button)findViewById(R.id.player_switch);
        play_again_button.setVisibility(View.INVISIBLE);
//        play_again_button.setOnClickListener(this);
        board = (Board)findViewById(R.id.board3);
        board.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    switch_player_button.setVisibility(View.INVISIBLE);
                    play_again_button.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });

        //Button works
        new Thread(new Runnable() {

            @Override
            public void run() {
                switch_player_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        board.player = board.player==1?2:1;
                        set_turn();


                    }
                });
                play_again_button.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void onClick(View v) {
                        board.game_reset();
                        board.got_winner = false;
                        board.invalidate();
                        Random rand = new Random();
                        board.player = rand.nextInt(2)+1;
                        set_turn();
                        showToast("New game Started");
                        switch_player_button.setVisibility(View.VISIBLE);
                        play_again_button.setVisibility((View.INVISIBLE));
                        board.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if(event.getAction() == MotionEvent.ACTION_UP){
                                    switch_player_button.setVisibility(View.INVISIBLE);
                                    play_again_button.setVisibility(View.VISIBLE);
                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                });
            }
        }).start();

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
                e.printStackTrace();
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
        set_turn();
        Handler handler1 = new Handler();
        Runnable runnable1 = () -> {  // constant check for send own turn
            while (true) {
                if(board.turn!=0){
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                            if(board.turn == 1) {
                                turn.setText(player1 + "'s turn");
                                board.turn = 0;
                            }
                            else if(board.turn == 2){
                                turn.setText(player2 + "'s turn");
                                board.turn = 0;
                            }
                        }
                    });
                }
            }
        };
        a = new Thread(runnable1);
        a.start();
    }

    @SuppressLint("SetTextI18n")
    protected void set_turn()
    {
        if (board.player==1) {
            turn.setText(player1 + " Starts");
            turn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        }
        else turn.setText(player2 + " Starts ");
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}



