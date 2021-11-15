package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Random;


public class Game extends AppCompatActivity{
    Button play_again_button, switch_player_button;
    Board board;
    String player1,player2,text;
    TextView tv1,tv2,score1,score2,winner,turn;
    LottieAnimationView celebration, badge1, badge2, cup1, cup2;
    private boolean flag=false;
    private MediaPlayer win_music;
    Thread thread,thread1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        score1 =findViewById(R.id.score_player1);
        score2 = findViewById(R.id.score_player2);
        turn = findViewById(R.id.turn);
        celebration = findViewById(R.id.win);
        badge1 = findViewById(R.id.player1Badge);
        badge2 = findViewById(R.id.player2Badge);
        cup1 = findViewById(R.id.cup1);
        play_again_button = (Button)findViewById(R.id.play_again);
        switch_player_button = (Button)findViewById(R.id.player_switch);
//        cup2 = findViewById(R.id.cup2);
        badge1.setVisibility(View.INVISIBLE);
        badge2.setVisibility(View.INVISIBLE);
        Intent i = getIntent();
        player1 = i.getStringExtra("player1");
        player2 = i.getStringExtra("player2");
        board = findViewById(R.id.board3);
        win_music = MediaPlayer.create(this, R.raw.win);
        play_again_button.setVisibility(View.INVISIBLE);
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
                        playAgain();
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


        cup1.addAnimatorListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                cup1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                cup1.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) { }
            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
//        cup2.addAnimatorListener(new Animator.AnimatorListener() {
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//                cup2.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                if (flag) {
//                    cup2.setSpeed(-1);
//                    cup2.playAnimation();
//                    flag = false;
//                }
//                else{
//                    cup2.setSpeed(1);
//                }
//            }

//            @Override
//            public void onAnimationCancel(Animator animation) { }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) { }
//        });

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
                                celebration.playAnimation();
                                flag = true;
                                cup1.playAnimation();
                                win_music.start();
                                board.score1++;
                            }
                            else if (board.match_winner == 2 ) {
                                turn.setText(player2 + " Won");
                                celebration.playAnimation();
                                flag = true;
                                cup1.playAnimation();
                                win_music.start();
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

//                                text = "!!! " + player1 + " is Winning !!!";
//                                winner.setText(text);
//                                winner.setTextColor(getResources().getColor(R.color.player1));
//                                winner.setTextSize(17);
//                                winner.setTypeface(null, Typeface.NORMAL);
                            } else if (board.score1 < board.score2) {
                                badge1.pauseAnimation();
                                badge1.setVisibility(View.INVISIBLE);
                                badge2.setVisibility(View.VISIBLE);
                                badge2.playAnimation();
//                                text = "!!! " + player2 + " is Winning !!!";
//                                winner.setText(text);
//                                winner.setTextColor(getResources().getColor(R.color.player2));
//                                winner.setTextSize(17);
//                                winner.setTypeface(null, Typeface.NORMAL);
                            } else if (board.score2 != 0) {
//                                winner.setText("!!! Match is Draw !!!");
//                                winner.setTextColor(getResources().getColor(R.color.teal_700));
//                                winner.setTextSize(17);
//                                winner.setTypeface(null, Typeface.NORMAL);
                                badge2.playAnimation();
                                badge2.setVisibility(View.VISIBLE);
                                badge1.setVisibility(View.VISIBLE);
                                badge1.playAnimation();
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

        thread = new Thread(runnable);
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
        thread1 = new Thread(runnable1);
        thread1.start();
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

    public void playAgain() {
        board.game_reset();
        board.got_winner = false;
        board.invalidate();
        Random rand = new Random();
        board.player = rand.nextInt(2)+1;
        set_turn();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        Toast.makeText(this,"Game Resumed",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}



