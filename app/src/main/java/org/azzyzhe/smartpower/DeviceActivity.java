package org.azzyzhe.smartpower;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;

public class DeviceActivity extends Dialog {
    Context _context;
    public OnConfirmListener onConfirmListener;
    private ListView DeviceView;
    private BluetoothAdapter mBluetoothAdapter = null;
    public ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();

    public DeviceActivity(Context context) {
        super(context);
        this._context = context;
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        DeviceView = (ListView) findViewById(R.id.DeviceView);
        BlueInit();
        onResume();
    }

    // 初始化蓝牙
    private void BlueInit() {
        Log.v("DeviceActivity", "BlueInit Run");
        // 获取蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 请求开启蓝牙

        if (!mBluetoothAdapter.isEnabled()) {
            Log.v("DeviceActivity", "BlueInit if Run");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) _context).startActivityForResult(enableBtIntent, 1);
            Log.v("DeviceActivity","BlueInit if Done");
        }
        Log.v("DeviceActivity","BlueInit Done");
    }
    protected void onResume()
    {
        Log.v("DeviceActivity","onResume Run");
        // 将已配对的设备添加到列表中
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() == 0)
            Log.e("DeviceActivity", "EmptyBondedList");
        deviceList.clear();
        if (pairedDevices.size() > 0)
        {
            String[] nameList = new String[pairedDevices.size()];
            int i=0;
            for (BluetoothDevice device : pairedDevices)
            {
                deviceList.add(device);
                nameList[i] = device.getName() + "\n" + device.getAddress();
//                Log.v("DeviceActivity",nameList[i]);
                i++;
            }
            //创建一个ArrayAdapter
            ArrayAdapter<?> adapter=new ArrayAdapter<Object>((Activity)_context,android.R.layout.simple_expandable_list_item_1,nameList);
            DeviceView.setAdapter(adapter);
            //注册一个元素单击事件监听方法
            DeviceView.setOnItemClickListener(new DeviceClick());
        }

        Log.v("DeviceActivity","OnResume Done");
    }
    //事件按钮触发
    public class DeviceClick implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
        {
            BluetoothDevice dev = deviceList.get(position);

            Log.v("DeviceActivity", "dev item clicked");
            if (dev == null)
                Log.e("DeviceActivity", "null dev item");
            else
                Log.v("DeviceActivity", dev.getName());
            onConfirmListener.confirm(dev);
        }

    }
    public interface OnConfirmListener
    {
        public void confirm(BluetoothDevice device);
    }


}

