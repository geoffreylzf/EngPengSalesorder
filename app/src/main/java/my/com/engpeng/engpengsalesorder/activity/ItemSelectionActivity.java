package my.com.engpeng.engpengsalesorder.activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import android.content.Intent;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.adapter.ItemSelectionAdapter;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingDisplay;
import my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail.TempSalesorderDetailEntry;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;
import my.com.engpeng.engpengsalesorder.fragment.dialog.EnterPriceDialogFragment;
import my.com.engpeng.engpengsalesorder.fragment.dialog.EnterQtyWgtDialogFragment;
import my.com.engpeng.engpengsalesorder.fragment.reuse.SearchBarFragment;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_CUSTOMER_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_DELIVERY_DATE;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_FACTOR;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_PRICE_BY_WEIGHT;

public class ItemSelectionActivity extends AppCompatActivity
        implements SearchBarFragment.SearchBarFragmentListener,
        EnterQtyWgtDialogFragment.EnterQtyWgtFragmentListener,
        EnterPriceDialogFragment.EnterPriceFragmentListener{

    private Toolbar tb;
    private DrawerLayout dl;
    private NavigationView nvStart;

    private RecyclerView rv;
    private ItemSelectionAdapter adapter;

    private AppDatabase mDb;

    //receive from intent
    private Long customerCompanyId;
    private Long priceGroupId;
    private String deliveryDate;

    private Long itemPackingId, priceSettingId;
    private String priceMethod;
    private double factor, price;
    private int priceByWeight;

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
            public void onItemSelected(long id, double p_factor, int p_priceByWeight, String p_priceMethod, long p_priceSettingId, double p_price) {
                itemPackingId = id;
                factor = p_factor;
                priceMethod = p_priceMethod;
                priceSettingId = p_priceSettingId;
                priceByWeight = p_priceByWeight;
                price = p_price;

                if (price != 0) {
                    openEnterQtyWgtDialog();
                } else {
                    openEnterPriceDialog();
                }
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
    public void afterEnterQtyWgt(double quantity, double weight) {
        double totalPrice = 0;
        if (priceByWeight == 1) {
            totalPrice = price * weight;
        } else {
            totalPrice = price * quantity;
        }

        final TempSalesorderDetailEntry detail = new TempSalesorderDetailEntry(
                itemPackingId,
                quantity,
                weight,
                factor,
                price,
                priceSettingId,
                priceMethod,
                totalPrice);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.tempSalesorderDetailDao().insertTempSalesorderDetail(detail);
                finish();
            }
        });
    }

    @Override
    public void afterEnterPrice(double price) {
        this.price = price;
        openEnterQtyWgtDialog();
    }

    private void openEnterPriceDialog() {
        FragmentManager fm = getSupportFragmentManager();
        EnterPriceDialogFragment fragment = new EnterPriceDialogFragment();
        fragment.show(fm, EnterPriceDialogFragment.tag);
    }

    private void openEnterQtyWgtDialog() {
        FragmentManager fm = getSupportFragmentManager();
        EnterQtyWgtDialogFragment fragment = new EnterQtyWgtDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(I_KEY_PRICE_BY_WEIGHT, priceByWeight);
        bundle.putDouble(I_KEY_FACTOR, factor);
        fragment.setArguments(bundle);
        fragment.show(fm, EnterQtyWgtDialogFragment.tag);
    }
}
