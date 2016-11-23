package com.codemine.talk2me;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final int UPDATE_LIST = 1;
    private ListView contractsList;
    private List<Contact> contacts = new ArrayList<>();
    private SearchView searchView;
    private WifiManager wifiManager;
    private TextView localIPText;

    MySQLiteOpenHelper mySQLiteOpenHelper;
//    SQLiteDatabase sqLiteDatabase;
    JSONObject contractJsonInfo;
    JSONObject callBackJson;
    Ip getIps;
    int oldResultSize = 0;
    int nowResultSize = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case UPDATE_LIST:
                        initContacts();
                        ContactsAdapter contactsAdapter = new ContactsAdapter(MainActivity.this, R.layout.contact_item, contacts);
                        contractsList.setAdapter(contactsAdapter);
                        break;
                    default:
                        break;
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        getIps = new Ip(wifiManager);

        localIPText = (TextView) findViewById(R.id.local_ip_text);
        localIPText.setText("本机IP: " + getLocalIp());

        try {
            new Thread(getIps).start();//获取局域网内所有IP地址
        } catch (Exception e) {
            e.printStackTrace();
        }
//        mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "data.db", null, 1);//创建数据库
//        sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();


        try {
            initContacts(); // 初始化联系人列表
        } catch (Exception e) {
            e.printStackTrace();
        }

        searchView = (SearchView) findViewById(R.id.search_view);//搜索框
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ChatActivity.class);
                intent.putExtra("contactName", query);
                intent.putExtra("contactHeadPortraitId", R.id.headPortrait);
                intent.putExtra("ipAddr", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.equals(""))
                    contractsList.setFilterText(newText);
                else
                    contractsList.clearTextFilter();
                return false;
            }
        });

        contractsList = (ListView) findViewById(R.id.contactsList);
        final ContactsAdapter contactsAdapter = new ContactsAdapter(MainActivity.this, R.layout.contact_item, contacts);
        contractsList.setAdapter(contactsAdapter);

        contractsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Contact contact = contacts.get(position);
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("contactName", contact.name);
                intent.putExtra("contactHeadPortraitId", contact.headPortraitId);
                intent.putExtra("ipAddr", contact.name);
                startActivity(intent);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(5000);
                        nowResultSize = Ip.result.size();
                        if(oldResultSize == nowResultSize)
                            continue;
                        Message message = new Message();
                        message.what = UPDATE_LIST;
                        handler.sendMessage(message);
                        oldResultSize = nowResultSize;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 从数据库读取联系人列表
     */
    public void initContacts() throws Exception {
        contacts.clear();
//        contacts.add(new Contact("billy", "hahaha", "now", R.drawable.head));
//        contacts.add(new Contact("wang", "2333", "now", R.drawable.head));
//        getIps.PingAll().run();
//        HashSet<String> hashSet = new HashSet<>();
//        hashSet.addAll(getIps.result);
        for(String ipAddr : getIps.ping) {
            contacts.add(new Contact(ipAddr, "", "", R.drawable.head));
        }
        System.out.println("result: " + getIps.result.size());

//        Cursor cursor = sqLiteDatabase.query("CONTRACTS", null, null, null, null, null, null);
//        while(cursor.moveToNext()) {
//            String name = cursor.getString(cursor.getColumnIndex("name"));
//            String time = cursor.getString(cursor.getColumnIndex("time"));
//            String msg = cursor.getString(cursor.getColumnIndex("msg"));
//            contacts.add(new Contact(name, msg, time, R.drawable.head));
//        }
    }

    /**
     * 更新联系人列表
     */
    public void updateContacts(String name, String msg) {
        ContentValues values = new ContentValues();
        values.put("time", getCurrentTime());
        values.put("msg", msg);
//        sqLiteDatabase.update("CONTRACTS", values, "name=?", new String[]{name});
    }

    /**
     * 向数据库添加联系人
     */
    public void insertContact(String name, String msg) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("time", getCurrentTime());
        values.put("msg", msg);
//        sqLiteDatabase.insert("CONTRACTS", null, values);
    }

    /**
     * 获取当前时间
     * @return
     */
    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    /**
     * 向聊天信息数据库插入新的信息
     * @param from
     * @param to
     * @param msg
     */
    public void insertNewMsg(String from, String to, String msg) {
        ContentValues values = new ContentValues();
        values.put("from", from);
        values.put("to", to);
        values.put("msg", msg);
        values.put("time", getCurrentTime());
//        sqLiteDatabase.insert("CHAT", null, values);
    }

    public String getLocalIp() {
//        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return intToIp(ipAddress);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

}
