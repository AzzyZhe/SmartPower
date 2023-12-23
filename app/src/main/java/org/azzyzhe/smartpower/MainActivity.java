//todo:更新版本号1.1

package org.azzyzhe.smartpower;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    static int cnt = 0;

    private BluetoothDevice _device = null;
    private Session _session = null;

    static TextView edxContent = null;
    static TextView edxContent2 = null
    static TextView edxMessage = null;
    static TextView txtDevice = null;
    static Button btnSelect = null;
    static Button btnConnect = null;
    static Button btnSend = null;
    static TextView baseVolt = null;

    static Handler _handler = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);


        edxContent = findViewById(R.id.editText_1);
        edxMessage = findViewById(R.id.text_nowVoltage);
        txtDevice = findViewById(R.id.deviceInfo_BLE);
        btnSelect = findViewById(R.id.button_Select);
        btnConnect = findViewById(R.id.button_Connect);
        btnSend = findViewById(R.id.button_Send);

        baseVolt = findViewById(R.id.text_baseVoltage_disp);

//        volt_display_buf = new String[];
        _handler =  new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
//                        TextView nowVoltage = findViewById(R.id.text_nowVoltage);
//                        nowVoltage.setText(msg.obj + "V");

//                        edxMessage.setText(edxMessage.getText()+"\n"+msg.obj);
//                volt_display_buf+=msg.obj;
                String recv = msg.obj.toString();
                int pos1, pos2;
                pos1 = recv.indexOf('(');
                pos2 = recv.indexOf(')');
                String volt_s = "-1023";
                if(pos1 >= pos2) {
                    pos1 = recv.indexOf('(', pos2 + 1);
                    pos2 = recv.indexOf(')', pos2 + 1);
                }
                if(pos1 + 1 < pos2)
                    volt_s = recv.substring(pos1 + 1, pos2);
                double volt = 5.0/1024*Integer.parseInt(volt_s);
                String disp = String.format("%.2fV", volt);

                edxMessage.setText(disp);
            }
        };


//        Button button1 = (Button) findViewById(R.id.button_1);
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Toast.makeText(MainActivity.this, "Refresh Pressed",
////                        Toast.LENGTH_SHORT).show();
//                TextView textView = findViewById(R.id.textView_1);
//                cnt++;
//                textView.setText(Integer.toString(cnt));
//
//
////                finish();
//            }
//        });
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDevice_Click(view);
            }
        });
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.v("MainActivity", "btnConnect Click");
//                String addr = "D4:95:04:05:7A:A7";
//                _device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(addr);
//                Log.v("MainActivity", "emmmmDevice setted");
//                if (_device == null)
//                    Log.e("MainActivity", "GG");
                btnConnect_Click(view);
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSend_Click(view);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.info_page) {
            Toast.makeText(this, "InfoPage还没做完", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.change_connection) {
            Toast.makeText(this, "Connect", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
            startActivity(intent);
        }

        return true;
    }


    public void btnDevice_Click(View v)//选择设备
    {
        Log.v("MainActivity","btnDevice_Click Run");
        final DeviceActivity _deviceDialog = new DeviceActivity(this);
        _deviceDialog.onConfirmListener = new  DeviceActivity.OnConfirmListener()
        {
            @Override
            public void confirm(BluetoothDevice device)
            {
                _device = device;
                txtDevice.setText(device.getName()+"\n"+device.getAddress());
                _deviceDialog.dismiss();
                btnConnect.setText("连接设备");
                btnConnect.setVisibility(View.VISIBLE);
                btnSend.setVisibility(View.INVISIBLE);
            }
        };
        _deviceDialog.show();
        Log.v("MainActivity","btnDevice_Click Done");
    }
    public void btnConnect_Click(View v)//连接设备
    {
        _session = new Session(_device,_handler);
        setTitle(_session.Name);
        _session.connect();
        if (_session.IsConnect)
        {
            _session.start();
            btnConnect.setVisibility(View.INVISIBLE);
            edxContent.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.VISIBLE);
            btnSend.setText("发送消息");
        }
        else
        {
            Toast.makeText(MainActivity.this,
                    "连接失败",
                    Toast.LENGTH_LONG).show();
            btnSend.setVisibility(View.INVISIBLE);
        }
    }
    public void btnSend_Click(View v)//发送消息
    {
        try
        {
            float volt_set = Float.parseFloat(edxContent.getText().toString());
            int volt_send = (int)(volt_set/5.0*1023);
            _session.Send(String.valueOf(volt_send));

        } catch (IOException e)
        {
            Toast.makeText(MainActivity.this,
                    "发送失败",
                    Toast.LENGTH_LONG).show();
        }
    }

}