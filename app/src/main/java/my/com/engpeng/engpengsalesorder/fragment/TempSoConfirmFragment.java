package my.com.engpeng.engpengsalesorder.fragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import my.com.engpeng.engpengsalesorder.Global;
import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.activity.NavigationHost;
import my.com.engpeng.engpengsalesorder.adapter.TempSoConfirmAdapter;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.salesorderDetail.SalesorderDetailEntry;
import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompany.CustomerCompanyEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompanyAddress.CustomerCompanyAddressEntry;
import my.com.engpeng.engpengsalesorder.database.salesorder.SalesorderEntry;
import my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail.TempSalesorderDetailDisplay;
import my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail.TempSalesorderDetailEntry;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;

import static my.com.engpeng.engpengsalesorder.Global.DATE_DISPLAY_FORMAT;
import static my.com.engpeng.engpengsalesorder.Global.DATE_SAVE_FORMAT;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_CUSTOMER_ADDRESS_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_CUSTOMER_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_DELIVERY_DATE;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_DOCUMENT_DATE;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_LPO;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_REMARK;
import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_CONFIRM;
import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_DRAFT;
import static my.com.engpeng.engpengsalesorder.Global.sUsername;

public class TempSoConfirmFragment extends Fragment implements ConfirmDialogFragment.ConfirmDialogFragmentListener {

    public static final String tag = "TEMP_SO_CONFIRM_FRAGMENT";

    private EditText etCompany, etCustomer, etAddress, etDocumentDate, etDeliveryDate, etLpo, etRemark;
    private TextView tvTotalPrice;
    private RecyclerView rv;
    private Button btnDraft, btnConfirm;

    private AppDatabase mDb;
    private SimpleDateFormat sdfSave, sdfDisplay;
    private TempSoConfirmAdapter adapter;

    //receive from bundle
    private Long companyId;
    private Long customerCompanyId;
    private Long customerAddressId;
    private String documentDate;
    private String deliveryDate;
    private String lpo;
    private String remark;

    private String status;
    private String runningNo;

    private List<TempSalesorderDetailEntry> tempSalesorderDetailEntries;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_temp_so_confirm, container, false);

        etCompany = rootView.findViewById(R.id.temp_so_confirm_et_company);
        etCustomer = rootView.findViewById(R.id.temp_so_confirm_et_customer_company);
        etAddress = rootView.findViewById(R.id.temp_so_confirm_et_customer_address);
        etDocumentDate = rootView.findViewById(R.id.temp_so_confirm_et_document_date);
        etDeliveryDate = rootView.findViewById(R.id.temp_so_confirm_et_delivery_date);
        etLpo = rootView.findViewById(R.id.temp_so_confirm_et_lpo);
        etRemark = rootView.findViewById(R.id.temp_so_confirm_et_remark);
        rv = rootView.findViewById(R.id.temp_so_confirm_rv);
        tvTotalPrice = rootView.findViewById(R.id.temp_so_confirm_tv_total_price);
        btnDraft = rootView.findViewById(R.id.temp_so_confirm_btn_draft);
        btnConfirm = rootView.findViewById(R.id.temp_so_confirm_btn_confirm);

        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());
        sdfDisplay = new SimpleDateFormat(DATE_DISPLAY_FORMAT, Locale.US);
        sdfSave = new SimpleDateFormat(DATE_SAVE_FORMAT, Locale.US);

        getActivity().setTitle("Salesorder Confirm");

        setupBundle();
        setupHeadInfo();
        setupRecycleView();
        retrieveTotalPrice();
        setupListener();
        constructRunningNo();

        return rootView;
    }

    private void setupBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            companyId = bundle.getLong(I_KEY_COMPANY_ID, 0);
            customerCompanyId = bundle.getLong(I_KEY_CUSTOMER_COMPANY_ID, 0);
            customerAddressId = bundle.getLong(I_KEY_CUSTOMER_ADDRESS_ID, 0);
            documentDate = bundle.getString(I_KEY_DOCUMENT_DATE);
            deliveryDate = bundle.getString(I_KEY_DELIVERY_DATE);
            lpo = bundle.getString(I_KEY_LPO);
            remark = bundle.getString(I_KEY_REMARK);
        }
    }

    private void setupHeadInfo() {
        etLpo.setText(lpo.equals("") ? " " : lpo);
        etRemark.setText(remark.equals("") ? " " : remark);

        retrieveBranch();
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

    private void retrieveBranch() {
        final LiveData<BranchEntry> cc = mDb.branchDao().loadLiveBranchById(companyId);
        cc.observe(this, new Observer<BranchEntry>() {
            @Override
            public void onChanged(@Nullable BranchEntry branchEntry) {
                cc.removeObserver(this);
                etCompany.setText(branchEntry.getBranchName());
            }
        });
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
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TempSoConfirmAdapter(getActivity());
        rv.setAdapter(adapter);

        final LiveData<List<TempSalesorderDetailDisplay>> cc = mDb.tempSalesorderDetailDao().loadAllTempSalesorderDetailsWithItemPacking();
        cc.observe(this, new Observer<List<TempSalesorderDetailDisplay>>() {
            @Override
            public void onChanged(@Nullable List<TempSalesorderDetailDisplay> details) {
                cc.removeObserver(this);
                LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_from_right);
                rv.setLayoutAnimation(controller);
                adapter.setList(details);
                rv.scheduleLayoutAnimation();
            }
        });

        final LiveData<List<TempSalesorderDetailEntry>> ld = mDb.tempSalesorderDetailDao().loadAllTempSalesorderDetails();
        ld.observe(TempSoConfirmFragment.this, new Observer<List<TempSalesorderDetailEntry>>() {
            @Override
            public void onChanged(@Nullable List<TempSalesorderDetailEntry> tempSalesorderDetailEntries) {
                ld.removeObserver(this);
                TempSoConfirmFragment.this.tempSalesorderDetailEntries = tempSalesorderDetailEntries;
            }
        });
    }

    private void retrieveTotalPrice() {
        final LiveData<Double> cc = mDb.tempSalesorderDetailDao().getLiveSumTotalPrice();
        cc.observe(this, new Observer<Double>() {
            @Override
            public void onChanged(@Nullable Double d) {
                if (d == null) {
                    tvTotalPrice.setText(StringUtils.getDisplayPrice(0));
                } else {
                    tvTotalPrice.setText(StringUtils.getDisplayPrice(d));
                }
            }
        });
    }

    private void setupListener() {
        btnDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = SO_STATUS_DRAFT;
                initSaveSO();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = SO_STATUS_CONFIRM;
                initSaveSO();
            }
        });
    }

    private void saveSo(String newRunningNo) {
        final SalesorderEntry salesorderEntry =
                new SalesorderEntry(companyId,
                        customerCompanyId,
                        customerAddressId,
                        documentDate,
                        deliveryDate,
                        lpo,
                        remark,
                        status,
                        newRunningNo,
                        0,
                        StringUtils.getCurrentDateTime(),
                        StringUtils.getCurrentDateTime());

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final Long salesorderId = mDb.salesorderDao().insertSalesorder(salesorderEntry);

                for (TempSalesorderDetailEntry tempSalesorderDetailEntry : tempSalesorderDetailEntries) {
                    SalesorderDetailEntry salesorderDetailEntry =
                            new SalesorderDetailEntry(salesorderId,
                                    tempSalesorderDetailEntry.getItemPackingId(),
                                    tempSalesorderDetailEntry.getPriceSettingId(),
                                    tempSalesorderDetailEntry.getPriceMethod(),
                                    tempSalesorderDetailEntry.getQty(),
                                    tempSalesorderDetailEntry.getWeight(),
                                    tempSalesorderDetailEntry.getFactor(),
                                    tempSalesorderDetailEntry.getPrice(),
                                    tempSalesorderDetailEntry.getTotalPrice());

                    mDb.salesorderDetailDao().insertSalesorderDetail(salesorderDetailEntry);
                }

                ((NavigationHost) getActivity()).clearAllNavigateTo(new SoDashboardFragment(), SoDashboardFragment.tag);
            }
        });
    }

    private void initSaveSO() {
        if (tempSalesorderDetailEntries.size() != 0) {
            if (status.equals(SO_STATUS_DRAFT)) {
                UiUtils.showConfirmDialog(getFragmentManager(), this, "Are you sure to save as draft?", "Upload is not allowed in draft", "Save as draft");
            } else if (status.equals(SO_STATUS_CONFIRM)) {
                UiUtils.showConfirmDialog(getFragmentManager(), this, "Are you sure to confirm?", "Edit is not allowed after confirm.", "Confirm");
            }
        } else {
            UiUtils.showAlertDialog(getFragmentManager(), "Error", "Please add atleast 1 item to save.");
        }
    }

    private void constructRunningNo() {
        final String prefix = sUsername + "-" + StringUtils.getCurrentYear() + "-" + Global.RUNNING_CODE_SALESORDER;
        final String defaultRunningNo = prefix + "-1";

        final LiveData<String> ld = mDb.salesorderDao().getLastRunningNoByPrefix(prefix + "%");
        ld.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String lastRunningNo) {
                ld.removeObserver(this);
                if (lastRunningNo == null) {
                    runningNo = defaultRunningNo;
                } else {
                    String[] arr = lastRunningNo.split("-");
                    int newNo = Integer.parseInt(arr[3]) + 1;
                    runningNo = prefix + "-" + newNo;
                }
                Log.e("runningNo", runningNo);
            }
        });
    }

    @Override
    public void onPositiveButtonClicked() {
        if (status.equals(SO_STATUS_DRAFT)) {
            saveSo(null);
        } else if (status.equals(SO_STATUS_CONFIRM)) {
            saveSo(runningNo);
        } else {
            UiUtils.showAlertDialog(getFragmentManager(), "Error", getString(R.string.msg_unexpected_error));
        }
    }


}