package my.com.engpeng.engpengsalesorder.fragment.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.activity.SalesorderActivity;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;
import my.com.engpeng.engpengsalesorder.database.log.LogEntry;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;
import my.com.engpeng.engpengsalesorder.fragment.dialog.ConfirmDialogFragment;
import my.com.engpeng.engpengsalesorder.fragment.MainFragment;
import my.com.engpeng.engpengsalesorder.gps.GpsConnection;
import my.com.engpeng.engpengsalesorder.model.SalesInfo;
import my.com.engpeng.engpengsalesorder.service.UpdateHouseKeepingService;
import my.com.engpeng.engpengsalesorder.service.UploadService;
import my.com.engpeng.engpengsalesorder.utilities.SharedPreferencesUtils;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;

import static my.com.engpeng.engpengsalesorder.Global.ACTION_GET_ALL_TABLE;
import static my.com.engpeng.engpengsalesorder.Global.ACTION_UPDATE;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_ACTION;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_LOCAL;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_TABLE;
import static my.com.engpeng.engpengsalesorder.Global.LOG_TASK_UPDATE_HK;
import static my.com.engpeng.engpengsalesorder.Global.LOG_TASK_UPLOAD;
import static my.com.engpeng.engpengsalesorder.Global.P_KEY_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.sCompanyCode;
import static my.com.engpeng.engpengsalesorder.Global.sCompanyId;
import static my.com.engpeng.engpengsalesorder.Global.sCompanyName;
import static my.com.engpeng.engpengsalesorder.Global.sCompanyShortName;

public class MainDashboardFragment extends Fragment {

    public static final String tag = "MAIN_DASHBOARD_FRAGMENT";
    private Button btnSync;
    private TextView tvLogHkDatetime, tvLogHkRemark;
    private TextView tvLogUDatetime, tvLogURemark;
    private CheckBox cbLocal;

    private Button btnSo;
    private TextView tvTmDraft, tvTmDraftAmt, tvTmConfirm, tvTmConfirmAmt, tvTmUpload;

    private TextView tvCompany, tvVersion;

    private AppDatabase mDb;
    private FragmentActivity activity;

    private LiveData<SalesInfo> ldSalesInfo;
    private LiveData<List<LogEntry>> ldLog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_m_dashboard, container, false);

        btnSo = rootView.findViewById(R.id.m_dashboard_btn_so);
        btnSync = rootView.findViewById(R.id.m_dashboard_btn_sync);
        tvCompany = rootView.findViewById(R.id.m_dashboard_tv_company);
        tvVersion = rootView.findViewById(R.id.m_dashboard_tv_version);

        tvLogHkDatetime = rootView.findViewById(R.id.m_dashboard_tv_log_hk_dt);
        tvLogHkRemark = rootView.findViewById(R.id.m_dashboard_tv_log_hk_r);
        tvLogUDatetime = rootView.findViewById(R.id.m_dashboard_tv_log_u_dt);
        tvLogURemark = rootView.findViewById(R.id.m_dashboard_tv_log_u_r);
        cbLocal = rootView.findViewById(R.id.m_dashboard_cb_local);

        tvTmDraft = rootView.findViewById(R.id.m_dashboard_tv_so_tm_draft_count);
        tvTmDraftAmt = rootView.findViewById(R.id.m_dashboard_tv_so_tm_draft_amount);
        tvTmConfirm = rootView.findViewById(R.id.m_dashboard_tv_so_tm_confirm_count);
        tvTmConfirmAmt = rootView.findViewById(R.id.m_dashboard_tv_so_tm_confirm_amount);
        tvTmUpload = rootView.findViewById(R.id.m_dashboard_tv_so_tm_upload);

        activity = getActivity();
        mDb = AppDatabase.getInstance(activity.getApplicationContext());
        getActivity().setTitle(getString(R.string.app_name));

        setupVersion();
        setupListener();
        setupSharedPreferences();
        retrieveLastLog();
        retrieveSoInfo();

        return rootView;
    }

    private void setupVersion() {
        tvVersion.setText(getString(R.string.version).concat(StringUtils.getAppVersion(getContext())));
    }

    public void setupListener() {

        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentUpdate = new Intent(getActivity(), UpdateHouseKeepingService.class);
                intentUpdate.putExtra(I_KEY_TABLE, ACTION_GET_ALL_TABLE);
                intentUpdate.putExtra(I_KEY_ACTION, ACTION_UPDATE);
                intentUpdate.putExtra(I_KEY_LOCAL, cbLocal.isChecked());
                getActivity().stopService(intentUpdate);
                getActivity().startService(intentUpdate);

                Intent intentDownload = new Intent(getActivity(), UploadService.class);
                intentDownload.putExtra(I_KEY_LOCAL, cbLocal.isChecked());
                getActivity().stopService(intentDownload);
                getActivity().startService(intentDownload);
            }
        });

        btnSo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sCompanyId == 0) {
                    UiUtils.showConfirmDialog(getFragmentManager(),
                            getString(R.string.dialog_title_select_company),
                            getString(R.string.dialog_msg_select_company),
                            getString(R.string.dialog_btn_positive_select_company),
                            new ConfirmDialogFragment.ConfirmDialogFragmentListener() {
                                @Override
                                public void onPositiveButtonClicked() {
                                    ((MainFragment) getParentFragment()).navigateTo(new CompanyFragment(), CompanyFragment.tag, true, null, null);
                                }
                            });

                } else if(!GpsConnection.checkPermission(getContext())){
                    GpsConnection.requestPermission(getActivity());
                } else{
                    startActivity(new Intent(activity, SalesorderActivity.class));
                }
            }
        });
    }

    private void setupSharedPreferences() {
        retrieveCompany();
        SharedPreferences sharedPreferences = SharedPreferencesUtils.getSharedPreferences(activity);
        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(P_KEY_COMPANY_ID)) {
                    retrieveCompany();
                }
            }
        });
    }

    private void retrieveCompany() {
        sCompanyId = SharedPreferencesUtils.getCompanyId(activity);
        if (sCompanyId != 0) {
            final LiveData<BranchEntry> ld = mDb.branchDao().loadLiveBranchById(sCompanyId);
            ld.observe(this, new Observer<BranchEntry>() {
                @Override
                public void onChanged(@Nullable BranchEntry branchEntry) {
                    ld.removeObserver(this);
                    if (branchEntry != null) {
                        sCompanyId = branchEntry.getId();
                        sCompanyCode = branchEntry.getBranchCode();
                        sCompanyName = branchEntry.getBranchName();
                        sCompanyShortName = branchEntry.getBranchShortName();
                        populateUi();
                    } else {
                        clearGlobalCompany();
                    }
                }
            });
        } else {
            clearGlobalCompany();
        }
    }

    private void clearGlobalCompany() {
        sCompanyId = 0;
        sCompanyCode = "Non";
        sCompanyName = "No Company Selected";
        sCompanyShortName = "No Company Selected";
        populateUi();
    }

    private void populateUi() {
        tvCompany.setText(sCompanyName);
    }

    private void retrieveLastLog() {
        ldLog = mDb.logDao().loadLiveLastLogGroupByTask();
        ldLog.removeObservers(getActivity());
        ldLog.observe(getActivity(), new Observer<List<LogEntry>>() {
            @Override
            public void onChanged(@Nullable List<LogEntry> logEntryList) {
                if (logEntryList != null) {
                    for (LogEntry log: logEntryList) {
                        if(log.getTask().equals(LOG_TASK_UPDATE_HK)){
                            tvLogHkDatetime.setText(log.getDatetime());
                            tvLogHkRemark.setText(log.getRemark());
                        }else if(log.getTask().equals(LOG_TASK_UPLOAD)){
                            tvLogUDatetime.setText(log.getDatetime());
                            tvLogURemark.setText(log.getRemark());
                        }
                    }
                } else {
                    tvLogUDatetime.setText("");
                    tvLogURemark.setText("");
                    tvLogHkDatetime.setText("");
                    tvLogHkRemark.setText("");
                }
            }
        });
    }

    private void retrieveSoInfo() {
        String month = StringUtils.getCurrentYearMonth();
        long companyId = sCompanyId;
        ldSalesInfo = mDb.salesorderDao().loadLiveInfoCountByMonth(month, companyId);
        ldSalesInfo.removeObservers(getActivity());
        ldSalesInfo.observe(getActivity(), new Observer<SalesInfo>() {
            @Override
            public void onChanged(@Nullable SalesInfo salesInfo) {
                if(salesInfo != null){
                    tvTmDraft.setText(String.valueOf(salesInfo.getDraft()));
                    tvTmDraftAmt.setText(StringUtils.getDisplayPrice(salesInfo.getDraftAmt()));
                    tvTmConfirm.setText(String.valueOf(salesInfo.getConfirm()));
                    tvTmConfirmAmt.setText(StringUtils.getDisplayPrice(salesInfo.getConfirmAmt()));
                    tvTmUpload.setText(String.valueOf(salesInfo.getUnupload()));
                }else{
                    tvTmDraft.setText("0");
                    tvTmDraftAmt.setText(StringUtils.getDisplayPrice(0));
                    tvTmConfirm.setText("0");
                    tvTmConfirmAmt.setText(StringUtils.getDisplayPrice(0));
                    tvTmUpload.setText("0");
                }
            }
        });
    }
}
