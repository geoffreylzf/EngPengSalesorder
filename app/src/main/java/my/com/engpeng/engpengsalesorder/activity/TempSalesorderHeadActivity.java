package my.com.engpeng.engpengsalesorder.activity;

import android.app.DatePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.customerCompany.CustomerCompanyEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompanyAddress.CustomerCompanyAddressEntry;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;
import my.com.engpeng.engpengsalesorder.utilities.UIUtils;

import static my.com.engpeng.engpengsalesorder.Global.DATE_DISPLAY_FORMAT;
import static my.com.engpeng.engpengsalesorder.Global.DATE_SAVE_FORMAT;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_CUSTOMER_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_DELIVERY_DATE;

public class TempSalesorderHeadActivity extends AppCompatActivity {

    private Spinner snCompany;
    private EditText etCustomer, etAddress, etDocumentDate, etDeliveryDate;
    private Button btnStart;

    private Calendar calendar;
    private SimpleDateFormat sdfSave, sdfDisplay;

    private static final int RC_SELECT_CUSTOMER = 9001;
    private static final int RC_SELECT_ADDRESS = 9002;

    private AppDatabase mDb;
    private Map<String, Long> map;

    private Long customerCompanyId, customerAddressId;
    private String documentDate, deliveryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_salesorder_head);

        snCompany = findViewById(R.id.temp_salesorder_head_sn_company);
        etCustomer = findViewById(R.id.temp_salesorder_head_et_customer);
        etAddress = findViewById(R.id.temp_salesorder_head_et_address);
        etDocumentDate = findViewById(R.id.temp_salesorder_head_et_document_date);
        etDeliveryDate = findViewById(R.id.temp_salesorder_head_et_delivery_date);
        btnStart = findViewById(R.id.temp_salesorder_head_btn_start);

        mDb = AppDatabase.getInstance(getApplicationContext());
        calendar = Calendar.getInstance();
        sdfDisplay = new SimpleDateFormat(DATE_DISPLAY_FORMAT, Locale.US);
        sdfSave = new SimpleDateFormat(DATE_SAVE_FORMAT, Locale.US);

        etDocumentDate.setText(sdfDisplay.format(calendar.getTime()));
        documentDate = sdfSave.format(calendar.getTime());

        setupSpinner();
        setupListener();

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                //mDb.tempSalesorderDetailDao().deleteAll();
            }
        });
    }



    private void setupListener() {

        etCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TempSalesorderHeadActivity.this, CustomerSelectionActivity.class);
                intent.putExtra(I_KEY_COMPANY_ID, getCompanyIdFromSpinner());
                startActivityForResult(intent, RC_SELECT_CUSTOMER);
            }
        });

        etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customerCompanyId == null || customerCompanyId == 0) {
                    UIUtils.getMessageDialog(TempSalesorderHeadActivity.this, "Error", "Please select customer first.");
                } else {
                    Intent intent = new Intent(TempSalesorderHeadActivity.this, AddressSelectionActivity.class);
                    intent.putExtra(I_KEY_CUSTOMER_COMPANY_ID, customerCompanyId);
                    startActivityForResult(intent, RC_SELECT_ADDRESS);
                }
            }
        });

        etDocumentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(TempSalesorderHeadActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                etDocumentDate.setText(sdfDisplay.format(calendar.getTime()));
                                documentDate = sdfSave.format(calendar.getTime());
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

        etDeliveryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(TempSalesorderHeadActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                etDeliveryDate.setText(sdfDisplay.format(calendar.getTime()));
                                deliveryDate = sdfSave.format(calendar.getTime());
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customerCompanyId == null || customerCompanyId == 0) {
                    UIUtils.getMessageDialog(TempSalesorderHeadActivity.this, "Error", "Please select customer.");
                    return;
                }
                if (deliveryDate == null || deliveryDate.equals("")) {
                    UIUtils.getMessageDialog(TempSalesorderHeadActivity.this, "Error", "Please select delivery date.");
                    return;
                }

                Intent intent = new Intent(TempSalesorderHeadActivity.this, TempSalesorderSummaryActivity.class);
                intent.putExtra(I_KEY_CUSTOMER_COMPANY_ID, customerCompanyId);
                intent.putExtra(I_KEY_DELIVERY_DATE, deliveryDate);
                startActivity(intent);
            }
        });
    }

    private void setupSpinner() {
        map = new HashMap<>();
        map.put("Salam Marketing", 17L);
        map.put("Eng Peng Cold Storage", 3L);

        List<String> labels = new ArrayList<>(map.keySet());

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, labels);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        snCompany.setAdapter(dataAdapter);
    }

    private Long getCompanyIdFromSpinner() {
        return map.get(snCompany.getSelectedItem().toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SELECT_CUSTOMER) {
            if (resultCode == RESULT_OK & data != null) {
                customerCompanyId = data.getLongExtra(CustomerSelectionActivity.CUSTOMER_COMPANY_ID, 0);
                retrieveCustomer();
            }
        } else if (requestCode == RC_SELECT_ADDRESS) {
            if (resultCode == RESULT_OK & data != null) {
                customerAddressId = data.getLongExtra(AddressSelectionActivity.CUSTOMER_ADDRESS_ID, 0);
                retrieveAddress();
            }
        }
    }

    private void retrieveCustomer() {
        final LiveData<CustomerCompanyEntry> cc = mDb.customerCompanyDao().loadLiveCustomerCompanyById(customerCompanyId);
        cc.observe(this, new Observer<CustomerCompanyEntry>() {
            @Override
            public void onChanged(@Nullable CustomerCompanyEntry customerCompanyEntry) {
                cc.removeObserver(this);
                etCustomer.setText(customerCompanyEntry.getPersonCustomerCompanyName());
                etCustomer.setTextColor(getColor(android.R.color.primary_text_light));

                customerAddressId = null;
                etAddress.setText(getString(R.string.no_address_selected));
                etAddress.setTextColor(getColor(R.color.colorHint));
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
                etAddress.setTextColor(getColor(android.R.color.primary_text_light));
            }
        });
    }
}
