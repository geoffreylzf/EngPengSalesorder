package my.com.engpeng.engpengsalesorder.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import my.com.engpeng.engpengsalesorder.R;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_FACTOR;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_PRICE_BY_WEIGHT;

public class EnterQtyWgtActivity extends AppCompatActivity {

    private EditText etValue;
    private SeekBar sbQty, sbQtyMulti;
    private Button btnSave, btnCancel;
    private TextView tvKg;

    public static final String QUANTITY = "QUANTITY";
    public static final String WEIGHT = "WEIGHT";

    //receive from intent
    private int priceByWeight;
    private double factor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quantity_picker);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = 550;
        this.getWindow().setAttributes(params);
        this.setFinishOnTouchOutside(false);

        etValue = findViewById(R.id.enter_qty_wgt_et_value);
        sbQty = findViewById(R.id.enter_qty_wgt_sb_qty);
        sbQtyMulti = findViewById(R.id.enter_qty_wgt_sb_qty_multi);
        btnSave = findViewById(R.id.enter_qty_wgt_btn_save);
        btnCancel = findViewById(R.id.enter_qty_wgt_btn_cancel);
        tvKg = findViewById(R.id.enter_qty_wgt_tv_kg);

        etValue.setSelection(0, etValue.getText().length());

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
        if (intentStart.hasExtra(I_KEY_FACTOR)) {
            factor = intentStart.getDoubleExtra(I_KEY_FACTOR, 0);
        }
    }

    private void setupListener(){
        sbQty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                etValue.setText(String.valueOf(i));
                etValue.setSelection(0, etValue.getText().length());
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
                etValue.setText(String.valueOf(i*10));
                etValue.setSelection(0, etValue.getText().length());
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
                double qty, wgt;
                if(priceByWeight == 1){
                    wgt = Double.parseDouble(etValue.getText().toString());
                    qty = wgt / factor;
                }else{
                    qty = Double.parseDouble(etValue.getText().toString());
                    wgt = qty * factor;
                }
                data.putExtra(QUANTITY, qty);
                data.putExtra(WEIGHT, wgt);
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
