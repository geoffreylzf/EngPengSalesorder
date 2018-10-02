package my.com.engpeng.engpengsalesorder.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import my.com.engpeng.engpengsalesorder.R;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_FACTOR;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_PRICE_BY_WEIGHT;

public class EnterQtyWgtDialogFragment extends DialogFragment {

    public static final String tag = "ENTER_QTY_WGT_DIALOG_FRAGMENT";

    private EditText etValue;
    private SeekBar sbQty, sbQtyMulti;
    private Button btnSave, btnCancel;
    private TextView tvKg;

    //receive from bundle
    private int priceByWeight;
    private double factor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_enter_qty_wgt, container, false);

        etValue = rootView.findViewById(R.id.enter_qty_wgt_et_value);
        sbQty = rootView.findViewById(R.id.enter_qty_wgt_sb_qty);
        sbQtyMulti = rootView.findViewById(R.id.enter_qty_wgt_sb_qty_multi);
        btnSave = rootView.findViewById(R.id.enter_qty_wgt_btn_save);
        btnCancel = rootView.findViewById(R.id.enter_qty_wgt_btn_cancel);
        tvKg = rootView.findViewById(R.id.enter_qty_wgt_tv_kg);
        etValue.setSelection(0, etValue.getText().length());

        getDialog().setCanceledOnTouchOutside(false);

        setupBundle();
        setupListener();

        return rootView;
    }

    public interface EnterQtyWgtFragmentListener{
        void afterEnterQtyWgt(double qty, double wgt);
    }

    private void setupBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            priceByWeight = bundle.getInt(I_KEY_PRICE_BY_WEIGHT, 0);
            if(priceByWeight == 1){
                getDialog().setTitle("Enter Weight");
            }else{
                getDialog().setTitle("Enter Quantity");
                tvKg.setVisibility(View.GONE);
            }
            factor = bundle.getDouble(I_KEY_FACTOR, 0);
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
                double qty, wgt;
                if(priceByWeight == 1){
                    wgt = Double.parseDouble(etValue.getText().toString());
                    qty = wgt / factor;
                }else{
                    qty = Double.parseDouble(etValue.getText().toString());
                    wgt = qty * factor;
                }
                EnterQtyWgtFragmentListener enterQtyWgtFragmentListener = (EnterQtyWgtFragmentListener) getActivity();
                enterQtyWgtFragmentListener.afterEnterQtyWgt(qty, wgt);
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
