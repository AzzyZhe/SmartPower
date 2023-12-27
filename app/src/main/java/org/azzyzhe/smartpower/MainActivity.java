package org.azzyzhe.smartpower;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.bluetooth.BluetoothDevice;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {


    private BluetoothDevice _device = null;
    private Session _session = null;

    //这堆View和Handler原本都有static不知道
    TextView edxContent = null;
    TextView edxMessage = null;
    TextView txtDevice = null;
    Button btnSelect = null;
    Button btnConnect = null;
    Button btnSend = null;
    Button button_control = null;

    TextView countDown = null;
    TextView countDown_Volt = null;

    Handler _handler = null;

    SharedPreferences sharedPreferences;

//    int cnt = 0;
    double baseVolt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(MainActivity.this, "MainActivity Run!",
                Toast.LENGTH_SHORT).show();

        setContentView(R.layout.activity_mainpage);


        edxContent = findViewById(R.id.editText_1);
        edxMessage = findViewById(R.id.text_nowVoltage);
        txtDevice = findViewById(R.id.deviceInfo_BLE);
        btnSelect = findViewById(R.id.button_Select);
        btnConnect = findViewById(R.id.button_Connect);
        btnSend = findViewById(R.id.button_Send);

        countDown = findViewById(R.id.countDown);
        countDown_Volt = findViewById(R.id.countDown_Volt);

        sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
//        double baseVolt = sharedPreferences.getFloat("baseVoltage", 5.0f);


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
                baseVolt = sharedPreferences.getFloat("baseVoltage", 5.0f);
                double volt = baseVolt/1024*Integer.parseInt(volt_s);
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
        button_control = findViewById(R.id.button_defaultControl);
        button_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseVolt = sharedPreferences.getFloat("baseVoltage", 5.0f);
                if(baseVolt < 5.0) {
                    Toast.makeText(MainActivity.this, "调整电压包含5.0V，高于当前测量基准电压，无法使用默认控制", Toast.LENGTH_LONG).show();
                } else {
                    // 在新线程中执行耗时操作
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 执行耗时操作的代码
                            final double[] volt_set = new double[]{3.0, 4.0, 5.0};
                            int volt_send = (int)(3.0/baseVolt*1023);
                            for(int i = 0; i < 3; i++) {
                                volt_send = (int)(volt_set[i]/baseVolt*1023);
                                try {
                                    _session.Send(String.format("(%d)",volt_send));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                int finalI = i;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        countDown_Volt.setText(String.format("%.2fV",volt_set[finalI]));
                                    }
                                });
                                for(int j = 0; j < 10; j++) {
                                    int finalJ = j;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            countDown.setText(String.format("%ds", 10 - finalJ));
                                        }
                                    });
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }

                            volt_send = (int)(3.0/baseVolt*1023);
                            try {
                                _session.Send(String.format("(%d)",volt_send));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    countDown_Volt.setText(String.format("%.2fV", 3.0));
                                    countDown.setText("结束");
                                }
                            });
                        }
                    }).start();
                }
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
//        if (itemId == R.id.info_page) {
//            Toast.makeText(this, "InfoPage还没做完", Toast.LENGTH_SHORT).show();
//        } else if (itemId == R.id.change_Preference) {
//            Toast.makeText(this, "Connect", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
//            startActivity(intent);
//        }
        if (itemId == R.id.change_Preference) {
//            Toast.makeText(this, "进入设置页面", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, PreferenceActivity.class);
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

            button_control.setVisibility(View.VISIBLE);
            countDown.setVisibility(View.VISIBLE);
            countDown_Volt.setVisibility(View.VISIBLE);
        }
        else
        {
            Toast.makeText(MainActivity.this,
                    "连接失败",
                    Toast.LENGTH_LONG).show();
            btnSend.setVisibility(View.INVISIBLE);
//            button_control.setVisibility(View.INVISIBLE);
        }
    }
    public void btnSend_Click(View v)//发送消息
    {
        try
        {
            baseVolt = sharedPreferences.getFloat("baseVoltage",5.0f);
            float volt_set = Float.parseFloat(edxContent.getText().toString());
            if(volt_set > baseVolt) {
                Toast.makeText(MainActivity.this, "设置电压不能高过当前测量基准电压", Toast.LENGTH_LONG).show();
            } else {
                int volt_send = (int)(volt_set/baseVolt*1023);
                _session.Send(String.format("(%d)",volt_send));
            }

        } catch (IOException e)
        {
            Toast.makeText(MainActivity.this,
                    "发送失败",
                    Toast.LENGTH_LONG).show();
        }
    }

}