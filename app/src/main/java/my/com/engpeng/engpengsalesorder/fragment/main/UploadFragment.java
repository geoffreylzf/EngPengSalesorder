package my.com.engpeng.engpengsalesorder.fragment.main;

import android.animation.LayoutTransition;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.adapter.LogAdapter;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.log.LogEntry;
import my.com.engpeng.engpengsalesorder.service.UploadService;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_LOCAL;
import static my.com.engpeng.engpengsalesorder.Global.LOG_TASK_UPLOAD;
import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_CONFIRM;

public class UploadFragment extends Fragment {

    public static final String tag = "MAIN_UPLOAD_FRAGMENT";

    private TextView tvCount;
    private Button btnUpload;
    private ProgressBar pb;
    private CheckBox cbLocal;

    private RecyclerView rv;
    private LogAdapter adapter;

    private AppDatabase mDb;

    private int count;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_m_upload, container, false);

        ((ViewGroup) rootView.findViewById(R.id.m_upload_cl)).getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        tvCount = rootView.findViewById(R.id.m_upload_tv_count);
        btnUpload = rootView.findViewById(R.id.m_upload_btn_upload);
        pb = rootView.findViewById(R.id.m_upload_pb);
        rv = rootView.findViewById(R.id.m_upload_rv);
        cbLocal = rootView.findViewById(R.id.m_upload_cb_local);

        getActivity().setTitle("Upload Salesorder");
        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());

        setupRecycleView();
        setupListener();
        retrieveCount();

        return rootView;
    }

    private void setupRecycleView() {
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LogAdapter(getContext());
        rv.setAdapter(adapter);
        LiveData<List<LogEntry>> ld = mDb.logDao().loadLiveLogByTask(LOG_TASK_UPLOAD);
        ld.observe(this, new Observer<List<LogEntry>>() {
            @Override
            public void onChanged(@Nullable List<LogEntry> logEntries) {
                adapter.setList(logEntries);
            }
        });
    }

    private void retrieveCount() {
        LiveData<Integer> ldCount = mDb.salesorderDao().getLiveCountByStatusUpload(SO_STATUS_CONFIRM, 0);
        ldCount.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null) {
                    tvCount.setText(integer.toString());
                    count = integer;
                    if(count == 0){
                        pb.setVisibility(View.GONE);
                    }
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
                    intent.putExtra(I_KEY_LOCAL, cbLocal.isChecked());
                    getActivity().stopService(intent);
                    getActivity().startService(intent);
                    pb.setVisibility(View.VISIBLE);
                } else {
                    UiUtils.showAlertDialog(getFragmentManager(), getString(R.string.error), getString(R.string.dialog_error_msg_no_data_upload));
                }
            }
        });
    }
}
