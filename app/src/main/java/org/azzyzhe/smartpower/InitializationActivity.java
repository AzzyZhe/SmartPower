package org.azzyzhe.smartpower;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class InitializationActivity extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

//        Toast.makeText(InitializationActivity.this, "InitActivity Run!",
//                    Toast.LENGTH_SHORT).show();
//        并非执行在UI线程中，所以Toast不显示

        Log.i("InitializationActivity", "InitActivity Run!");

        // 在这里进行SharedPreferences的初始设置
        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);

        // 检查是否是第一次启动应用
        boolean isFirstRun = sharedPreferences.getBoolean("is_first_run", true);
//        boolean isFirstRun = true;

        if (isFirstRun) {

            // 第一次启动应用，设置初始缺省值
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("baseVoltage", 5.0f);

            // 将 isFirstRun 设置为 false，表示应用已经启动过一次
            editor.putBoolean("is_first_run", false);

            editor.apply();
        }
    }
}
