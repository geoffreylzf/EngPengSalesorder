package my.com.engpeng.engpengsalesorder.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import my.com.engpeng.engpengsalesorder.Global;
import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.adapter.TempSalesorderConfirmAdapter;
import my.com.engpeng.engpengsalesorder.adapter.TempSalesorderSummaryAdapter;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.customerCompany.CustomerCompanyEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompanyAddress.CustomerCompanyAddressEntry;
import my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail.TempSalesorderDetailDisplay;

import static my.com.engpeng.engpengsalesorder.Global.DATE_DISPLAY_FORMAT;
import static my.com.engpeng.engpengsalesorder.Global.DATE_SAVE_FORMAT;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_CUSTOMER_ADDRESS_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_CUSTOMER_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_DELIVERY_DATE;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_DOCUMENT_DATE;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_LPO;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_REMARK;

public class TempSalesorderConfirmActivity extends AppCompatActivity {

    private EditText etCompany, etCustomer, etAddress, etDocumentDate, etDeliveryDate, etLpo, etRemark;
    private TextView tvTotalPrice;
    private RecyclerView rv;

    private AppDatabase mDb;
    private SimpleDateFormat sdfSave, sdfDisplay;
    private TempSalesorderConfirmAdapter adapter;

    //receive from intent
    private Long companyId;
    private Long customerCompanyId;
    private Long customerAddressId;
    private String documentDate;
    private String deliveryDate;
    private String lpo;
    private String remark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_salesorder_confirm);

        etCompany = findViewById(R.id.temp_so_confirm_et_company);
        etCustomer = findViewById(R.id.temp_so_confirm_et_customer_company);
        etAddress = findViewById(R.id.temp_so_confirm_et_customer_address);
        etDocumentDate = findViewById(R.id.temp_so_confirm_et_document_date);
        etDeliveryDate = findViewById(R.id.temp_so_confirm_et_delivery_date);
        etLpo = findViewById(R.id.temp_so_confirm_et_lpo);
        etRemark = findViewById(R.id.temp_so_confirm_et_remark);
        rv = findViewById(R.id.temp_so_confirm_rv);
        tvTotalPrice = findViewById(R.id.temp_so_confirm_tv_total_price);

        mDb = AppDatabase.getInstance(getApplicationContext());
        sdfDisplay = new SimpleDateFormat(DATE_DISPLAY_FORMAT, Locale.US);
        sdfSave = new SimpleDateFormat(DATE_SAVE_FORMAT, Locale.US);

        setTitle("Salesorder Confirm");

        setupIntent();
        setupHeadInfo();
        setupRecycleView();
        retrieveTotalPrice();
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

    private void setupHeadInfo() {
        etCompany.setText(companyId.toString());
        etLpo.setText(lpo.equals("") ? " ": lpo);
        etRemark.setText(remark.equals("") ? " ": remark);

        retrieveCustomer();
        retrieveAddress();

        try {
            etDocumentDate.setText(sdfDisplay.format(sdfSave.parse(documentDate).getTime()));
            etDeliveryDate.setText(sdfDisplay.format(sdfSave.parse(deliveryDate).getTime()));
        } catch (ParseException e) {
            etDocumentDate.setText(documentDate);
            etDeliveryDate.setText(deliveryDate);
        }
    }

    private void retrieveCustomer() {
        final LiveData<CustomerCompanyEntry> cc = mDb.customerCompanyDao().loadLiveCustomerCompanyById(customerCompanyId);
        cc.observe(this, new Observer<CustomerCompanyEntry>() {
            @Override
            public void onChanged(@Nullable CustomerCompanyEntry customerCompanyEntry) {
                cc.removeObserver(this);
                etCustomer.setText(customerCompanyEntry.getPersonCustomerCompanyName());
            }
        });
    }

    private void retrieveAddress() {
        final LiveData<CustomerCompanyAddressEntry> cca = mDb.customerCompanyAddressDao().loadLiveCustomerCompanyAddressById(customerAddressId);
        cca.observe(this, new Observer<CustomerCompanyAddressEntry>() {
            @Override
            public void onChanged(@Nullable CustomerCompanyAddressEntry customerCompanyAddressEntry) {
                cca.removeObserver(this);
                etAddress.setText(customerCompanyAddressEntry.getPersonCustomerAddressName());
            }
        });
    }

    private void setupRecycleView() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TempSalesorderConfirmAdapter(this);
        rv.setAdapter(adapter);

        final LiveData<List<TempSalesorderDetailDisplay>> cc = mDb.tempSalesorderDetailDao().loadAllTempSalesorderDetailsWithItemPacking();
        cc.observe(this, new Observer<List<TempSalesorderDetailDisplay>>() {
            @Override
            public void onChanged(@Nullable List<TempSalesorderDetailDisplay> details) {
                adapter.setList(details);
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
}
