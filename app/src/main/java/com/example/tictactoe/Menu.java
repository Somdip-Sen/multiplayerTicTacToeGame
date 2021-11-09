package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class Menu extends AppCompatActivity {
    Button single_device_mode, multiple_device_mode;
    Animation scaleup,scaledown;
    private static final String TAG = "Menu";
    Intent music_player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        Intent i = getIntent();
        //music_player = (Intent) i.getSerializableExtra("musicplayer");
        single_device_mode = findViewById(R.id.single_player);
        multiple_device_mode = findViewById(R.id.multi_player);
        scaleup = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaledown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        single_device_mode.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    single_device_mode.startAnimation(scaleup);
                }else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    single_device_mode.startAnimation(scaledown);
                    Intent i = new Intent(Menu.this,Details.class);
                    startActivity(i);
                    finish();
                }
                Log.d(TAG, "onTouch: pressed..");

                return true;
            }

        });
        multiple_device_mode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    multiple_device_mode.startAnimation(scaleup);
                }else if(event.getAction() == MotionEvent.ACTION_DOWN){
                    multiple_device_mode.startAnimation(scaledown);
                    Intent i = new Intent(Menu.this,multiple_device_player_details.class);
                    startActivity(i);
                    finish();
                }

                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i =new Intent(this, MainActivity.class);
        startActivity(i);
        Log.d(TAG, "onBackPressed: calling..");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: calling..");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Context context = getApplicationContext();
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//        if (!taskInfo.isEmpty()) {
//            ComponentName topActivity = taskInfo.get(0).topActivity;
//            if (!topActivity.getPackageName().equals(context.getPackageName())) {
//                Log.d(TAG, "onPause: huss");
//                stopService(music_player);
//            }
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: calling..");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: calling..");
    }

}
