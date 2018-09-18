package my.com.engpeng.engpengsalesorder.fragment;

import android.app.DatePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import my.com.engpeng.engpengsalesorder.activity.AddressSelectionActivity;
import my.com.engpeng.engpengsalesorder.activity.CustomerSelectionActivity;
import my.com.engpeng.engpengsalesorder.activity.NavigationHost;
import my.com.engpeng.engpengsalesorder.animation.FabOpenAnimation;
import my.com.engpeng.engpengsalesorder.animation.RevealAnimationSetting;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompany.CustomerCompanyEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompanyAddress.CustomerCompanyAddressEntry;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;
import my.com.engpeng.engpengsalesorder.utilities.UIUtils;

import static android.app.Activity.RESULT_OK;
import static my.com.engpeng.engpengsalesorder.Global.DATE_DISPLAY_FORMAT;
import static my.com.engpeng.engpengsalesorder.Global.DATE_SAVE_FORMAT;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_CUSTOMER_ADDRESS_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_CUSTOMER_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_DELIVERY_DATE;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_DOCUMENT_DATE;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_LPO;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_REMARK;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_REVEAL_ANIMATION_SETTINGS;
import static my.com.engpeng.engpengsalesorder.Global.hideKeyboard;

public class TempSoHeadFragment extends Fragment {

    private Spinner snCompany;
    private EditText etCustomer, etAddress, etDocumentDate, etDeliveryDate, etLpo, etRemark;
    private Button btnStart;
    private View rootView;

    private Calendar calendar;
    private SimpleDateFormat sdfSave, sdfDisplay;

    private static final int RC_SELECT_CUSTOMER = 9001;
    private static final int RC_SELECT_ADDRESS = 9002;

    private AppDatabase mDb;
    private Map<String, Long> map;

    private Long customerCompanyId, customerAddressId;
    private String documentDate, deliveryDate;

    //Receive from bundle
    private RevealAnimationSetting revealAnimationSetting;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_temp_so_head, container, false);

        snCompany = rootView.findViewById(R.id.temp_so_head_sn_company);
        etCustomer = rootView.findViewById(R.id.temp_so_head_et_customer);
        etAddress = rootView.findViewById(R.id.temp_so_head_et_address);
        etDocumentDate = rootView.findViewById(R.id.temp_so_head_et_document_date);
        etDeliveryDate = rootView.findViewById(R.id.temp_so_head_et_delivery_date);
        etLpo = rootView.findViewById(R.id.temp_so_head_et_lpo);
        etRemark = rootView.findViewById(R.id.temp_so_head_et_remark);
        btnStart = rootView.findViewById(R.id.temp_so_head_btn_start);

        getActivity().setTitle("New Salesorder");

        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());
        calendar = Calendar.getInstance();
        sdfDisplay = new SimpleDateFormat(DATE_DISPLAY_FORMAT, Locale.US);
        sdfSave = new SimpleDateFormat(DATE_SAVE_FORMAT, Locale.US);

        etDocumentDate.setText(sdfDisplay.format(calendar.getTime()));
        documentDate = sdfSave.format(calendar.getTime());

        setupBundle();
        setupAnimation();
        setupSpinner();
        setupListener();

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                //mDb.tempSalesorderDetailDao().deleteAll();
            }
        });

        return rootView;
    }

    private void setupBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            revealAnimationSetting = bundle.getParcelable(I_KEY_REVEAL_ANIMATION_SETTINGS);
        }
    }

    private void setupAnimation() {
        if (revealAnimationSetting != null) {
            FabOpenAnimation.registerCircularRevealAnimation(
                    getContext(),
                    rootView,
                    revealAnimationSetting,
                    ContextCompat.getColor(getContext(), R.color.colorPrimary),
                    ContextCompat.getColor(getContext(), R.color.colorBackground));
        }
    }

    private void setupListener() {
        etCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CustomerSelectionActivity.class);
                intent.putExtra(I_KEY_COMPANY_ID, getCompanyIdFromSpinner());
                startActivityForResult(intent, RC_SELECT_CUSTOMER);
            }
        });

        etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customerCompanyId == null || customerCompanyId == 0) {
                    UIUtils.showAlertDialog(getFragmentManager(), "Error", "Please select customer first.");
                } else {
                    Intent intent = new Intent(getActivity(), AddressSelectionActivity.class);
                    intent.putExtra(I_KEY_CUSTOMER_COMPANY_ID, customerCompanyId);
                    startActivityForResult(intent, RC_SELECT_ADDRESS);
                }
            }
        });

        etDocumentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
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
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
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
                    UIUtils.showAlertDialog(getFragmentManager(), "Error", "Please select customer.");
                    return;
                }
                if (customerAddressId == null || customerAddressId == 0) {
                    UIUtils.showAlertDialog(getFragmentManager(), "Error", "Please select address.");
                    return;
                }
                if (deliveryDate == null || deliveryDate.equals("")) {
                    UIUtils.showAlertDialog(getFragmentManager(), "Error", "Please select delivery date.");
                    return;
                }

                /*Intent intent = new Intent(TempSalesorderHeadActivity.this, TempSalesorderSummaryActivity.class);
                intent.putExtra(I_KEY_COMPANY_ID, getCompanyIdFromSpinner());
                intent.putExtra(I_KEY_CUSTOMER_COMPANY_ID, customerCompanyId);
                intent.putExtra(I_KEY_CUSTOMER_ADDRESS_ID, customerAddressId);
                intent.putExtra(I_KEY_DOCUMENT_DATE, documentDate);
                intent.putExtra(I_KEY_DELIVERY_DATE, deliveryDate);
                intent.putExtra(I_KEY_LPO, etLpo.getText().toString());
                intent.putExtra(I_KEY_REMARK, etRemark.getText().toString());
                startActivity(intent);*/

                hideKeyboard(getActivity());

                TempSoCartFragment tempSoCartFragment = new TempSoCartFragment();
                Bundle bundle = new Bundle();
                bundle.putLong(I_KEY_COMPANY_ID, getCompanyIdFromSpinner());
                bundle.putLong(I_KEY_CUSTOMER_COMPANY_ID, customerCompanyId);
                bundle.putLong(I_KEY_CUSTOMER_ADDRESS_ID, customerAddressId);
                bundle.putString(I_KEY_DOCUMENT_DATE, documentDate);
                bundle.putString(I_KEY_DELIVERY_DATE, deliveryDate);
                bundle.putString(I_KEY_LPO, etLpo.getText().toString());
                bundle.putString(I_KEY_REMARK, etRemark.getText().toString());
                tempSoCartFragment.setArguments(bundle);

                ((NavigationHost) getActivity()).navigateTo(tempSoCartFragment, true);
            }
        });
    }

    private void setupSpinner() {

        final LiveData<List<BranchEntry>> cc = mDb.branchDao().loadLiveAllCompany();
        cc.observe(this, new Observer<List<BranchEntry>>() {
            @Override
            public void onChanged(@Nullable List<BranchEntry> branchEntryList) {
                cc.removeObserver(this);
                map = new HashMap<>();
                for (BranchEntry branchEntry : branchEntryList) {
                    map.put(branchEntry.getBranchName(), branchEntry.getId());
                }
                List<String> labels = new ArrayList<>(map.keySet());

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                        R.layout.spinner_item, labels);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                snCompany.setAdapter(dataAdapter);
            }
        });

    }

    private Long getCompanyIdFromSpinner() {
        if (snCompany.getSelectedItem() == null) {
            return 0L;
        }
        return map.get(snCompany.getSelectedItem().toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                etCustomer.setTextColor(getActivity().getColor(android.R.color.primary_text_light));

                customerAddressId = null;
                etAddress.setText("");
                //etAddress.setText(getString(R.string.no_address_selected));
                //etAddress.setTextColor(getActivity().getColor(R.color.colorHint));
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
                //etAddress.setTextColor(getActivity().getColor(android.R.color.primary_text_light));
            }
        });
    }
}
