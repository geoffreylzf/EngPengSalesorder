package my.com.engpeng.engpengsalesorder.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import my.com.engpeng.engpengsalesorder.R;

public class EnterPriceActivity extends AppCompatActivity {

    private EditText etValue;
    private Button btnSave, btnCancel;

    public static final String PRICE = "PRICE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_price);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = 550;
        this.getWindow().setAttributes(params);
        this.setFinishOnTouchOutside(false);

        setTitle("Enter Price");

        etValue = findViewById(R.id.enter_price_et_value);
        btnSave = findViewById(R.id.enter_price_btn_save);
        btnCancel = findViewById(R.id.enter_price_btn_cancel);

        setupListener();
    }

    private void setupListener(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etValue.getText().toString().equals("")){
                    Intent data = new Intent();
                    data.putExtra(PRICE, Double.parseDouble(etValue.getText().toString()));
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
