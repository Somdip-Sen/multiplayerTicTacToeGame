package com.example.tictactoe;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class GameWifiBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private Game_multi activity;
    private Context context;
    //    public boolean isEnabled;
    GameWifiBroadcastReceiver(){}

    public GameWifiBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, Game_multi activity, Context context) {
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action.equalsIgnoreCase(WifiManager.WIFI_STATE_CHANGED_ACTION)){
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            if (wifiState == WifiManager.WIFI_STATE_DISABLED){
                Toast.makeText(context, "Wifi should be enabled", Toast.LENGTH_SHORT).show();
            }

            //call your method
        }

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            Toast.makeText(context, "Wifi should be enabled", Toast.LENGTH_SHORT).show();
            // check if wifi is enabled and notify corresponding activity
//            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
//            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) { // Check to see if Wi-Fi P2P is on and supported
//                // Wifi P2P is enabled
//                isEnabled = true;
//            } else {
//                // Wi-Fi P2P is not enabled
//                isEnabled = false;
//            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // get list of current peers
//            if (manager != null) {
//                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//                    return;
//                manager.requestPeers(channel, activity.peerListListener);
//
//            }
        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            // respond to new connection or disconnection
//            if (manager!=null){
//                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
//                if (networkInfo.isConnected()){
//                    //activity.is_connected = true;
//                    manager.requestConnectionInfo(channel, activity.connectionInfoListener);
//                }
//            }
        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing

        }

    }
}

