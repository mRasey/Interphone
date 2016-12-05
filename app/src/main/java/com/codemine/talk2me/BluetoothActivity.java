package com.codemine.talk2me;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codemine.talk2me.bluetoothchat.BluetoothChatService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    ListView contractsList;
    List<Contact> contacts = new ArrayList<>();
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private BluetoothAdapter mBtAdapter;
    private ContactsAdapter contactsAdapter;
    private BluetoothChatService mChatService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        mChatService=((Data) getApplication()).getmChatService();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        findViewById(R.id.local_ip_text).setVisibility(View.GONE);
//        mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "data.db", null, 1);//创建数据库
//        sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();
        if (mBtAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.finish();
        }
        if (!mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }

        try {
            mBtAdapter.startDiscovery(); // 初始化联系人列表
        } catch (Exception e) {
            e.printStackTrace();
        }
        contractsList = (ListView) findViewById(R.id.contactsList);
        contactsAdapter = new ContactsAdapter(BluetoothActivity.this, R.layout.contact_item, contacts);
        contractsList.setAdapter(contactsAdapter);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        setResult(Activity.RESULT_CANCELED);
        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                Contact contact=new Contact(device.getName(), "", "", R.drawable.head);
                contactsAdapter.add(contact);
                contact.setAddr(device.getAddress());
            }
        }

        contractsList.setOnItemClickListener(mDeviceClickListener);


    }
    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int position, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();
            Contact contact = contacts.get(position);
            String address = contact.getAddr();

            // Create the result Intent and include the MAC address
            Intent intent = new Intent(BluetoothActivity.this, BluetoothChatActivity.class);
            intent.putExtra("device address",address);
            intent.putExtra("device name",contact.getName());
            startActivity(intent);
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    Contact contact=new Contact(device.getName(), "", "", R.drawable.head);
                    contactsAdapter.add(contact);
                    contact.setAddr(device.getAddress());
                }
                // When discovery is finished, change the Activity title
            }
        }
    };



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

}


