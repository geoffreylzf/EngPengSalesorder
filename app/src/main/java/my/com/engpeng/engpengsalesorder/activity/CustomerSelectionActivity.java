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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.adapter.CustomerSelectionAdapter;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.customerCompany.CustomerCompanyEntry;
import my.com.engpeng.engpengsalesorder.fragment.SearchBarFragment;

public class CustomerSelectionActivity extends AppCompatActivity implements SearchBarFragment.SearchBarFragmentListener
{

    private Toolbar tb;
    private DrawerLayout dl;
    private NavigationView nvStart;

    private RecyclerView rv;
    private CustomerSelectionAdapter adapter;

    private AppDatabase mDb;

    public static final String CUSTOMER_COMPANY_ID = "CUSTOMER_COMPANY_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_selection);

        setTitle("Customer Selection");

        this.getWindow().setStatusBarColor(this.getColor(R.color.colorTransparent));

        tb = findViewById(R.id.customer_selection_tb);
        dl = findViewById(R.id.customer_selection_dl);
        nvStart = findViewById(R.id.customer_selection_start_nv);
        rv = findViewById(R.id.customer_selection_rv_list);

        mDb = AppDatabase.getInstance(getApplicationContext());

        setupLayout();
        setupRecycleView();
        retrieveCustomerCompany("");
    }

    private void setupLayout() {
        setSupportActionBar(tb);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dl, tb,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dl.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupRecycleView() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomerSelectionAdapter(this, new CustomerSelectionAdapter.CustomerSelectionAdapterListener() {
            @Override
            public void onCustomerSelected(Long id) {
                Intent data = new Intent();
                data.putExtra(CUSTOMER_COMPANY_ID, id);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        rv.setAdapter(adapter);
    }

    private void retrieveCustomerCompany(final String filter) {
        final LiveData<List<CustomerCompanyEntry>> cc = mDb.customerCompanyDao().loadLiveAllCustomerCompaniesByFilter("%" + filter + "%");
        cc.observe(this, new Observer<List<CustomerCompanyEntry>>() {
            @Override
            public void onChanged(@Nullable List<CustomerCompanyEntry> customerCompanyEntries) {
                cc.removeObserver(this);
                adapter.setCustomerCompanyEntryList(customerCompanyEntries);
            }
        });
    }

    @Override
    public void onFilterChanged(String filter) {

        retrieveCustomerCompany(filter);
    }
}
