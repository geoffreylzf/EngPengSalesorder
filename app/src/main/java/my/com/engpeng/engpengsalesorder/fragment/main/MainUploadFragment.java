package my.com.engpeng.engpengsalesorder.fragment.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;

public class MainUploadFragment extends Fragment {

    public static final String tag = "MAIN_UPLOAD_FRAGMENT";

    private Button btnUpload;

    private AppDatabase mDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_m_upload, container, false);

        btnUpload = rootView.findViewById(R.id.m_upload_btn_upload);

        getActivity().setTitle("Upload Salesorder");
        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());

        setupListener();

        return rootView;
    }

    private void setupListener(){
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
