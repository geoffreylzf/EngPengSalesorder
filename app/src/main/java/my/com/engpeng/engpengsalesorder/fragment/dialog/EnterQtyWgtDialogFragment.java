package my.com.engpeng.engpengsalesorder.fragment.dialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import my.com.engpeng.engpengsalesorder.R;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_FACTOR;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_PRICE_BY_WEIGHT;

public class EnterQtyWgtDialogFragment extends DialogFragment {

    public static final String tag = "ENTER_QTY_WGT_DIALOG_FRAGMENT";

    private EditText etQty, etWgt;
    private Button btnSave, btnCancel;

    //receive from bundle
    private int priceByWeight;
    private double factor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_enter_qty_wgt, container, false);

        etQty = rootView.findViewById(R.id.enter_qty_wgt_et_qty);
        etWgt = rootView.findViewById(R.id.enter_qty_wgt_et_wgt);
        btnSave = rootView.findViewById(R.id.enter_qty_wgt_btn_save);
        btnCancel = rootView.findViewById(R.id.enter_qty_wgt_btn_cancel);

        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setTitle("Enter Quantity & Weight");

        setupBundle();
        setupListener();

        return rootView;
    }

    public interface EnterQtyWgtFragmentListener {
        void afterEnterQtyWgt(double qty, double wgt);
    }

    private void setupBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            priceByWeight = bundle.getInt(I_KEY_PRICE_BY_WEIGHT, 0);
            factor = bundle.getDouble(I_KEY_FACTOR, 0);

            etQty.setText("1");
            etQty.requestFocus();
            etQty.setSelection(1);
            etWgt.setText(String.format(Locale.ENGLISH, "%.3f", factor));
        }
    }

    private void setupListener() {
        etQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String strQty = etQty.getText().toString();
                if (strQty.length() > 0) {
                    double wgt = Double.parseDouble(strQty) * factor;
                    etWgt.setText(String.format(Locale.ENGLISH, "%.3f", wgt));
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double qty, wgt;
                qty = Double.parseDouble(etQty.getText().toString());
                wgt = Double.parseDouble(etWgt.getText().toString());
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
