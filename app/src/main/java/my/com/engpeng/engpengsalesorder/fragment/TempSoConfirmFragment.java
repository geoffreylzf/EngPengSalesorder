package my.com.engpeng.engpengsalesorder.fragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.parceler.Parcels;

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
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_SALESORDER_ENTRY;
import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_CONFIRM;
import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_DRAFT;
import static my.com.engpeng.engpengsalesorder.Global.sUsername;

public class TempSoConfirmFragment extends Fragment implements ConfirmDialogFragment.ConfirmDialogFragmentListener {

    public static final String tag = "TEMP_SO_CONFIRM_FRAGMENT";

    private EditText etCompany, etCustomer, etAddress, etDocumentDate, etDeliveryDate, etLpo, etRemark;
    private TextView tvTotalPrice;
    private RecyclerView rv;
    private Button btnDraft, btnConfirm;
    private LinearLayout llAction;

    private AppDatabase mDb;
    private SimpleDateFormat sdfSave, sdfDisplay;
    private TempSoConfirmAdapter adapter;

    //receive from bundle
    private SalesorderEntry salesorderEntry;

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
        llAction = rootView.findViewById(R.id.temp_so_confirm_ll_action);
        btnDraft = rootView.findViewById(R.id.temp_so_confirm_btn_draft);
        btnConfirm = rootView.findViewById(R.id.temp_so_confirm_btn_confirm);

        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());
        sdfDisplay = new SimpleDateFormat(DATE_DISPLAY_FORMAT, Locale.US);
        sdfSave = new SimpleDateFormat(DATE_SAVE_FORMAT, Locale.US);

        setupBundle();
        setupRecycleView();
        retrieveTotalPrice();
        setupListener();

        return rootView;
    }

    private void setupBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            salesorderEntry = Parcels.unwrap(bundle.getParcelable(I_KEY_SALESORDER_ENTRY));
            populateHeadInfo();

            String status = salesorderEntry.getStatus();
            if (status == null || status.equals("")) {
                constructRunningNo();
                getActivity().setTitle("Salesorder Confirmation");
            } else if (status.equals(SO_STATUS_CONFIRM)) {
                llAction.setVisibility(View.GONE);
                getActivity().setTitle("Salesorder Summary");
            } else if (status.equals(SO_STATUS_DRAFT)) {
                constructRunningNo();
                getActivity().setTitle("Saved Draft Salesorder");
            }
        }
    }

    private void populateHeadInfo() {
        String lpo = salesorderEntry.getLpo();
        String remark = salesorderEntry.getRemark();
        etLpo.setText(lpo.equals("") ? " " : lpo);
        etRemark.setText(remark.equals("") ? " " : remark);

        retrieveBranch();
        retrieveCustomer();
        retrieveAddress();

        try {
            etDocumentDate.setText(sdfDisplay.format(sdfSave.parse(salesorderEntry.getDocumentDate()).getTime()));
            etDeliveryDate.setText(sdfDisplay.format(sdfSave.parse(salesorderEntry.getDeliveryDate()).getTime()));
        } catch (ParseException e) {
            etDocumentDate.setText(salesorderEntry.getDocumentDate());
            etDeliveryDate.setText(salesorderEntry.getDeliveryDate());
        }
    }

    private void retrieveBranch() {
        final LiveData<BranchEntry> cc = mDb.branchDao().loadLiveBranchById(salesorderEntry.getCompanyId());
        cc.observe(this, new Observer<BranchEntry>() {
            @Override
            public void onChanged(@Nullable BranchEntry branchEntry) {
                cc.removeObserver(this);
                etCompany.setText(branchEntry.getBranchName());
            }
        });
    }

    private void retrieveCustomer() {
        final LiveData<CustomerCompanyEntry> cc = mDb.customerCompanyDao().loadLiveCustomerCompanyById(salesorderEntry.getCustomerCompanyId());
        cc.observe(this, new Observer<CustomerCompanyEntry>() {
            @Override
            public void onChanged(@Nullable CustomerCompanyEntry customerCompanyEntry) {
                cc.removeObserver(this);
                etCustomer.setText(customerCompanyEntry.getPersonCustomerCompanyName());
            }
        });
    }

    private void retrieveAddress() {
        final LiveData<CustomerCompanyAddressEntry> cca = mDb.customerCompanyAddressDao().loadLiveCustomerCompanyAddressById(salesorderEntry.getCustomerAddressId());
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
        final String existStatus = salesorderEntry.getStatus();
        if (existStatus == null || !existStatus.equals(SO_STATUS_DRAFT)) {
            salesorderEntry.setCreateDatetime(StringUtils.getCurrentDateTime());
        }

        salesorderEntry.setStatus(status);
        salesorderEntry.setRunningNo(newRunningNo);
        salesorderEntry.setIsUpload(0);
        salesorderEntry.setModifyDatetime(StringUtils.getCurrentDateTime());

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (existStatus != null && existStatus.equals(SO_STATUS_DRAFT)) { //clear exist detail if draft
                    mDb.salesorderDetailDao().deleteAllBySalesorderId(salesorderEntry.getId());
                }

                Long salesorderId = mDb.salesorderDao().insertSalesorder(salesorderEntry);

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
                UiUtils.showConfirmDialog(getFragmentManager(), this,
                        getString(R.string.dialog_title_so_draft),
                        getString(R.string.dialog_msg_so_draft),
                        getString(R.string.dialog_btn_positive_so_draft));
            } else if (status.equals(SO_STATUS_CONFIRM)) {
                UiUtils.showConfirmDialog(getFragmentManager(), this,
                        getString(R.string.dialog_title_so_confirm),
                        getString(R.string.dialog_msg_so_confirm),
                        getString(R.string.dialog_btn_positive_so_confirm));
            }
        } else {
            UiUtils.showAlertDialog(getFragmentManager(),
                    "Error",
                    getString(R.string.dialog_error_msg_so_no_item));
        }
    }

    private void constructRunningNo() {
        final String prefix = sUsername + "-" + StringUtils.getSoYearMonthFormat(salesorderEntry.getDocumentDate()) + "-" + Global.RUNNING_CODE_SALESORDER;
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