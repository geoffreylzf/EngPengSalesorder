package my.com.engpeng.engpengsalesorder.fragment.sales;

import android.app.DatePickerDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
import my.com.engpeng.engpengsalesorder.database.salesorder.SalesorderEntry;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;

import static android.app.Activity.RESULT_OK;
import static my.com.engpeng.engpengsalesorder.Global.DATE_DISPLAY_FORMAT;
import static my.com.engpeng.engpengsalesorder.Global.DATE_SAVE_FORMAT;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_CUSTOMER_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_REVEAL_ANIMATION_SETTINGS;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_SALESORDER_ENTRY;
import static my.com.engpeng.engpengsalesorder.Global.sCompanyId;

public class TempSoHeadFragment extends Fragment implements FabOpenAnimation.Dismissible {

    public static final String tag = "TEMP_SO_HEAD_FRAGMENT";

    private EditText etCompany, etCustomer, etAddress, etDocumentDate, etDeliveryDate, etLpo, etRemark;
    private Button btnStart;
    private View rootView;
    private TextView tvNote;
    private ImageView ivFabIcon;

    private Calendar calendarDocumentDate, calendarDeliveryDate;
    private SimpleDateFormat sdfSave, sdfDisplay;

    private static final int RC_SELECT_CUSTOMER = 9001;
    private static final int RC_SELECT_ADDRESS = 9002;

    private AppDatabase mDb;

    private Long customerCompanyId, customerAddressId;
    private String documentDate, deliveryDate;

    //Receive from bundle
    private RevealAnimationSetting revealAnimationSetting;

    //Receive from bundle and use for next fragment
    private SalesorderEntry salesorderEntry;

    private boolean isStartingAnimationDone = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_temp_so_head, container, false);

        etCompany = rootView.findViewById(R.id.temp_so_head_et_company);
        etCustomer = rootView.findViewById(R.id.temp_so_head_et_customer);
        etAddress = rootView.findViewById(R.id.temp_so_head_et_address);
        etDocumentDate = rootView.findViewById(R.id.temp_so_head_et_document_date);
        etDeliveryDate = rootView.findViewById(R.id.temp_so_head_et_delivery_date);
        etLpo = rootView.findViewById(R.id.temp_so_head_et_lpo);
        etRemark = rootView.findViewById(R.id.temp_so_head_et_remark);
        btnStart = rootView.findViewById(R.id.temp_so_head_btn_start);
        tvNote = rootView.findViewById(R.id.temp_so_head_tv_note);
        ivFabIcon = rootView.findViewById(R.id.temp_so_head_iv_fab_icon);

        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());
        calendarDocumentDate = Calendar.getInstance();
        calendarDeliveryDate = Calendar.getInstance();
        sdfDisplay = new SimpleDateFormat(DATE_DISPLAY_FORMAT, Locale.US);
        sdfSave = new SimpleDateFormat(DATE_SAVE_FORMAT, Locale.US);

        etDocumentDate.setText(sdfDisplay.format(calendarDocumentDate.getTime()));
        documentDate = sdfSave.format(calendarDocumentDate.getTime());

        setupBundle();
        setupAnimation();
        setupListener();

        return rootView;
    }

    private void setupBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            revealAnimationSetting = Parcels.unwrap(bundle.getParcelable(I_KEY_REVEAL_ANIMATION_SETTINGS));
            salesorderEntry = Parcels.unwrap(bundle.getParcelable(I_KEY_SALESORDER_ENTRY));
            if (salesorderEntry == null) {
                getActivity().setTitle("New Salesorder");
                displayCompany(sCompanyId);
            } else {
                getActivity().setTitle("Draft Salesorder");
                populateUi();
            }
        }
    }

    private void setupAnimation() {
        if (revealAnimationSetting != null && !isStartingAnimationDone) {
            FabOpenAnimation.registerCircularRevealAnimation(
                    getContext(),
                    rootView,
                    revealAnimationSetting,
                    ContextCompat.getColor(getContext(), R.color.colorPrimaryDark),
                    ContextCompat.getColor(getContext(), R.color.colorBackground),
                    new FabOpenAnimation.AnimationFinishedListener() {
                        @Override
                        public void onAnimationFinished() {
                            ivFabIcon.setVisibility(View.GONE);
                        }
                    });
            isStartingAnimationDone = true;
        }
    }

    @Override
    public void dismiss(final OnDismissedListener listener) {
        if (revealAnimationSetting != null) {
            ivFabIcon.setVisibility(View.VISIBLE);
            FabOpenAnimation.startCircularExitAnimation(
                    getContext(), rootView, revealAnimationSetting,
                    ContextCompat.getColor(getContext(), R.color.colorBackground),
                    ContextCompat.getColor(getContext(), R.color.colorPrimaryDark),
                    new FabOpenAnimation.AnimationFinishedListener() {
                        @Override
                        public void onAnimationFinished() {
                            listener.onDismissed();
                        }
                    });
        } else {
            listener.onDismissed();
        }
    }

    private void populateUi() {
        tvNote.setVisibility(View.VISIBLE);

        displayCompany(salesorderEntry.getCompanyId());

        customerCompanyId = salesorderEntry.getCustomerCompanyId();
        retrieveCustomer(false);

        customerAddressId = salesorderEntry.getCustomerAddressId();
        retrieveAddress();

        documentDate = salesorderEntry.getDocumentDate();
        deliveryDate = salesorderEntry.getDeliveryDate();
        try {
            etDocumentDate.setText(sdfDisplay.format(sdfSave.parse(documentDate).getTime()));
            etDeliveryDate.setText(sdfDisplay.format(sdfSave.parse(deliveryDate).getTime()));

            calendarDocumentDate.setTime(sdfSave.parse(documentDate));
            calendarDeliveryDate.setTime(sdfSave.parse(deliveryDate));
        } catch (ParseException e) {
            etDocumentDate.setText(salesorderEntry.getDocumentDate());
            etDeliveryDate.setText(salesorderEntry.getDeliveryDate());
        }

        etLpo.setText(salesorderEntry.getLpo());
        etRemark.setText(salesorderEntry.getRemark());
    }

    private void displayCompany(final long companyId) {
        final LiveData<BranchEntry> ld = mDb.branchDao().loadLiveBranchById(companyId);
        ld.observe(this, new Observer<BranchEntry>() {
            @Override
            public void onChanged(@Nullable BranchEntry branchEntry) {
                ld.removeObserver(this);
                if (branchEntry != null) {
                    etCompany.setText(branchEntry.getBranchName());
                } else {
                    etCompany.setText(String.valueOf(companyId));
                }
            }
        });
    }

    private void setupListener() {

        etCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CustomerSelectionActivity.class);
                intent.putExtra(I_KEY_COMPANY_ID, sCompanyId);
                startActivityForResult(intent, RC_SELECT_CUSTOMER);
            }
        });

        etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customerCompanyId == null || customerCompanyId == 0) {
                    UiUtils.showAlertDialog(getFragmentManager(), "Error", "Please select customer first.");
                } else {
                    Intent intent = new Intent(getActivity(), AddressSelectionActivity.class);
                    intent.putExtra(I_KEY_CUSTOMER_COMPANY_ID, customerCompanyId);
                    startActivityForResult(intent, RC_SELECT_ADDRESS);
                }
            }
        });

        /*etDocumentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.showDatePickerDialog(getFragmentManager(), calendarDocumentDate, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        calendarDocumentDate.set(Calendar.YEAR, year);
                        calendarDocumentDate.set(Calendar.MONTH, monthOfYear);
                        calendarDocumentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        etDocumentDate.setText(sdfDisplay.format(calendarDocumentDate.getTime()));
                        documentDate = sdfSave.format(calendarDocumentDate.getTime());
                    }
                });
            }
        });*/

        etDeliveryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.showDatePickerDialog(getFragmentManager(), calendarDeliveryDate, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        calendarDeliveryDate.set(Calendar.YEAR, year);
                        calendarDeliveryDate.set(Calendar.MONTH, monthOfYear);
                        calendarDeliveryDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        etDeliveryDate.setText(sdfDisplay.format(calendarDeliveryDate.getTime()));
                        deliveryDate = sdfSave.format(calendarDeliveryDate.getTime());
                        clearTempSoDetailEntry();
                    }
                }, System.currentTimeMillis(), System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customerCompanyId == null || customerCompanyId == 0) {
                    UiUtils.showAlertDialog(getFragmentManager(), "Error", "Please select customer.");
                    return;
                }
                if (customerAddressId == null || customerAddressId == 0) {
                    UiUtils.showAlertDialog(getFragmentManager(), "Error", "Please select address.");
                    return;
                }
                if (deliveryDate == null || deliveryDate.equals("")) {
                    UiUtils.showAlertDialog(getFragmentManager(), "Error", "Please select delivery date.");
                    return;
                } else {
                    try {
                        long deliveryTimestamp = sdfSave.parse(deliveryDate).getTime();
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);

                        long minDateTimeStamp = cal.getTime().getTime();
                        cal.add(Calendar.DATE, 2);
                        long maxDateTimeStamp = cal.getTime().getTime();

                        if (deliveryTimestamp < minDateTimeStamp ){
                            UiUtils.showAlertDialog(getFragmentManager(), "Invalid Delivery Date (Less Than Minimum Date)", "Please select delivery date again.");
                            return;
                        }else if (deliveryTimestamp > maxDateTimeStamp){
                            UiUtils.showAlertDialog(getFragmentManager(), "Invalid Delivery Date (More Than Maximum Date)", "Please select delivery date again.");
                            return;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                UiUtils.hideKeyboard(getActivity());

                SalesorderEntry salesorderEntry;
                if (TempSoHeadFragment.this.salesorderEntry == null) {
                    salesorderEntry = new SalesorderEntry();
                } else {
                    salesorderEntry = TempSoHeadFragment.this.salesorderEntry;
                }
                salesorderEntry.setCompanyId(sCompanyId);
                salesorderEntry.setCustomerCompanyId(customerCompanyId);
                salesorderEntry.setCustomerAddressId(customerAddressId);
                salesorderEntry.setDocumentDate(documentDate);
                salesorderEntry.setDeliveryDate(deliveryDate);
                salesorderEntry.setLpo(etLpo.getText().toString());
                salesorderEntry.setRemark(etRemark.getText().toString());

                TempSoCartFragment tempSoCartFragment = new TempSoCartFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(I_KEY_SALESORDER_ENTRY, Parcels.wrap(salesorderEntry));
                tempSoCartFragment.setArguments(bundle);

                ((NavigationHost) getActivity()).navigateTo(tempSoCartFragment, TempSoCartFragment.tag, true, null, null);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SELECT_CUSTOMER) {
            if (resultCode == RESULT_OK & data != null) {
                customerCompanyId = data.getLongExtra(CustomerSelectionActivity.CUSTOMER_COMPANY_ID, 0);
                retrieveCustomer(true);
            }
        } else if (requestCode == RC_SELECT_ADDRESS) {
            if (resultCode == RESULT_OK & data != null) {
                customerAddressId = data.getLongExtra(AddressSelectionActivity.CUSTOMER_ADDRESS_ID, 0);
                retrieveAddress();
            }
        }
    }

    private void retrieveCustomer(final boolean clearRelatedData) {
        final LiveData<CustomerCompanyEntry> cc = mDb.customerCompanyDao().loadLiveCustomerCompanyById(customerCompanyId);
        cc.observe(this, new Observer<CustomerCompanyEntry>() {
            @Override
            public void onChanged(@Nullable CustomerCompanyEntry customerCompanyEntry) {
                cc.removeObserver(this);
                if (customerCompanyEntry != null) {
                    etCustomer.setText(customerCompanyEntry.getPersonCustomerCompanyName());
                } else {
                    etCustomer.setText(String.valueOf(salesorderEntry.getCustomerCompanyId()));
                }

                if (clearRelatedData) {
                    customerAddressId = null;
                    etAddress.setText("");
                    clearTempSoDetailEntry();
                }

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        List<CustomerCompanyAddressEntry> customerCompanyAddressEntryList = mDb.customerCompanyAddressDao().loadCustomerCompanyAddressesByPersonCustomerCompanyId(customerCompanyId);
                        if (customerCompanyAddressEntryList.size() == 1) {
                            final CustomerCompanyAddressEntry addr = customerCompanyAddressEntryList.get(0);
                            customerAddressId = addr.getId();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    etAddress.setText(addr.getPersonCustomerAddressName());
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void retrieveAddress() {
        final LiveData<CustomerCompanyAddressEntry> cca = mDb.customerCompanyAddressDao().loadLiveCustomerCompanyAddressById(customerAddressId);
        cca.observe(this, new Observer<CustomerCompanyAddressEntry>() {
            @Override
            public void onChanged(@Nullable CustomerCompanyAddressEntry customerCompanyAddressEntry) {
                cca.removeObserver(this);
                if (customerCompanyAddressEntry != null) {
                    etAddress.setText(customerCompanyAddressEntry.getPersonCustomerAddressName());
                } else {
                    etAddress.setText(String.valueOf(salesorderEntry.getCustomerAddressId()));
                }
            }
        });
    }

    private void clearTempSoDetailEntry() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.tempSalesorderDetailDao().deleteAll();
            }
        });
    }
}
