package my.com.engpeng.engpengsalesorder.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.salesorder.SalesorderEntry;
import my.com.engpeng.engpengsalesorder.database.salesorderDetail.SalesorderDetailEntry;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;
import my.com.engpeng.engpengsalesorder.service.UploadService;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_LOCAL;
import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_CONFIRM;

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

    private void setupListener() {
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int count = mDb.salesorderDao().getCountByStatusUpload(SO_STATUS_CONFIRM, 0);
                        if (count != 0) {
                            Intent intent = new Intent(getActivity(), UploadService.class);
                            intent.putExtra(I_KEY_LOCAL, false);
                            getActivity().stopService(intent);
                            getActivity().startService(intent);
                        } else {
                            UiUtils.showAlertDialog(getFragmentManager(), getString(R.string.error), getString(R.string.dialog_error_msg_no_data_upload));
                        }
                    }
                });
            }
        });
    }
}
