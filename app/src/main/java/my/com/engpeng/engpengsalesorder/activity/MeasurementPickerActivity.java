package my.com.engpeng.engpengsalesorder.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import my.com.engpeng.engpengsalesorder.R;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_PRICE_BY_WEIGHT;

public class MeasurementPickerActivity extends AppCompatActivity {

    private EditText etQty;
    private SeekBar sbQty, sbQtyMulti;
    private Button btnSave, btnCancel;
    private TextView tvKg;

    public static final String QUANTITY = "QUANTITY";

    //receive from intent
    private int priceByWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quantity_picker);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = 550;
        this.getWindow().setAttributes(params);
        this.setFinishOnTouchOutside(false);

        etQty = findViewById(R.id.quantity_picker_et_qty);
        sbQty = findViewById(R.id.quantity_picker_sb_qty);
        sbQtyMulti = findViewById(R.id.quantity_picker_sb_qty_multi);
        btnSave = findViewById(R.id.quantity_picker_btn_save);
        btnCancel = findViewById(R.id.quantity_picker_btn_cancel);
        tvKg = findViewById(R.id.quantity_picker_tv_kg);

        etQty.setSelection(0, etQty.getText().length());

        setupIntent();
        setupListener();
    }

    private void setupIntent(){
        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_PRICE_BY_WEIGHT)) {
            priceByWeight = intentStart.getIntExtra(I_KEY_PRICE_BY_WEIGHT, 0);
            if(priceByWeight == 1){
                setTitle("Enter Weight");
            }else{
                setTitle("Enter Quantity");
                tvKg.setVisibility(View.GONE);
            }
        }
    }

    private void setupListener(){
        sbQty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                etQty.setText(String.valueOf(i));
                etQty.setSelection(0, etQty.getText().length());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbQtyMulti.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                etQty.setText(String.valueOf(i*10));
                etQty.setSelection(0, etQty.getText().length());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra(QUANTITY, Double.parseDouble(etQty.getText().toString()));
                setResult(RESULT_OK, data);
                finish();
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
