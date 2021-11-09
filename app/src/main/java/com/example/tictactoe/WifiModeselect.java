package com.example.tictactoe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.InetAddresses;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WifiModeselect extends AppCompatActivity {
    WifiP2pManager.Channel channel;
    WifiP2pManager manager;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;
    List<WifiP2pDevice> peers;
    String[] deviceNames;
    WifiP2pDevice[] devices;
    ArrayAdapter<String> arrayAdapter;
    private int port = 8888;
    Socket socket;
    Server server;
    Client client;
    ListView listOfDevices;
    TextView tv;
    LocationManager Lmanager;
    private static final String TAG = "WifiModeSelect";
    protected boolean is_server = false, is_connected = false;
    int me;
    String name, otherPlayerName;
    Intent intent;
    LottieAnimationView findAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_mode_select);
        intent = getIntent();
        name =intent.getStringExtra("name");
        me = intent.getIntExtra("me", 0);
        findAnimation = findViewById(R.id.find);

        startingAssignment();
    }

    private void startingAssignment() {
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);  // register your application with the Wi-Fi P2P framework by calling initialize()
        receiver = new WifiDirectBroadcastReceiver(manager, channel, this, this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        peers = new ArrayList<WifiP2pDevice>();
        listOfDevices = findViewById(R.id.DeviceList);
        tv = findViewById(R.id.tv3);

//        statusCheck();

        Lmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (!Lmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            showToast("Location Service Needed");
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//        }

    }

    private void showToast(String msg) {
        Toast.makeText(WifiModeselect.this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        Log.d(TAG, "onPause: calling..");
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.d(TAG, "onStop: calling..");
//    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: calling..");
//        unregisterReceiver(receiver);
    }

    private void send(String player_name){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (is_server) server.write(player_name.getBytes());
                else client.write(player_name.getBytes());
            }
        });
    }




    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            if (!wifiP2pDeviceList.equals(peers)) {
                peers.clear();
                peers.addAll(wifiP2pDeviceList.getDeviceList());

                deviceNames = new String[wifiP2pDeviceList.getDeviceList().size()];
                devices = new WifiP2pDevice[wifiP2pDeviceList.getDeviceList().size()];

                int index = 0;
                for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
                    deviceNames[index] = device.deviceName;
                    devices[index] = device;
                    index++;
                }
            }
            arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNames);
            listOfDevices.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();


            if (peers.size() == 0) {
                showToast("No device Found");
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress grpOwnerAddress = info.groupOwnerAddress;
            intent = new Intent(WifiModeselect.this, Game_multi.class);
            intent.putExtra("me", me);
            intent.putExtra("name", name);
            if (info.groupFormed && info.isGroupOwner)
            {
                is_server = true;
                server = new Server();
                server.start();
                showToast("Connecting ...");
            }
            else if (info.groupFormed){
                client = new Client(grpOwnerAddress);
                intent.putExtra("grpOwnerAddress", grpOwnerAddress);
                client.start();
                showToast("Connecting ...");
            }

            intent.putExtra("port", port);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (socket != null) {
            if (socket.isConnected()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(TAG, "onBackPressed: ", e);
                }
            }
        }
        Intent i = new Intent(this, Menu.class);
        startActivity(i);
        // close connection
    }
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be Disabled. Please turn on the location")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        showToast("Your location service is needed for this facility.");
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @SuppressLint("MissingPermission")
    public void client(View view) {
        if (!Lmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
        findAnimation.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);
        findAnimation.playAnimation();

        checkPermission();

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                showToast("Discovery Started");
                tv.setText("Available devices");
                listOfDevices.setBackgroundColor(ContextCompat.getColor(WifiModeselect.this,R.color.listViewBackground));
            }
            @Override
            public void onFailure(int reason) {
                showToast("Discovery Failed, Turn on Wi-Fi");
            }
        });

        listOfDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //obtain a peer from the WifiP2pDeviceList
                WifiP2pDevice device;
                WifiP2pConfig config = new WifiP2pConfig();
                device = devices[position];
                config.deviceAddress = device.deviceAddress;

                checkPermission();
                manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: connected");
                    }

                    @Override
                    public void onFailure(int reason) {
                        showToast("Connection Failed");
                    }
                });
            }
        });
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults){
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public class Client extends Thread{
        String hostAdd;
        private InputStream inputStream;
        private OutputStream outputStream;

        public void write(byte[] bytes){

            try{
                outputStream.write(bytes);
            } catch (Exception e) {
                Log.e(TAG, "Exception", e);
            }
        }

        public Client(InetAddress hostAddress) {
            hostAdd = hostAddress.getHostAddress();
            ClientSocketClass.getData(channel, manager);
            socket = ClientSocketClass.getSocketInstance();
        }

        @Override
        public void run() {
            try {
                Log.d(TAG, "socket = " + socket);
                if (!socket.isConnected() || ClientSocketClass.newDevice) {
                    socket.connect(new InetSocketAddress(hostAdd, port), 1500);
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                    send(name);
                    ClientSocketClass.newDevice = false;
                }


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
                    if(socket!=null){
                        try{
                            if (ClientSocketClass.newDevice) {
                                inputStream = socket.getInputStream();
                                ClientSocketClass.newDevice = false;
                            }
                            bytes = inputStream.read(buffer);
                            if (bytes > 0){
                                int finalBytes = bytes;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        otherPlayerName = new String(buffer, 0 , finalBytes);
                                        intent.putExtra("OtherPlayerName", otherPlayerName);
                                        startActivity(intent);
                                    }
                                });
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                ServerSocketClass.getData(port, channel, manager);
                socket = ServerSocketClass.getSocketInstance();
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                send(name);
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
                    if(socket!=null){
                        try{
                            if (ServerSocketClass.newDevice) {
                                inputStream = socket.getInputStream();
                                ServerSocketClass.newDevice = false;
                            }
                            bytes = inputStream.read(buffer);
                            if (bytes > 0){
                                int finalBytes = bytes;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        otherPlayerName = new String(buffer, 0 , finalBytes);
                                        intent.putExtra("OtherPlayerName", otherPlayerName);
                                        intent.putExtra("is_server" , is_server);
                                        startActivity(intent);
                                    }
                                });
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

class ClientSocketClass {
    static WifiP2pManager.Channel channel;
    private ClientSocketClass() {}
    private static Socket socket;
    static WifiP2pManager manager;
    static boolean newDevice = false; // this will indicate that the connection is not brand new connection
    protected static void getData(WifiP2pManager.Channel temp_channel, WifiP2pManager temp_manager) {
        channel = temp_channel;
        manager = temp_manager;
    }
    public static Socket getSocketInstance() {
        if(socket == null || newDevice){
            socket = new Socket();
            newDevice = false;
        }


        return socket;
    }

}

class ServerSocketClass {
    private ServerSocketClass() {}
    private static ServerSocket serversocket;
    private static int port;
    private static Socket socket;
    static WifiP2pManager.Channel channel;
    static WifiP2pManager manager;
    static boolean newDevice = false; // this will indicate that the connection is not brand new connection

    protected static void getData(int port_pass, WifiP2pManager.Channel temp_channel, WifiP2pManager temp_manager) {
        channel = temp_channel;
        port = port_pass;
        manager = temp_manager;
    }
    public static Socket getSocketInstance() {

        if(socket == null){
            try {
                serversocket = new ServerSocket(port);
                socket = serversocket.accept();
            } catch (IOException e) {
                Log.e("TAG", "Exception", e);
            }
        }
        else if (newDevice){
            try {
                socket = serversocket.accept();
            } catch (IOException e) {
                Log.e("TAG", "getSocketInstance: ", e);
            }
        }
        return socket;
    }
}
