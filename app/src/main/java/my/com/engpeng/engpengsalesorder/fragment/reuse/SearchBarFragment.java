package my.com.engpeng.engpengsalesorder.fragment.reuse;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import my.com.engpeng.engpengsalesorder.R;

public class SearchBarFragment extends Fragment {

    private ImageButton ibIcon, ibClear;
    private EditText etFilter;
    private SearchBarFragmentListener sbfListener;

    public interface SearchBarFragmentListener {
        void onFilterChanged(String filter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            sbfListener = (SearchBarFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SearchBarFragmentListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_search_bar, container, false);

        ibIcon = rootView.findViewById(R.id.search_ib_icon);
        ibClear = rootView.findViewById(R.id.search_ib_clear);
        etFilter = rootView.findViewById(R.id.search_et_filter);

        ibIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etFilter.requestFocus();
            }
        });
        ibClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etFilter.setText(null);
            }
        });

        etFilter.addTextChangedListener(new TextWatcher() {
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
                sbfListener.onFilterChanged(etFilter.getText().toString());
            }
        });

        etFilter.requestFocus();

        return rootView;
    }
}
