package my.com.engpeng.engpengsalesorder.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.tubb.smrv.SwipeMenuRecyclerView;

import java.util.List;

import my.com.engpeng.engpengsalesorder.Global;
import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.adapter.TempSalesorderSummaryAdapter;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail.TempSalesorderDetailDisplay;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_CUSTOMER_ADDRESS_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_CUSTOMER_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_DELIVERY_DATE;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_DOCUMENT_DATE;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_LPO;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_REMARK;

public class TempSalesorderSummaryActivity extends AppCompatActivity {

    private Toolbar tb;
    private DrawerLayout dl;
    private NavigationView nvStart;
    private FloatingActionButton fabAdd;
    private TextView tvTotalPrice;

    private TempSalesorderSummaryAdapter adapter;
    private SwipeMenuRecyclerView rv;

    private AppDatabase mDb;

    //receive from intent
    private Long companyId;
    private Long customerCompanyId;
    private Long customerAddressId;
    private String documentDate;
    private String deliveryDate;
    private String lpo;
    private String remark;

    //for delete item
    private boolean isDeleting = false;
    private int deletePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_salesorder_summary);

        setTitle("In Cart List");

        this.getWindow().setStatusBarColor(this.getColor(R.color.colorTransparent));

        tb = findViewById(R.id.temp_so_summary_tb);
        dl = findViewById(R.id.temp_so_summary_dl);
        nvStart = findViewById(R.id.temp_so_summary_start_nv);
        rv = findViewById(R.id.temp_so_summary_rv);
        fabAdd = findViewById(R.id.temp_so_summary_fab_add);
        tvTotalPrice = findViewById(R.id.temp_so_summary_tv_total_price);

        mDb = AppDatabase.getInstance(getApplicationContext());

        setupLayout();
        setupIntent();
        setupHeadDetail();
        setupRecycleView();
        retrieveDetail();
        retrieveTotalPrice();
        setupListener();
    }

    private void setupLayout() {
        setSupportActionBar(tb);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dl, tb,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dl.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupIntent() {
        Intent intentStart = getIntent();
        companyId = intentStart.getLongExtra(I_KEY_COMPANY_ID, 0);
        customerCompanyId = intentStart.getLongExtra(I_KEY_CUSTOMER_COMPANY_ID, 0);
        customerAddressId = intentStart.getLongExtra(I_KEY_CUSTOMER_ADDRESS_ID, 0);
        documentDate = intentStart.getStringExtra(I_KEY_DOCUMENT_DATE);
        deliveryDate = intentStart.getStringExtra(I_KEY_DELIVERY_DATE);
        lpo = intentStart.getStringExtra(I_KEY_LPO);
        remark = intentStart.getStringExtra(I_KEY_REMARK);
    }

    private void setupHeadDetail(){

    }

    private void setupRecycleView() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TempSalesorderSummaryAdapter(this, new TempSalesorderSummaryAdapter.TempSalesorderSummaryAdapterListener() {
            @Override
            public void afterItemDelete(final long item_packing_id, final int position) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        isDeleting = true;
                        deletePosition = position;
                        mDb.tempSalesorderDetailDao().deleteByItemPackingId(item_packing_id);
                    }
                });
            }
        });
        rv.setAdapter(adapter);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabAdd.hide();
                } else {
                    fabAdd.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void retrieveDetail() {
        final LiveData<List<TempSalesorderDetailDisplay>> cc = mDb.tempSalesorderDetailDao().loadAllTempSalesorderDetailsWithItemPacking();
        cc.observe(this, new Observer<List<TempSalesorderDetailDisplay>>() {
            @Override
            public void onChanged(@Nullable List<TempSalesorderDetailDisplay> details) {
                if (isDeleting) {
                    rv.reset();
                    adapter.setListAfterDelete(details, deletePosition);
                    isDeleting = false;
                } else {
                    rv.reset();
                    adapter.setList(details);
                }
            }
        });
    }

    private void retrieveTotalPrice() {
        final LiveData<Double> cc = mDb.tempSalesorderDetailDao().getLiveSumTotalPrice();
        cc.observe(this, new Observer<Double>() {
            @Override
            public void onChanged(@Nullable Double d) {
                if(d == null){
                    tvTotalPrice.setText(Global.getDisplayPrice(0));
                }else{
                    tvTotalPrice.setText(Global.getDisplayPrice(d));
                }
            }
        });
    }

    private void setupListener() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callItemSelection();
            }
        });
    }

    private void callItemSelection() {
        Intent intent = new Intent(TempSalesorderSummaryActivity.this, ItemSelectionActivity.class);
        intent.putExtra(I_KEY_CUSTOMER_COMPANY_ID, customerCompanyId);
        intent.putExtra(I_KEY_DELIVERY_DATE, deliveryDate);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.temp_so_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_checkout) {
            Intent intent = new Intent(this, TempSalesorderConfirmActivity.class);
            intent.putExtra(I_KEY_COMPANY_ID, companyId);
            intent.putExtra(I_KEY_CUSTOMER_COMPANY_ID, customerCompanyId);
            intent.putExtra(I_KEY_CUSTOMER_ADDRESS_ID, customerAddressId);
            intent.putExtra(I_KEY_DOCUMENT_DATE, documentDate);
            intent.putExtra(I_KEY_DELIVERY_DATE, deliveryDate);
            intent.putExtra(I_KEY_LPO, lpo);
            intent.putExtra(I_KEY_REMARK, remark);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
