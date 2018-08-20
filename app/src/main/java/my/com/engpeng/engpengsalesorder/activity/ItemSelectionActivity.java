package my.com.engpeng.engpengsalesorder.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.adapter.CustomerSelectionAdapter;
import my.com.engpeng.engpengsalesorder.adapter.ItemSelectionAdapter;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingDisplay;
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingEntry;
import my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail.TempSalesorderDetailEntry;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;
import my.com.engpeng.engpengsalesorder.fragment.SearchBarFragment;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_CUSTOMER_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_DELIVERY_DATE;

public class ItemSelectionActivity extends AppCompatActivity implements SearchBarFragment.SearchBarFragmentListener {

    private Toolbar tb;
    private DrawerLayout dl;
    private NavigationView nvStart;

    private RecyclerView rv;
    private ItemSelectionAdapter adapter;

    private AppDatabase mDb;
    public static final String ITEM_PACKING_ID = "ITEM_PACKING_ID";
    public static final String QUANTITY = "QUANTITY";
    public static final int RC_QUANTITY_PICKER = 9101;

    //receive from intent
    private Long customerCompanyId;
    private Long priceGroupId;
    private String deliveryDate;

    private Long itemPackingId, priceSettingId;
    private double sellingPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_selection);

        setTitle("Item Packing Selection");

        this.getWindow().setStatusBarColor(this.getColor(R.color.colorTransparent));

        tb = findViewById(R.id.item_selection_tb);
        dl = findViewById(R.id.item_selection_dl);
        nvStart = findViewById(R.id.item_selection_start_nv);
        rv = findViewById(R.id.item_selection_rv_list);

        mDb = AppDatabase.getInstance(getApplicationContext());

        setupLayout();
        setupIntent();
        setupRecycleView();
        retrieveItemPacking("");
    }

    private void setupLayout() {
        setSupportActionBar(tb);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dl, tb
                ,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dl.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupIntent() {
        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_CUSTOMER_COMPANY_ID)) {
            customerCompanyId = intentStart.getLongExtra(I_KEY_CUSTOMER_COMPANY_ID, 0);
        }
        if (intentStart.hasExtra(I_KEY_DELIVERY_DATE)) {
            deliveryDate = intentStart.getStringExtra(I_KEY_DELIVERY_DATE);
        }
    }

    private void setupRecycleView() {
        //rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new ItemSelectionAdapter(this, new ItemSelectionAdapter.ItemSelectionAdapterListener() {
            @Override
            public void onItemSelected(Long id) {
                /*Intent data = new Intent();
                data.putExtra(ITEM_PACKING_ID, id);
                setResult(RESULT_OK, data);
                finish();*/
                itemPackingId = id;
                Intent intent = new Intent(ItemSelectionActivity.this, QuantityPickerActivity.class);
                startActivityForResult(intent, RC_QUANTITY_PICKER);
            }
        });
        rv.setAdapter(adapter);
    }


    private void retrieveItemPacking(final String filter) {
        final LiveData<List<ItemPackingDisplay>> cc = mDb.itemPackingDao().loadLiveAllItemPackingsByFilter("%" + filter + "%", customerCompanyId, deliveryDate);
        cc.observe(this, new Observer<List<ItemPackingDisplay>>() {
            @Override
            public void onChanged(@Nullable List<ItemPackingDisplay> itemPackingDisplays) {
                cc.removeObserver(this);
                adapter.setItemEntryList(itemPackingDisplays);
            }
        });
    }

    @Override
    public void onFilterChanged(String filter) {
        retrieveItemPacking(filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_QUANTITY_PICKER) {
            if (resultCode == RESULT_OK & data != null) {
                double quantity = data.getDoubleExtra(QuantityPickerActivity.QUANTITY, 0);
                //return to summary
                final TempSalesorderDetailEntry detail = new TempSalesorderDetailEntry(
                        itemPackingId,
                        quantity,
                        0,
                        0,
                        0,
                        0,
                        "IDK",
                        0);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.tempSalesorderDetailDao().insertTempSalesorderDetail(detail);
                        finish();
                    }
                });
            }
        }
    }
}
