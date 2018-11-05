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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompany.CustomerCompanyEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompanyAddress.CustomerCompanyAddressEntry;
import my.com.engpeng.engpengsalesorder.database.itemCompany.ItemCompanyEntry;
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingEntry;
import my.com.engpeng.engpengsalesorder.database.priceSetting.PriceSettingEntry;
import my.com.engpeng.engpengsalesorder.database.tableList.TableInfoEntry;
import my.com.engpeng.engpengsalesorder.database.taxCode.TaxCodeEntry;
import my.com.engpeng.engpengsalesorder.database.taxItemMatching.TaxItemMatchingEntry;
import my.com.engpeng.engpengsalesorder.database.taxType.TaxTypeEntry;
import my.com.engpeng.engpengsalesorder.fragment.dialog.ConfirmDialogFragment;
import my.com.engpeng.engpengsalesorder.service.UpdateHouseKeepingService;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;

import static my.com.engpeng.engpengsalesorder.Global.ACTION_GET_ALL_TABLE;
import static my.com.engpeng.engpengsalesorder.Global.ACTION_REFRESH;
import static my.com.engpeng.engpengsalesorder.Global.ACTION_UPDATE;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_ACTION;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_LOCAL;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_TABLE;

public class HouseKeepingFragment extends Fragment {
    public static final String tag = "MAIN_HOUSE_KEEPING_FRAGMENT";

    private ImageButton ibBranchRefresh;
    private TextView tvBranchCount, tvBranchProgress, tvBranchLastSync;
    private ProgressBar pbBranchProgress;

    private ImageButton ibCustRefresh;
    private TextView tvCustCount, tvCustProgress, tvCustLastSync;
    private ProgressBar pbCustProgress;

    private ImageButton ibAddrRefresh;
    private TextView tvAddrCount, tvAddrProgress, tvAddrLastSync;
    private ProgressBar pbAddrProgress;

    private ImageButton ibIcRefresh;
    private TextView tvIcCount, tvIcProgress, tvIcLastSync;
    private ProgressBar pbIcProgress;

    private ImageButton ibIPRefresh;
    private TextView tvIPCount, tvIPProgress, tvIPLastSync;
    private ProgressBar pbIPProgress;

    private ImageButton ibPSRefresh;
    private TextView tvPSCount, tvPSProgress, tvPSLastSync;
    private ProgressBar pbPSProgress;

    private ImageButton ibTcRefresh;
    private TextView tvTcCount, tvTcProgress, tvTcLastSync;
    private ProgressBar pbTcProgress;

    private ImageButton ibTimRefresh;
    private TextView tvTimCount, tvTimProgress, tvTimLastSync;
    private ProgressBar pbTimProgress;

    private ImageButton ibTtRefresh;
    private TextView tvTtCount, tvTtProgress, tvTtLastSync;
    private ProgressBar pbTtProgress;

    private CheckBox cbLocal;
    private Button btnReSyncAll, btnUpdateAll;

    private AppDatabase mDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_m_house_keeping, container, false);

        tvBranchCount = rootView.findViewById(R.id.house_keeping_tv_branch_count);
        tvBranchProgress = rootView.findViewById(R.id.house_keeping_tv_branch_progress);
        tvBranchLastSync = rootView.findViewById(R.id.house_keeping_tv_branch_last_sync);
        pbBranchProgress = rootView.findViewById(R.id.house_keeping_pb_branch_progress);
        ibBranchRefresh = rootView.findViewById(R.id.house_keeping_btn_branch_refresh);

        tvCustCount = rootView.findViewById(R.id.house_keeping_tv_cust_count);
        tvCustProgress = rootView.findViewById(R.id.house_keeping_tv_cust_progress);
        tvCustLastSync = rootView.findViewById(R.id.house_keeping_tv_cust_last_sync);
        pbCustProgress = rootView.findViewById(R.id.house_keeping_pb_cust_progress);
        ibCustRefresh = rootView.findViewById(R.id.house_keeping_btn_cust_refresh);

        tvAddrCount = rootView.findViewById(R.id.house_keeping_tv_addr_count);
        tvAddrProgress = rootView.findViewById(R.id.house_keeping_tv_addr_progress);
        tvAddrLastSync = rootView.findViewById(R.id.house_keeping_tv_addr_last_sync);
        pbAddrProgress = rootView.findViewById(R.id.house_keeping_pb_addr_progress);
        ibAddrRefresh = rootView.findViewById(R.id.house_keeping_btn_addr_refresh);

        tvIcCount = rootView.findViewById(R.id.house_keeping_tv_ic_count);
        tvIcProgress = rootView.findViewById(R.id.house_keeping_tv_ic_progress);
        tvIcLastSync = rootView.findViewById(R.id.house_keeping_tv_ic_last_sync);
        pbIcProgress = rootView.findViewById(R.id.house_keeping_pb_ic_progress);
        ibIcRefresh = rootView.findViewById(R.id.house_keeping_btn_ic_refresh);

        tvIPCount = rootView.findViewById(R.id.house_keeping_tv_ip_count);
        tvIPProgress = rootView.findViewById(R.id.house_keeping_tv_ip_progress);
        tvIPLastSync = rootView.findViewById(R.id.house_keeping_tv_ip_last_sync);
        pbIPProgress = rootView.findViewById(R.id.house_keeping_pb_ip_progress);
        ibIPRefresh = rootView.findViewById(R.id.house_keeping_btn_ip_refresh);

        tvPSCount = rootView.findViewById(R.id.house_keeping_tv_ps_count);
        tvPSProgress = rootView.findViewById(R.id.house_keeping_tv_ps_progress);
        tvPSLastSync = rootView.findViewById(R.id.house_keeping_tv_ps_last_sync);
        pbPSProgress = rootView.findViewById(R.id.house_keeping_pb_ps_progress);
        ibPSRefresh = rootView.findViewById(R.id.house_keeping_btn_ps_refresh);

        tvTcCount = rootView.findViewById(R.id.house_keeping_tv_tc_count);
        tvTcProgress = rootView.findViewById(R.id.house_keeping_tv_tc_progress);
        tvTcLastSync = rootView.findViewById(R.id.house_keeping_tv_tc_last_sync);
        pbTcProgress = rootView.findViewById(R.id.house_keeping_pb_tc_progress);
        ibTcRefresh = rootView.findViewById(R.id.house_keeping_btn_tc_refresh);

        tvTimCount = rootView.findViewById(R.id.house_keeping_tv_tim_count);
        tvTimProgress = rootView.findViewById(R.id.house_keeping_tv_tim_progress);
        tvTimLastSync = rootView.findViewById(R.id.house_keeping_tv_tim_last_sync);
        pbTimProgress = rootView.findViewById(R.id.house_keeping_pb_tim_progress);
        ibTimRefresh = rootView.findViewById(R.id.house_keeping_btn_tim_refresh);

        tvTtCount = rootView.findViewById(R.id.house_keeping_tv_tt_count);
        tvTtProgress = rootView.findViewById(R.id.house_keeping_tv_tt_progress);
        tvTtLastSync = rootView.findViewById(R.id.house_keeping_tv_tt_last_sync);
        pbTtProgress = rootView.findViewById(R.id.house_keeping_pb_tt_progress);
        ibTtRefresh = rootView.findViewById(R.id.house_keeping_btn_tt_refresh);

        cbLocal = rootView.findViewById(R.id.house_keeping_cb_local);
        btnReSyncAll = rootView.findViewById(R.id.house_keeping_btn_resync_all);
        btnUpdateAll = rootView.findViewById(R.id.house_keeping_btn_update_all);

        getActivity().setTitle("House Keeping Management");
        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());

        retrieveHouseKeeping();
        setupListener();

        return rootView;
    }

    private void retrieveHouseKeeping() {
        final LiveData<List<TableInfoEntry>> tableInfoList = mDb.tableInfoDao().loadLiveAllTableInfo();
        tableInfoList.observe(this, new Observer<List<TableInfoEntry>>() {
            @Override
            public void onChanged(@Nullable List<TableInfoEntry> tableInfoEntries) {
                if (tableInfoEntries != null) {
                    for (TableInfoEntry tableInfo : tableInfoEntries) {

                        if (tableInfo.getType().equals(BranchEntry.TABLE_NAME)) {
                            String msg_last_sync = "Last Synchronize : " + tableInfo.getLastSyncDate();
                            double row = tableInfo.getInsert();
                            double total = tableInfo.getTotal();
                            double percentage = row / total * 100;
                            String msg_progress = (int) row + "/" + (int) total + " (" + String.format(Locale.US, "%.2f", percentage) + "%)";
                            tvBranchLastSync.setText(msg_last_sync);
                            pbBranchProgress.setProgress((int) percentage);
                            tvBranchProgress.setText(msg_progress);
                        }

                        if (tableInfo.getType().equals(PriceSettingEntry.TABLE_NAME)) {
                            String msg_last_sync = "Last Synchronize : " + tableInfo.getLastSyncDate();
                            double row = tableInfo.getInsert();
                            double total = tableInfo.getTotal();
                            double percentage = row / total * 100;
                            String msg_progress = (int) row + "/" + (int) total + " (" + String.format(Locale.US, "%.2f", percentage) + "%)";
                            tvPSLastSync.setText(msg_last_sync);
                            pbPSProgress.setProgress((int) percentage);
                            tvPSProgress.setText(msg_progress);
                        }

                        if (tableInfo.getType().equals(ItemCompanyEntry.TABLE_NAME)) {
                            String msg_last_sync = "Last Synchronize : " + tableInfo.getLastSyncDate();
                            double row = tableInfo.getInsert();
                            double total = tableInfo.getTotal();
                            double percentage = row / total * 100;
                            String msg_progress = (int) row + "/" + (int) total + " (" + String.format(Locale.US, "%.2f", percentage) + "%)";
                            tvIcLastSync.setText(msg_last_sync);
                            pbIcProgress.setProgress((int) percentage);
                            tvIcProgress.setText(msg_progress);
                        }

                        if (tableInfo.getType().equals(ItemPackingEntry.TABLE_NAME)) {
                            String msg_last_sync = "Last Synchronize : " + tableInfo.getLastSyncDate();
                            double row = tableInfo.getInsert();
                            double total = tableInfo.getTotal();
                            double percentage = row / total * 100;
                            String msg_progress = (int) row + "/" + (int) total + " (" + String.format(Locale.US, "%.2f", percentage) + "%)";
                            tvIPLastSync.setText(msg_last_sync);
                            pbIPProgress.setProgress((int) percentage);
                            tvIPProgress.setText(msg_progress);
                        }

                        if (tableInfo.getType().equals(CustomerCompanyEntry.TABLE_NAME)) {
                            String msg_last_sync = "Last Synchronize : " + tableInfo.getLastSyncDate();
                            double row = tableInfo.getInsert();
                            double total = tableInfo.getTotal();
                            double percentage = row / total * 100;
                            String msg_progress = (int) row + "/" + (int) total + " (" + String.format(Locale.US, "%.2f", percentage) + "%)";
                            tvCustLastSync.setText(msg_last_sync);
                            pbCustProgress.setProgress((int) percentage);
                            tvCustProgress.setText(msg_progress);
                        }

                        if (tableInfo.getType().equals(CustomerCompanyAddressEntry.TABLE_NAME)) {
                            String msg_last_sync = "Last Synchronize : " + tableInfo.getLastSyncDate();
                            double row = tableInfo.getInsert();
                            double total = tableInfo.getTotal();
                            double percentage = row / total * 100;
                            String msg_progress = (int) row + "/" + (int) total + " (" + String.format(Locale.US, "%.2f", percentage) + "%)";
                            tvAddrLastSync.setText(msg_last_sync);
                            pbAddrProgress.setProgress((int) percentage);
                            tvAddrProgress.setText(msg_progress);
                        }

                        if (tableInfo.getType().equals(TaxCodeEntry.TABLE_NAME)) {
                            String msg_last_sync = "Last Synchronize : " + tableInfo.getLastSyncDate();
                            double row = tableInfo.getInsert();
                            double total = tableInfo.getTotal();
                            double percentage = row / total * 100;
                            String msg_progress = (int) row + "/" + (int) total + " (" + String.format(Locale.US, "%.2f", percentage) + "%)";
                            tvTcLastSync.setText(msg_last_sync);
                            pbTcProgress.setProgress((int) percentage);
                            tvTcProgress.setText(msg_progress);
                        }

                        if (tableInfo.getType().equals(TaxItemMatchingEntry.TABLE_NAME)) {
                            String msg_last_sync = "Last Synchronize : " + tableInfo.getLastSyncDate();
                            double row = tableInfo.getInsert();
                            double total = tableInfo.getTotal();
                            double percentage = row / total * 100;
                            String msg_progress = (int) row + "/" + (int) total + " (" + String.format(Locale.US, "%.2f", percentage) + "%)";
                            tvTimLastSync.setText(msg_last_sync);
                            pbTimProgress.setProgress((int) percentage);
                            tvTimProgress.setText(msg_progress);
                        }

                        if (tableInfo.getType().equals(TaxTypeEntry.TABLE_NAME)) {
                            String msg_last_sync = "Last Synchronize : " + tableInfo.getLastSyncDate();
                            double row = tableInfo.getInsert();
                            double total = tableInfo.getTotal();
                            double percentage = row / total * 100;
                            String msg_progress = (int) row + "/" + (int) total + " (" + String.format(Locale.US, "%.2f", percentage) + "%)";
                            tvTtLastSync.setText(msg_last_sync);
                            pbTtProgress.setProgress((int) percentage);
                            tvTtProgress.setText(msg_progress);
                        }
                    }
                }
            }
        });

        mDb.branchDao().getLiveCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                tvBranchCount.setText(BranchEntry.TABLE_DISPLAY_NAME + " (" + integer + ")");
            }
        });

        mDb.priceSettingDao().getLiveCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                tvPSCount.setText(PriceSettingEntry.TABLE_DISPLAY_NAME + " (" + integer + ")");
            }
        });

        mDb.itemPackingDao().getLiveCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                tvIPCount.setText(ItemPackingEntry.TABLE_DISPLAY_NAME + " (" + integer + ")");
            }
        });

        mDb.customerCompanyDao().getLiveCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                tvCustCount.setText(CustomerCompanyEntry.TABLE_DISPLAY_NAME + " (" + integer + ")");
            }
        });

        mDb.customerCompanyAddressDao().getLiveCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                tvAddrCount.setText(CustomerCompanyAddressEntry.TABLE_DISPLAY_NAME + " (" + integer + ")");
            }
        });

        mDb.taxCodeDao().getLiveCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                tvTcCount.setText(TaxCodeEntry.TABLE_DISPLAY_NAME + " (" + integer + ")");
            }
        });

        mDb.taxItemMatchingDao().getLiveCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                tvTimCount.setText(TaxItemMatchingEntry.TABLE_DISPLAY_NAME + " (" + integer + ")");
            }
        });

        mDb.taxTypeDao().getLiveCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                tvTtCount.setText(TaxTypeEntry.TABLE_DISPLAY_NAME + " (" + integer + ")");
            }
        });
    }

    private void retrieveHouseKeepingUpdate(String tables, String action) {
        Intent intent = new Intent(getActivity(), UpdateHouseKeepingService.class);
        intent.putExtra(I_KEY_TABLE, tables);
        intent.putExtra(I_KEY_ACTION, action);
        intent.putExtra(I_KEY_LOCAL, cbLocal.isChecked());
        getActivity().stopService(intent);
        getActivity().startService(intent);
    }

    private void setupListener() {
        btnReSyncAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiUtils.showConfirmDialog(getFragmentManager(),
                        getString(R.string.dialog_title_resync_hk),
                        getString(R.string.dialog_msg_resync_hk),
                        getString(R.string.dialog_btn_positive_resync_hk),
                        new ConfirmDialogFragment.ConfirmDialogFragmentListener() {
                            @Override
                            public void onPositiveButtonClicked() {
                                retrieveHouseKeepingUpdate(ACTION_GET_ALL_TABLE, ACTION_REFRESH);
                            }
                        });
            }
        });
        btnUpdateAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveHouseKeepingUpdate(ACTION_GET_ALL_TABLE, ACTION_UPDATE);
            }
        });

        ibBranchRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveHouseKeepingUpdate(BranchEntry.TABLE_NAME, ACTION_REFRESH);
            }
        });
        ibPSRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveHouseKeepingUpdate(PriceSettingEntry.TABLE_NAME, ACTION_REFRESH);
            }
        });
        ibIcRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveHouseKeepingUpdate(ItemCompanyEntry.TABLE_NAME, ACTION_REFRESH);
            }
        });
        ibIPRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveHouseKeepingUpdate(ItemPackingEntry.TABLE_NAME, ACTION_REFRESH);
            }
        });
        ibCustRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveHouseKeepingUpdate(CustomerCompanyEntry.TABLE_NAME, ACTION_REFRESH);
            }
        });
        ibAddrRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveHouseKeepingUpdate(CustomerCompanyAddressEntry.TABLE_NAME, ACTION_REFRESH);
            }
        });
        ibTcRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveHouseKeepingUpdate(TaxCodeEntry.TABLE_NAME, ACTION_REFRESH);
            }
        });
        ibTimRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveHouseKeepingUpdate(TaxItemMatchingEntry.TABLE_NAME, ACTION_REFRESH);
            }
        });
        ibTtRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveHouseKeepingUpdate(TaxTypeEntry.TABLE_NAME, ACTION_REFRESH);
            }
        });
    }
}