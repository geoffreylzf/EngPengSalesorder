package my.com.engpeng.engpengsalesorder.fragment.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingEntry;
import my.com.engpeng.engpengsalesorder.database.priceSetting.PriceSettingEntry;
import my.com.engpeng.engpengsalesorder.database.tableList.TableInfoEntry;
import my.com.engpeng.engpengsalesorder.service.UpdateHouseKeepingService;

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

    private ImageButton ibIPRefresh;
    private TextView tvIPCount, tvIPProgress, tvIPLastSync;
    private ProgressBar pbIPProgress;

    private ImageButton ibPSRefresh;
    private TextView tvPSCount, tvPSProgress, tvPSLastSync;
    private ProgressBar pbPSProgress;

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
                    }
                }
            }
        });

        final LiveData<Integer> branchCount = mDb.branchDao().getLiveCount();
        branchCount.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                String msg = BranchEntry.TABLE_DISPLAY_NAME + " (" + integer + ")";
                tvBranchCount.setText(msg);
            }
        });

        final LiveData<Integer> priceSettingCount = mDb.priceSettingDao().getLiveCount();
        priceSettingCount.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                String msg = PriceSettingEntry.TABLE_DISPLAY_NAME + " (" + integer + ")";
                tvPSCount.setText(msg);
            }
        });

        final LiveData<Integer> itemPackingCount = mDb.itemPackingDao().getLiveCount();
        itemPackingCount.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                String msg = ItemPackingEntry.TABLE_DISPLAY_NAME + " (" + integer + ")";
                tvIPCount.setText(msg);
            }
        });

        final LiveData<Integer> ccCount = mDb.customerCompanyDao().getLiveCount();
        ccCount.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                String msg = CustomerCompanyEntry.TABLE_DISPLAY_NAME + " (" + integer + ")";
                tvCustCount.setText(msg);
            }
        });

        final LiveData<Integer> ccaCount = mDb.customerCompanyAddressDao().getLiveCount();
        ccaCount.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                String msg = CustomerCompanyAddressEntry.TABLE_DISPLAY_NAME + " (" + integer + ")";
                tvAddrCount.setText(msg);
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
                retrieveHouseKeepingUpdate(ACTION_GET_ALL_TABLE, ACTION_REFRESH);
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
    }
}