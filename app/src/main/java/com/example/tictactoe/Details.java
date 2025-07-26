package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class Details extends AppCompatActivity {
    protected String player1,player2;
    protected TextInputLayout t1, t2;
    private static final int LENGTH = 12;
    private static final int MIN_LENGTH = 3;
    // some transient state for the activity instance
    String gameState;
    private static final String TAG = "Details";
    LottieAnimationView homebtn;
    Button continuebuttn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: calling..");
        // recovering the instance state
//        if (savedInstanceState != null) {
//            gameState = savedInstanceState.getString(GAME_STATE_KEY);
//        }

        setContentView(R.layout.activity_player_details);
        t1 = findViewById(R.id.firstPlayer);
        t2 = findViewById(R.id.secondPlayer);
        continuebuttn = findViewById(R.id.continue_game);
        homebtn = findViewById(R.id.home);
//        continuebuttn.playAnimation();
        homebtn.setSpeed(2.5f);
        homebtn.setOnClickListener(v -> {
            Intent i = new Intent(Details.this,MainActivity.class);
            startActivity(i);
            finish();
        });
        continuebuttn.setOnClickListener(v -> {
            // text input of the players' name
            player1 = t1.getEditText().getText().toString();
            player2 = t2.getEditText().getText().toString();

            validate(player1,player2);  //checking
        });
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putString(player1,t1.getEditText().getText().toString());
//        savedInstanceState.putString(player2,t2.getEditText().getText().toString());
//        super.onSaveInstanceState(savedInstanceState);
//
//    }
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        player1 =
//    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: calling..");
        homebtn.playAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: calling..");
        homebtn.pauseAnimation();


        Context context = getApplicationContext();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        if (!taskInfo.isEmpty()) {
            ComponentName topActivity = taskInfo.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                //stopService(music_player);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: calling..");
        //startService(music_player);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: calling..");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this,Menu.class);
        startActivity(i);
        finish();
        Log.d(TAG, "onBackPressed: calling..");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: calling..");
    }

    private void validate(String player1, String player2) {
        if (!player1.equals("") && !player2.equals(""))
        {
            t1.setError(null);
            t2.setError(null);
            if (player1.length()<MIN_LENGTH) t1.setError("Too short : Minimum length is "+ MIN_LENGTH);
            if (player2.length()<MIN_LENGTH) t2.setError("Too short : Minimum length is "+ MIN_LENGTH);
            if(player1.length()<=LENGTH && player2.length()<=LENGTH && player1.length()>=MIN_LENGTH && player2.length()>=MIN_LENGTH) {
                Intent i = new Intent(this, Game.class);
                i.putExtra("player1", player1);
                i.putExtra("player2", player2);
                startActivity(i);
                finish();
            }
        }
        else
        {
            if (player1.equals(""))
            {
                if (!player2.equals(""))t2.setError(null);
                t1.setError("Enter a name");
            }
            if(player2.equals(""))
            {
                if (!player1.equals(""))t1.setError(null);
                t2.setError("Enter a name");
            }
        }
    }
}