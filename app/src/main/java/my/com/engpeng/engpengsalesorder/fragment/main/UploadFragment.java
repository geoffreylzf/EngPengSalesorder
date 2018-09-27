package my.com.engpeng.engpengsalesorder.fragment.main;

import android.animation.LayoutTransition;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
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
import android.widget.TextView;

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

public class UploadFragment extends Fragment {

    public static final String tag = "MAIN_UPLOAD_FRAGMENT";

    private TextView tvCount;
    private Button btnUpload;

    private AppDatabase mDb;

    private int count;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_m_upload, container, false);

        ((ViewGroup) rootView.findViewById(R.id.m_upload_cl)).getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        tvCount = rootView.findViewById(R.id.m_upload_tv_count);
        btnUpload = rootView.findViewById(R.id.m_upload_btn_upload);

        getActivity().setTitle("Upload Salesorder");
        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());

        setupListener();
        retrieveCount();

        return rootView;
    }

    private void retrieveCount() {
        LiveData<Integer> ldCount = mDb.salesorderDao().getLiveCountByStatusUpload(SO_STATUS_CONFIRM, 0);
        ldCount.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null) {
                    tvCount.setText(integer.toString());
                    count = integer;
                } else {
                    count = 0;
                    tvCount.setText(getString(R.string.default_upload));
                }
            }
        });
    }

    private void setupListener() {
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
}
