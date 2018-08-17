package my.com.engpeng.engpengsalesorder.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.utilities.ScheduleUtils;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private Button btn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScheduleUtils.scheduleAutoUpdate(this);

        btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HouseKeepingActivity.class));
            }
        });

        btn3 = findViewById(R.id.button3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TempSalesorderHeadActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
