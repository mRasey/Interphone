package com.codemine.talk2me;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final int NEW_MSG = 1;
    ListView contractsList;
    List<Contact> contacts = new ArrayList<>();
    MySQLiteOpenHelper mySQLiteOpenHelper;
    SQLiteDatabase sqLiteDatabase;
    JSONObject contractJsonInfo;
    JSONObject callBackJson;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NEW_MSG:
                    try {
                        updateContacts(callBackJson.getString("name"), callBackJson.getString("msg"));//更新联系人列表
                        ContactsAdapter contactsAdapter = new ContactsAdapter(MainActivity.this, R.layout.contact_item, contacts);
                        contractsList.setAdapter(contactsAdapter);
                        break;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "data.db", null, 1);//创建数据库
        sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();


        initContacts(); // 初始化联系人列表
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
                startActivity(intent);
            }
        });
    }

    /**
     * 从数据库读取联系人列表
     */
    public void initContacts() {
        contacts.add(new Contact("billy", "hahaha", "now", R.drawable.head));
        contacts.add(new Contact("wang", "2333", "now", R.drawable.head));
        Cursor cursor = sqLiteDatabase.query("CONTRACTS", null, null, null, null, null, null);
        while(cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            String msg = cursor.getString(cursor.getColumnIndex("msg"));
            contacts.add(new Contact(name, msg, time, R.drawable.head));
        }
    }

    /**
     * 更新联系人列表
     */
    public void updateContacts(String name, String msg) {
        ContentValues values = new ContentValues();
        values.put("time", getCurrentTime());
        values.put("msg", msg);
        sqLiteDatabase.update("CONTRACTS", values, "name=?", new String[]{name});
    }

    /**
     * 向数据库添加联系人
     */
    public void insertContact(String name, String msg) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("time", getCurrentTime());
        values.put("msg", msg);
        sqLiteDatabase.insert("CONTRACTS", null, values);
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
        sqLiteDatabase.insert("CHAT", null, values);
    }

}
