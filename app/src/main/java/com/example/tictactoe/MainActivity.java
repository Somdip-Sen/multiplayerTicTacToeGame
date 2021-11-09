package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Serializable {
    LottieAnimationView lottiebuttn;
    private static final String TAG = "MainActivity";
    Intent music_player, i;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        music_player = new Intent(this, BackgroundSoundService.class);
        startService(music_player);
        text = findViewById(R.id.tv1);

        lottiebuttn = findViewById(R.id.playButton);
        lottiebuttn.playAnimation();
        YoYo.with(Techniques.Shake).duration(3000).repeat(Animation.INFINITE).playOn(text);

        lottiebuttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.FadeIn).duration(500).playOn(text);
                i = new Intent(MainActivity.this, Menu.class);
                i.putExtra("musicplayer",music_player);
                startActivity(i);
                finish();
            }
        });

    }


//#FD8800 --> note it

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: calling..");
        //if(i==null) startService(music_player);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: calling..");
        Context context = getApplicationContext();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        if (!taskInfo.isEmpty()) {
            ComponentName topActivity = taskInfo.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                stopService(music_player);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: calling..");
        startService(music_player);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: calling..");
        if (i==null){
            stopService(music_player);
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Log.d(TAG, "onBackPressed: calling..");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: calling..");
    }
}