package my.com.engpeng.engpengsalesorder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import my.com.engpeng.engpengsalesorder.utilities.ScheduleUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScheduleUtils.scheduleAutoUpdate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startActivity(new Intent(MainActivity.this, HouseKeepingActivity.class));
    }
}
