package my.com.engpeng.engpengsalesorder.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.adapter.AddressSelectionAdapter;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.customerCompanyAddress.CustomerCompanyAddressEntry;
import my.com.engpeng.engpengsalesorder.fragment.SearchBarFragment;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_CUSTOMER_COMPANY_ID;

public class AddressSelectionActivity extends AppCompatActivity implements SearchBarFragment.SearchBarFragmentListener{

    private Toolbar tb;

    private RecyclerView rv;
    private AddressSelectionAdapter adapter;

    private AppDatabase mDb;

    private Long customerCompanyId;

    public static final String CUSTOMER_ADDRESS_ID = "CUSTOMER_ADDRESS_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_selection);

        setTitle("Address Selection");

        tb = findViewById(R.id.address_selection_tb);
        rv = findViewById(R.id.address_selection_rv_list);

        mDb = AppDatabase.getInstance(getApplicationContext());

        setupIntent();
        setupLayout();
        setupRecycleView();
        retrieveCustomerAddress( "");
    }

    private void setupIntent(){
        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_CUSTOMER_COMPANY_ID)) {
            customerCompanyId = intentStart.getLongExtra(I_KEY_CUSTOMER_COMPANY_ID, 0);
        }
    }

    private void setupLayout() {
        setSupportActionBar(tb);
    }

    private void setupRecycleView() {
        rv.setLayoutManager(new LinearLayoutManager(this));
         adapter = new AddressSelectionAdapter(this, new AddressSelectionAdapter.AddressSelectionAdapterListener() {
            @Override
            public void onAddressSelected(Long id) {
                Intent data = new Intent();
                data.putExtra(CUSTOMER_ADDRESS_ID, id);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        rv.setAdapter(adapter);
    }

    private void retrieveCustomerAddress(String filter) {
        final LiveData<List<CustomerCompanyAddressEntry>> cca = mDb.customerCompanyAddressDao().loadLiveCustomerCompanyAddressesByPersonCustomerCompanyIdFilter(customerCompanyId, "%" + filter + "%");
        cca.observe(this, new Observer<List<CustomerCompanyAddressEntry>>() {
            @Override
            public void onChanged(@Nullable List<CustomerCompanyAddressEntry> customerCompanyEntries) {
                cca.removeObserver(this);
                adapter.setCustomerCompanyEntryList(customerCompanyEntries);
            }
        });
    }

    @Override
    public void onFilterChanged(String filter) {
        retrieveCustomerAddress(filter);
    }
}
