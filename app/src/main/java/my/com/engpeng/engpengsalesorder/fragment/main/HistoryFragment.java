package my.com.engpeng.engpengsalesorder.fragment.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.salesorder.SalesorderEntry;
import my.com.engpeng.engpengsalesorder.database.tableList.TableInfoEntry;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;
import my.com.engpeng.engpengsalesorder.fragment.dialog.ConfirmDialogFragment;
import my.com.engpeng.engpengsalesorder.service.DownloadHistoryService;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_LOCAL;
import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_CONFIRM;

public class HistoryFragment extends Fragment {

    public static final String tag = "MAIN_HISTORY_FRAGMENT";

    private Button btnHistory;
    private TextView tvSoCount, tvSoProgress, tvSoLastSync;
    private ProgressBar pbSoProgress;
    private CheckBox cbLocal;

    private AppDatabase mDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_m_history, container, false);

        tvSoCount = rootView.findViewById(R.id.history_tv_so_count);
        tvSoProgress = rootView.findViewById(R.id.history_tv_so_progress);
        tvSoLastSync = rootView.findViewById(R.id.history_tv_so_last_sync);
        pbSoProgress = rootView.findViewById(R.id.history_pb_so_progress);
        cbLocal = rootView.findViewById(R.id.m_history_cb_local);
        btnHistory = rootView.findViewById(R.id.m_history_btn_history);

        getActivity().setTitle("History Management");
        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());

        retrieveHistory();
        setupListener();

        return rootView;
    }

    private void setupListener() {
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int count = mDb.salesorderDao().getCountByStatusUpload(SO_STATUS_CONFIRM, 0);
                        if (count == 0) {
                            UiUtils.showConfirmDialog(getFragmentManager(),
                                    getString(R.string.dialog_title_get_history),
                                    getString(R.string.dialog_msg_get_history),
                                    getString(R.string.dialog_btn_positive_get_history),
                                    new ConfirmDialogFragment.ConfirmDialogFragmentListener() {
                                        @Override
                                        public void onPositiveButtonClicked() {
                                            Intent intent = new Intent(getActivity(), DownloadHistoryService.class);
                                            intent.putExtra(I_KEY_LOCAL, cbLocal.isChecked());
                                            getActivity().stopService(intent);
                                            getActivity().startService(intent);
                                        }
                                    });
                        } else {
                            UiUtils.showAlertDialog(getFragmentManager(), getString(R.string.error), getString(R.string.dialog_error_msg_unable_retrieve_history_due_to_un_upload_data));
                        }
                    }
                });
            }
        });
    }

    private void retrieveHistory() {
        final LiveData<TableInfoEntry> ld = mDb.tableInfoDao().loadLiveTableInfoByType(SalesorderEntry.TABLE_NAME);
        ld.observe(this, new Observer<TableInfoEntry>() {
            @Override
            public void onChanged(@Nullable TableInfoEntry tableInfo) {
                if (tableInfo != null) {
                    String msg_last_sync = "Last Synchronize : " + tableInfo.getLastSyncDate();
                    double row = tableInfo.getInsert();
                    double total = tableInfo.getTotal();
                    double percentage = row / total * 100;
                    String msg_progress = (int) row + "/" + (int) total + " (" + String.format(Locale.US, "%.2f", percentage) + "%)";
                    tvSoLastSync.setText(msg_last_sync);
                    pbSoProgress.setProgress((int) percentage);
                    tvSoProgress.setText(msg_progress);
                }
            }
        });
    }
}
