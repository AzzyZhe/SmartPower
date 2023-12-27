package org.azzyzhe.smartpower;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PreferenceActivity extends AppCompatActivity {

    //这边也是之前一堆static
    TextView baseVolt_disp = null;
    TextView baseVolt_input = null;
    Button button_adjust = null;
    SharedPreferences sharedPreferences;
    boolean b_button_isAdjusting = false;
    double baseVolt = 5.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toast.makeText(this, "Preference页onCreate执行", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_preferencepage); //之前忘记先聚焦到此页了
        sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);

        b_button_isAdjusting = false;

        baseVolt_disp = findViewById(R.id.text_baseVoltage_disp);
        baseVolt_input = findViewById(R.id.text_baseVoltage_input);
        button_adjust = findViewById(R.id.button_baseVoltage_adjust);

        if(button_adjust == null) {
            Log.e("Vital","NullButton_adjust");
        }

        baseVolt = sharedPreferences.getFloat("baseVoltage",5.0f);
        baseVolt_disp.setText(String.format("%.3fV", baseVolt));

        button_adjust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!b_button_isAdjusting) {
                    baseVolt = sharedPreferences.getFloat("baseVoltage",5.0f);
                    baseVolt_input.setText(String.format("%.3f",baseVolt));

                    baseVolt_disp.setVisibility(View.INVISIBLE);
                    baseVolt_input.setVisibility(View.VISIBLE);

                    button_adjust.setText("确认");
                    b_button_isAdjusting = true;
                } else {
                    String inputV = baseVolt_input.getText().toString();
                    double newVolt = Double.parseDouble(inputV);
                    if(newVolt < 0.0 || newVolt > 10.0) {
                        Toast.makeText(PreferenceActivity.this, "不合理的输入值", Toast.LENGTH_SHORT).show();

                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        baseVolt = newVolt;
                        Log.d("PreferenceActivity",String.format("PreferencePage:%fV",baseVolt));

                        editor.putFloat("baseVoltage", (float)baseVolt);
                        editor.apply();
                        Toast.makeText(PreferenceActivity.this, String.format("已修改基准电压为%.3fV", newVolt), Toast.LENGTH_SHORT).show();

                        baseVolt = sharedPreferences.getFloat("baseVoltage",5.0f);
                        baseVolt_disp.setText(String.format("%.3fV",baseVolt));

                        baseVolt_disp.setVisibility(View.VISIBLE);
                        baseVolt_input.setVisibility(View.INVISIBLE);

                        button_adjust.setText("调整");
                        b_button_isAdjusting = false;

//                        Intent intent = new Intent(ConnectActivity.this, DeviceActivity.class);
//                        startActivity(intent);

//                        finish();
                    }
                }
            }
        });
    }

}