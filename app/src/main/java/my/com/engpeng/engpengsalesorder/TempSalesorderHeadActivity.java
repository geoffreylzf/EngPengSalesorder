package my.com.engpeng.engpengsalesorder;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.customerCompany.CustomerCompanyEntry;

public class TempSalesorderHeadActivity extends AppCompatActivity {

    private Spinner snCompany;
    private Button btnSearchCustomer;
    private EditText etCustomerCompany;

    private static final int RC_SELECT_CUSTOMER = 9001;
    private static final int RC_SELECT_ADDRESS = 9002;

    private AppDatabase mDb;

    private Long customerCompanyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_salesorder_head);

        snCompany = findViewById(R.id.temp_salesorder_head_sn_company);
        btnSearchCustomer = findViewById(R.id.temp_salesorder_head_btn_select_customer);
        etCustomerCompany = findViewById(R.id.temp_salesorder_head_et_customer);

        mDb = AppDatabase.getInstance(getApplicationContext());

        setupSpinnerWithCoverQty();
        setupListener();
    }

    private void setupListener() {
        btnSearchCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(TempSalesorderHeadActivity.this, CustomerSelectionActivity.class)
                        , RC_SELECT_CUSTOMER);
            }
        });
    }

    private void setupSpinnerWithCoverQty() {
        List<String> labels = new ArrayList<>();
        labels.add("Eng Peng Cold Storage ");
        labels.add("Salam Marketing");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, labels);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        snCompany.setAdapter(dataAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SELECT_CUSTOMER) {
            if (resultCode == RESULT_OK & data != null) {
                customerCompanyId = data.getLongExtra(CustomerSelectionActivity.CUSTOMER_COMPANY_ID, 0);
                retriveCustomerCompany();
            }
        }
    }

    private void retriveCustomerCompany(){
        final LiveData<CustomerCompanyEntry> cc = mDb.customerCompanyDao().loadLiveCustomerCompanyById(customerCompanyId);
        cc.observe(this, new Observer<CustomerCompanyEntry>() {
            @Override
            public void onChanged(@Nullable CustomerCompanyEntry customerCompanyEntries) {
                cc.removeObserver(this);
                etCustomerCompany.setText(customerCompanyEntries.getPersonCustomerCompanyName());
            }
        });
    }
}
