package com.example.tictactoe;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.datatype.XMLGregorianCalendar;

public class bluetooth_mode_select extends AppCompatActivity {
    private static final int REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_DISCOVER = 1;
    private static final int STATE_CONNECTED = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_LISTENING = 3;
    private static final int STATE_CONNECTION_FAILED = 4;
    private static final int STATE_SENDING_SIGNAL = 5;
    Intent enableBtIntent;
    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    ListView list_of_server;
    TextView tv1;
    Button serverButton,clientButton;
    ArrayList<BluetoothDevice> arrayListBluetoothDevices = null;
    BluetoothDevice btDevice;
    ArrayList<String> device_list = new ArrayList<>();
    //ArrayList<String> device_address_list = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
//    private static final String APP_NAME = "Tic Tac Toe";
    private static final UUID myUUID = UUID.fromString("ab50c9a9-c703-4d87-b145-b901a28b5067");

    private static final String TAG = "bluetooth_mode_select";
    private boolean granted = false, is_server =false;
    ThreadPerTaskExecutor th;
    Runnable runnable;
    private String name;
    private int me;
    private OutputStream outputStream;
    private InputStream inputStream;
    Server server_connect;
    Client client_connect;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_mode_select);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        arrayListBluetoothDevices = new ArrayList<BluetoothDevice>();
        Intent intent = getIntent();
        name =intent.getStringExtra("name");
        me = intent.getIntExtra("me", 0);


        // I don't know why but restarting device bluetooth provides me all the devices available

        if (!bluetoothAdapter.isEnabled()) {        //enable the bluetooth
            enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        }


        //list_of_client = findViewById(R.id.List_of_client);
        list_of_server = findViewById(R.id.List_of_server);
        tv1 = findViewById(R.id.choose_server);

        checkLocationPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(this, "Bluetooth Enabled", Toast.LENGTH_SHORT).show();

            }
            else if (resultCode == RESULT_CANCELED)    Toast.makeText(this, "Bluetooth is necessary to play this mode", Toast.LENGTH_LONG).show();
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action) || BluetoothDevice.ACTION_NAME_CHANGED.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (!device_list.contains(device.getName())){
                        device_list.add(device.getName());
                        arrayListBluetoothDevices.add(device);
                        //device_address_list.add(device.getAddress());
                        arrayAdapter.notifyDataSetChanged();
                    }

            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                th.execute(runnable);
            }
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceiving: Bluetooth off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onReceiving: Bluetooth being off...");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceiving: Bluetooth on");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "onReceiving: Bluetooth being on...");
                        break;
                }
            }
        }
    };

   

    protected void checkLocationPermission() {  // requesting for device location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION);
            granted = true;
        } else {
            if (!granted)
                granted = true;
                proceedDiscovery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    proceedDiscovery();
                } else {
                    showToast("The required permissions should be allowed");

                }
                break;
            }
        }
    }


    private void proceedDiscovery() {
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver,intentFilter);

        // start discovering new device in another thread
        runnable = new Runnable() {
            @Override
            public void run() {
                bluetoothAdapter.startDiscovery();
            }
        };
                
        th = new ThreadPerTaskExecutor();
        th.execute(runnable);
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, device_list);

    }



    class ThreadPerTaskExecutor implements Executor {
        public void execute(Runnable r) {
            Thread th = new Thread(r);
            th.start();

        }


    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.cancelDiscovery();
        arrayAdapter.clear();
        unregisterReceiver(receiver);

    }

    public boolean createBond(BluetoothDevice btDevice)
            throws Exception
    {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    private void send(String player_name){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (is_server) server_connect.write(player_name.getBytes());
                else client_connect.write(player_name.getBytes());
            }
        });
    }

    public void server(View view) {
            is_server = true;
            if (!granted)
                checkLocationPermission();  // always ensure to have the location access and connect extra available devices

            // making other option disappear
            tv1.setText("");
            list_of_server.setVisibility(View.GONE);
//            clientButton = findViewById(R.id.client);
//            clientButton.setClickable(false);

            server_connect = new Server();
            server_connect.start();


//        // making own list viewable
//        arrayAdapter.notifyDataSetChanged();
//        list_of_client.setAdapter(arrayAdapter);
//        }
//        else showToast("You are joining your Friend's room");
    }

    public void client(View view) {
//        serverButton = findViewById(R.id.server);
//        serverButton.setClickable(false);
        list_of_server.setVisibility(View.VISIBLE);
        is_server = false;
        if (!granted)
            checkLocationPermission();  // always ensure to have the location access and connect extra available device
        tv1.setText("Find Your Friend");

        arrayAdapter.notifyDataSetChanged();
        list_of_server.setAdapter(arrayAdapter);
        list_of_server.setBackgroundColor(getResources().getColor(R.color.listViewBackground));

        list_of_server.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                btDevice = arrayListBluetoothDevices.get(position);
                Boolean isBonded = false;
                try {
                    isBonded = createBond(btDevice);
                    if (isBonded) {
                        //paired the device
                        showToast("Pairing...");
                        arrayAdapter.notifyDataSetChanged();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onItemClick: " + btDevice.toString());
                client_connect = new Client(btDevice);
                client_connect.start();
            }
        });

    }
    protected void showToast(String msg){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case STATE_CONNECTING:
                    showToast("Connecting");break;
                case STATE_CONNECTED:

                    // here lies the problem .... The both devices (Server and Client) connects because

                     Log.d(TAG, "handleMessage: running");
                     showToast("Connected");
                    Log.d(TAG, "All ok 1");
                     i = new Intent(bluetooth_mode_select.this, Game_multi_bluetooth.class);
                     i.putExtra("is_server", is_server);
                     i.putExtra("me", me);
                     i.putExtra("name", name);
                    Log.d(TAG, "All ok 2");
                     send(name);
                    Log.d(TAG, "All ok 3");
//                     startActivity(i);
//                     finish();
                     break;
                case STATE_LISTENING:
                    showToast("Finding Your Friend");break;
                case STATE_CONNECTION_FAILED:
                    showToast("Connection Failed");break;
                case STATE_SENDING_SIGNAL:
                    //code later
            }
            return true;
        }
    });


    class Server extends Thread{
//        private BluetoothServerSocket serverSocket;
        public BluetoothSocket socket;
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;
//         int b = BUFFER_SIZE;

        public void write(byte[] bytes){
            try{
                outputStream.write(bytes);
            } catch (Exception e) {
//                e.printStackTrace();
                Log.e(TAG, "Exception", e);
            }
        }

        @Override
        public void run() {
            super.run();
//            if (SetServerSocket.newDevice) serverButton.setClickable(true);
            SetServerSocket.getData(bluetoothAdapter, handler);
            socket = SetServerSocket.getSocketInstance();
            //SetServerSocket.getOtherDevice(socket.getRemoteDevice());
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Reading Mechanism
            while(socket!=null) {
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0){
                        String otherPlayerName = new String(buffer, 0 , bytes);
                        i.putExtra("OtherPlayerName", otherPlayerName);
                        startActivity(i);
                        finish();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "run: ", e );
                }
            }
        }
    }
    class Client extends Thread{
        private final BluetoothDevice device;
        public BluetoothSocket socket;
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;

        Client(BluetoothDevice device){
            this.device = device;
        }

        public void write(byte[] bytes){
            try{
                outputStream.write(bytes);
            } catch (Exception e) {
//                e.printStackTrace();
                Log.e(TAG, "Exception", e);
            }
        }

        @Override
        public void run() {
            super.run();
            SetClientSocket.getData(device,bluetoothAdapter,handler);
            socket = SetClientSocket.getSocketInstance();

            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "run: ", e );
            }
            while(socket!=null) {
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0){
                        Log.d(TAG, "All ok 4");
                        String otherPlayerName = new String(buffer, 0 , bytes);
                        i.putExtra("OtherPlayerName", otherPlayerName);
                        startActivity(i);
                        finish();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "run: ", e );
                }
            }
        }
    }
}
class SetClientSocket {
    private static final UUID myUUID = UUID.fromString("ab50c9a9-c703-4d87-b145-b901a28b5067");
    private static BluetoothDevice device;
    private static BluetoothAdapter bluetoothAdapter;
    private static final int STATE_CONNECTED = 1;
    private static final int STATE_CONNECTION_FAILED = 4;
    private SetClientSocket(){};
    private static BluetoothSocket socket;
    private static Handler handler;
    static boolean newDevice = false;
    protected static void getData( BluetoothDevice btdevice, BluetoothAdapter btAdapter, Handler h){
        handler = h;
        device = btdevice;
        bluetoothAdapter = btAdapter;
    }


    public static BluetoothSocket getSocketInstance() {
        if (socket == null) {
            try {
                socket = device.createRfcommSocketToServiceRecord(myUUID);
                socket.connect();
                bluetoothAdapter.cancelDiscovery();

                Message message =Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);
            } catch (IOException e) {
                Message message =Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
                e.printStackTrace();
            }
        }
        return socket;
    }
    public static void close(){
        try {
//            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
//            method.invoke(device, (Object[]) null);
//            socket.close();
            socket = null;
        } catch (Exception e) {
            Log.e("Unpair", "close: ", e);
        }
    }
}
//}

class SetServerSocket {
    private static final UUID myUUID = UUID.fromString("ab50c9a9-c703-4d87-b145-b901a28b5067");
    //private static BluetoothDevice device;
    private static BluetoothAdapter bluetoothAdapter;
    private static final int STATE_CONNECTED = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_LISTENING = 3;
    private static final int STATE_CONNECTION_FAILED = 4;
    private static final int STATE_SENDING_SIGNAL = 5;
    private static BluetoothDevice device;

    private SetServerSocket(){};
    private static BluetoothServerSocket serverSocket;
    private static BluetoothSocket socket;
    private static Handler handler;
    private static final String APP_NAME = "Tic Tac Toe";
    private static final String TAG = "SetServerSocket";
    static boolean newDevice = false;
    protected static void getData(BluetoothAdapter btAdapter, Handler h){
        handler = h;
        //serverSocket = serversocket;
//        socket = socket_client;
//        device = btdevice;
        bluetoothAdapter = btAdapter;
    }

    public static void close(){
            try {
//                Method method = device.getClass().getMethod("removeBond", (Class[]) null);
//                method.invoke(device, (Object[]) null);
//                socket.close();
                socket = null;

            } catch (Exception e) {
                e.printStackTrace();
        }
    }


    public static BluetoothSocket getSocketInstance() {
        if (socket == null) {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, myUUID);
            } catch (IOException e) {
                Log.e(TAG, "getSocketInstance: ",e );
            }
            while (socket==null){
                try{
                    Message message =Message.obtain();
                    message.what = STATE_LISTENING;
                    handler.sendMessage(message);
                    socket = serverSocket.accept();  // accept the incoming connection
                } catch (IOException e) {
                    Message message =Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                    Log.e(TAG, "getSocketInstance: ",e );
                }
                if (socket!=null){
                    Message message =Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);
                    break;
                    // code for after connection
                }
            }

        }
        return socket;
    }
}
