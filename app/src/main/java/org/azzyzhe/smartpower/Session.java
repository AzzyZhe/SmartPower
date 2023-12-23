package org.azzyzhe.smartpower;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class Session extends Thread
{
    private BluetoothDevice _device = null;
    private BluetoothSocket _socket = null;
    private OutputStream _outStream;
    private InputStream _inStream = null;
    public boolean IsConnect = false;
    public String Name="";
    public String Address="";
    Handler _handler;
    public Session(BluetoothDevice _device,Handler _handler)
    {
        this._handler = _handler;
        this._device = _device;
        this.Name = this._device.getName();
        this.Address = this._device.getAddress();
        IsConnect = false;
        try
        {
            // 蓝牙串口服务对应的UUID。如使用的是其它蓝牙服务，需更改下面的字符串
            // UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            _socket = _device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        } catch (Exception e)
        {
            return;
        }
    }
    public void connect()
    {
        try
        {
            _socket.connect();
            _outStream = _socket.getOutputStream();
            _inStream = _socket.getInputStream();
            IsConnect = true;
        }
        catch (IOException e)
        {
            IsConnect = false;
            try {
                _socket.close();
            } catch (IOException e1)
            {
            }
            return;
        }
    }
    @Override
    public void run()
    {
        byte [] buffer = new byte [1024];
        int len = 0;
        while(true)
        {
            //从InputStream读取
            try
            {
                len = _inStream.read(buffer);//易溢出
            } catch (IOException e)
            {
                continue;
            }
            if(len> 0)
            {
                Message msg = _handler.obtainMessage();
                msg.what = 0;
                try
                {
                    msg.obj=new String(buffer, 0, len,"UTF-8");
                } catch (UnsupportedEncodingException e)
                {
                }
                _handler.sendMessage(msg);
            }
        }
    }
    public void Send(String _value) throws IOException
    {
        _outStream.write(_value.getBytes());
    }
    public void Close() throws IOException
    {
        IsConnect = false;
        _socket.close();
    }


}