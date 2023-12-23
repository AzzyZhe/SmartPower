package org.azzyzhe.smartpower;
import android.app.Application;
import android.content.SharedPreferences;

public class InitializationActivity extends Application {
    public class MyApp extends Application {
        @Override
        public void onCreate() {
            super.onCreate();

            // 在这里进行SharedPreferences的初始设置
            SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);

            // 检查是否是第一次启动应用
            boolean isFirstRun = sharedPreferences.getBoolean("is_first_run", true);

            if (isFirstRun) {
                // 第一次启动应用，设置初始缺省值
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", "default_username");
                editor.putInt("score", 0);

                // 将 isFirstRun 设置为 false，表示应用已经启动过一次
                editor.putBoolean("is_first_run", false);

                editor.apply();
            }
        }
    }
}
