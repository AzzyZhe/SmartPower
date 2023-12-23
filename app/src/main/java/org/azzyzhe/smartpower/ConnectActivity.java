package org.azzyzhe.smartpower;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ConnectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectpage);
        Button button2 = (Button) findViewById(R.id.button_2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(ConnectActivity.this, "以后这里会是选择连接蓝牙设备的地方", Toast.LENGTH_SHORT).show();
                Toast.makeText(ConnectActivity.this, "还没做完所以还是先回去吧", Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent(ConnectActivity.this, DeviceActivity.class);
//                startActivity(intent);

                finish();
            }
        });
    }

}