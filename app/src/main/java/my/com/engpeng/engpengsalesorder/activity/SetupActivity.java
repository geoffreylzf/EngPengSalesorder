package my.com.engpeng.engpengsalesorder.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import my.com.engpeng.engpengsalesorder.R;

public class SetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        setTitle("Setup Management");
    }
}
