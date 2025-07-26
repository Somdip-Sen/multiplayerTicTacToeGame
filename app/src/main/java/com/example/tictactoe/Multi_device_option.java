package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

public class Multi_device_option extends AppCompatActivity {
    private int me;
    private String name;
    Button bluetoothmode, wifimode;
    Animation scaleup,scaledown;

    //bluetooth Support
    BluetoothAdapter bluetoothAdapter;
    Intent enableBtIntent;
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_device_option);
        Intent i = getIntent();
        name = i.getStringExtra("name");
        me = i.getIntExtra("me",0);
        bluetoothmode = findViewById(R.id.bluetooth);
        wifimode = findViewById(R.id.wifi);
        scaleup = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaledown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        bluetoothmode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    bluetoothmode.startAnimation(scaleup);
                }else if(event.getAction() == MotionEvent.ACTION_DOWN){
                    bluetoothmode.startAnimation(scaledown);
                    bluetooth();
                }

                return true;
            }
        });
        wifimode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    wifimode.startAnimation(scaleup);
                }else if(event.getAction() == MotionEvent.ACTION_DOWN){
                    wifimode.startAnimation(scaledown);
                    wifi();
                }

                return true;
            }
        });
    }

    public void bluetooth() {
        // bluetooth connection here
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this,"Bluetooth is not supported in this device", Toast.LENGTH_LONG).show();
        }
        else
        {
//            if (!bluetoothAdapter.isEnabled()) {        //enable the bluetooth
//                enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
//            }
//            else{
//                Toast.makeText(this, "Bluetooth Enabled", Toast.LENGTH_LONG).show();
                bluetoothAdapter.disable();
                Intent i = new Intent(this,bluetooth_mode_select.class);
                i.putExtra("me", me); // player number
                i.putExtra("name", name); // player name
                startActivity(i);
                finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this,Menu.class);
        startActivity(i);
        finish();
    }
    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_ENABLE_BT) {
//            if (resultCode == RESULT_OK)
//            {
//                Toast.makeText(this, "Bluetooth Enabled", Toast.LENGTH_LONG).show();
//                Intent i = new Intent(this,bluetooth_mode_select.class);
//                startActivity(i);
//            }
//            else if (resultCode == RESULT_CANCELED)    Toast.makeText(this, "Bluetooth is necessary to play this mode", Toast.LENGTH_LONG).show();
//        }
//    }



    public void wifi() {

        Intent i = new Intent(this,WifiModeselect.class);
        i.putExtra("me", me); // player number
        i.putExtra("name", name); // player name
        startActivity(i);
        finish();
    }
}