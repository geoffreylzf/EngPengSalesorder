package my.com.engpeng.engpengsalesorder.fragment.sales;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import my.com.engpeng.engpengsalesorder.fragment.dialog.ConfirmDialogFragment;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;

import static my.com.engpeng.engpengsalesorder.Global.DATE_DISPLAY_FORMAT;
import static my.com.engpeng.engpengsalesorder.Global.DATE_SAVE_FORMAT;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_SALESORDER_ENTRY;
import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_CONFIRM;
import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_DRAFT;
import static my.com.engpeng.engpengsalesorder.Global.sCompanyCode;
import static my.com.engpeng.engpengsalesorder.Global.sUsername;

public class TempSoConfirmFragment extends Fragment {

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
                setHasOptionsMenu(true);
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
                if (branchEntry != null) {
                    etCompany.setText(branchEntry.getBranchName());
                } else {
                    etCompany.setText(String.valueOf(salesorderEntry.getCompanyId()));
                }

            }
        });
    }

    private void retrieveCustomer() {
        final LiveData<CustomerCompanyEntry> cc = mDb.customerCompanyDao().loadLiveCustomerCompanyById(salesorderEntry.getCustomerCompanyId());
        cc.observe(this, new Observer<CustomerCompanyEntry>() {
            @Override
            public void onChanged(@Nullable CustomerCompanyEntry customerCompanyEntry) {
                cc.removeObserver(this);
                if (customerCompanyEntry != null) {
                    etCustomer.setText(customerCompanyEntry.getPersonCustomerCompanyName());
                } else {
                    etCustomer.setText(String.valueOf(salesorderEntry.getCustomerCompanyId()));
                }
            }
        });
    }

    private void retrieveAddress() {
        final LiveData<CustomerCompanyAddressEntry> cca = mDb.customerCompanyAddressDao().loadLiveCustomerCompanyAddressById(salesorderEntry.getCustomerAddressId());
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

    private void setupRecycleView() {
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TempSoConfirmAdapter(getActivity());
        rv.setAdapter(adapter);

        final LiveData<List<TempSalesorderDetailDisplay>> cc = mDb.tempSalesorderDetailDao().loadAllTempSalesorderDetailsWithItemPacking();
        cc.observe(this, new Observer<List<TempSalesorderDetailDisplay>>() {
            @Override
            public void onChanged(@Nullable List<TempSalesorderDetailDisplay> details) {
                cc.removeObserver(this);
                adapter.setList(details);
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
                salesorderEntry.setId(salesorderId);

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

                ((NavigationHost) getActivity()).navigateDefault();
                if (salesorderEntry.getStatus().equals(SO_STATUS_CONFIRM)) {
                    openPrintPreview();
                }
            }
        });
    }

    private void initSaveSO() {
        if (tempSalesorderDetailEntries.size() != 0) {
            if (status.equals(SO_STATUS_DRAFT)) {
                UiUtils.showConfirmDialog(getFragmentManager(),
                        getString(R.string.dialog_title_so_draft),
                        getString(R.string.dialog_msg_so_draft),
                        getString(R.string.dialog_btn_positive_so_draft),
                        new ConfirmDialogFragment.ConfirmDialogFragmentListener() {
                            @Override
                            public void onPositiveButtonClicked() {
                                saveSo(null);
                            }
                        });
            } else if (status.equals(SO_STATUS_CONFIRM)) {
                UiUtils.showConfirmDialog(getFragmentManager(),
                        getString(R.string.dialog_title_so_confirm),
                        getString(R.string.dialog_msg_so_confirm),
                        getString(R.string.dialog_btn_positive_so_confirm),
                        new ConfirmDialogFragment.ConfirmDialogFragmentListener() {
                            @Override
                            public void onPositiveButtonClicked() {
                                saveSo(runningNo);
                            }
                        });
            }
        } else {
            UiUtils.showAlertDialog(getFragmentManager(),
                    "Error",
                    getString(R.string.dialog_error_msg_so_no_item));
        }
    }

    private void constructRunningNo() {
        final String prefix = sUsername + "-" + sCompanyCode + "-" + Global.RUNNING_CODE_SALESORDER + "-" + StringUtils.getSoYearMonthFormat(salesorderEntry.getDocumentDate());
        final String defaultRunningNo = prefix + "-001";

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String lastRunningNo = mDb.salesorderDao().getLastRunningNoByPrefix(prefix + "%");
                if (lastRunningNo == null) {
                    runningNo = defaultRunningNo;
                } else {
                    String[] arr = lastRunningNo.split("-");
                    String newNo = String.format(Locale.getDefault(), "%03d", Integer.parseInt(arr[4]) + 1);
                    runningNo = prefix + "-" + newNo;
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.temp_so_confirm, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_print) {

            openPrintPreview();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPrintPreview(){
        SoPrintFragment soPrintFragment = new SoPrintFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(I_KEY_SALESORDER_ENTRY, Parcels.wrap(salesorderEntry));
        soPrintFragment.setArguments(bundle);
        ((NavigationHost) getActivity()).navigateTo(soPrintFragment, SoPrintFragment.tag, true, null, null);
    }
}