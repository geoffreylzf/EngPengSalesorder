package my.com.engpeng.engpengsalesorder;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import my.com.engpeng.engpengsalesorder.database.AppDatabase;
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

public class HouseKeepingActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_keeping2);

        tvCustCount = findViewById(R.id.house_keeping_tv_cust_count);
        tvCustProgress = findViewById(R.id.house_keeping_tv_cust_progress);
        tvCustLastSync = findViewById(R.id.house_keeping_tv_cust_last_sync);
        pbCustProgress = findViewById(R.id.house_keeping_pb_cust_progress);
        ibCustRefresh = findViewById(R.id.house_keeping_btn_cust_refresh);

        tvAddrCount = findViewById(R.id.house_keeping_tv_addr_count);
        tvAddrProgress = findViewById(R.id.house_keeping_tv_addr_progress);
        tvAddrLastSync = findViewById(R.id.house_keeping_tv_addr_last_sync);
        pbAddrProgress = findViewById(R.id.house_keeping_pb_addr_progress);
        ibAddrRefresh = findViewById(R.id.house_keeping_btn_addr_refresh);

        tvIPCount = findViewById(R.id.house_keeping_tv_ip_count);
        tvIPProgress = findViewById(R.id.house_keeping_tv_ip_progress);
        tvIPLastSync = findViewById(R.id.house_keeping_tv_ip_last_sync);
        pbIPProgress = findViewById(R.id.house_keeping_pb_ip_progress);
        ibIPRefresh = findViewById(R.id.house_keeping_btn_ip_refresh);

        tvPSCount = findViewById(R.id.house_keeping_tv_ps_count);
        tvPSProgress = findViewById(R.id.house_keeping_tv_ps_progress);
        tvPSLastSync = findViewById(R.id.house_keeping_tv_ps_last_sync);
        pbPSProgress = findViewById(R.id.house_keeping_pb_ps_progress);
        ibPSRefresh = findViewById(R.id.house_keeping_btn_ps_refresh);

        cbLocal = findViewById(R.id.house_keeping_cb_local);
        btnReSyncAll = findViewById(R.id.house_keeping_btn_resync_all);
        btnUpdateAll = findViewById(R.id.house_keeping_btn_update_all);

        mDb = AppDatabase.getInstance(getApplicationContext());

        retrieveHouseKeeping();
        setupListener();
    }

    private void retrieveHouseKeeping() {
        final LiveData<List<TableInfoEntry>> tableInfoList = mDb.tableInfoDao().loadLiveAllTableInfo();
        tableInfoList.observe(this, new Observer<List<TableInfoEntry>>() {
            @Override
            public void onChanged(@Nullable List<TableInfoEntry> tableInfoEntries) {
                if (tableInfoEntries != null) {
                    for (TableInfoEntry tableInfo : tableInfoEntries) {
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

    private void setupListener() {
        btnReSyncAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HouseKeepingActivity.this, UpdateHouseKeepingService.class);
                intent.putExtra(I_KEY_TABLE, ACTION_GET_ALL_TABLE);
                intent.putExtra(I_KEY_ACTION, ACTION_REFRESH);
                intent.putExtra(I_KEY_LOCAL, cbLocal.isChecked());
                stopService(intent);
                startService(intent);
            }
        });
        btnUpdateAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HouseKeepingActivity.this, UpdateHouseKeepingService.class);
                intent.putExtra(I_KEY_TABLE, ACTION_GET_ALL_TABLE);
                intent.putExtra(I_KEY_ACTION, ACTION_UPDATE);
                intent.putExtra(I_KEY_LOCAL, cbLocal.isChecked());
                stopService(intent);
                startService(intent);
            }
        });
        ibPSRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HouseKeepingActivity.this, UpdateHouseKeepingService.class);
                intent.putExtra(I_KEY_TABLE, PriceSettingEntry.TABLE_NAME);
                intent.putExtra(I_KEY_ACTION, ACTION_REFRESH);
                intent.putExtra(I_KEY_LOCAL, cbLocal.isChecked());
                stopService(intent);
                startService(intent);
            }
        });
        ibIPRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HouseKeepingActivity.this, UpdateHouseKeepingService.class);
                intent.putExtra(I_KEY_TABLE, ItemPackingEntry.TABLE_NAME);
                intent.putExtra(I_KEY_ACTION, ACTION_REFRESH);
                intent.putExtra(I_KEY_LOCAL, cbLocal.isChecked());
                stopService(intent);
                startService(intent);
            }
        });
        ibCustRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HouseKeepingActivity.this, UpdateHouseKeepingService.class);
                intent.putExtra(I_KEY_TABLE, CustomerCompanyEntry.TABLE_NAME);
                intent.putExtra(I_KEY_ACTION, ACTION_REFRESH);
                intent.putExtra(I_KEY_LOCAL, cbLocal.isChecked());
                stopService(intent);
                startService(intent);
            }
        });
        ibAddrRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HouseKeepingActivity.this, UpdateHouseKeepingService.class);
                intent.putExtra(I_KEY_TABLE, CustomerCompanyAddressEntry.TABLE_NAME);
                intent.putExtra(I_KEY_ACTION, ACTION_REFRESH);
                intent.putExtra(I_KEY_LOCAL, cbLocal.isChecked());
                stopService(intent);
                startService(intent);
            }
        });
    }

}
