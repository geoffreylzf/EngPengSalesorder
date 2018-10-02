package my.com.engpeng.engpengsalesorder.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import my.com.engpeng.engpengsalesorder.R;

public class EnterPriceDialogFragment extends DialogFragment {

    public static final String tag = "ENTER_PRICE_DIALOG_FRAGMENT";
    private EditText etValue;
    private Button btnSave, btnCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_enter_price, container, false);

        getDialog().setTitle("Enter Price");
        getDialog().setCanceledOnTouchOutside(false);

        etValue = rootView.findViewById(R.id.enter_price_et_value);
        btnSave = rootView.findViewById(R.id.enter_price_btn_save);
        btnCancel = rootView.findViewById(R.id.enter_price_btn_cancel);

        setupListener();

        return rootView;
    }

    public interface EnterPriceFragmentListener{
        void afterEnterPrice(double price);
    }

    private void setupListener(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etValue.getText().toString().equals("")){
                    double price = Double.parseDouble(etValue.getText().toString());

                    EnterPriceFragmentListener enterPriceFragmentListener = (EnterPriceFragmentListener) getActivity();
                    enterPriceFragmentListener.afterEnterPrice(price);
                    dismiss();
                }
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
