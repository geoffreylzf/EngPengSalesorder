package my.com.engpeng.engpengsalesorder.fragment;

import android.animation.LayoutTransition;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import my.com.engpeng.engpengsalesorder.Global;
import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.activity.NavigationHost;
import my.com.engpeng.engpengsalesorder.activity.SalesorderActivity;
import my.com.engpeng.engpengsalesorder.adapter.SoDashboardDateAdapter;
import my.com.engpeng.engpengsalesorder.adapter.SoDashboardSoAdapter;
import my.com.engpeng.engpengsalesorder.animation.BackdropMenuAnimation;
import my.com.engpeng.engpengsalesorder.animation.RevealAnimationSetting;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.salesorder.SalesorderEntry;
import my.com.engpeng.engpengsalesorder.database.salesorder.SoDisplay;
import my.com.engpeng.engpengsalesorder.database.salesorder.SoGroupByDateDisplay;
import my.com.engpeng.engpengsalesorder.database.salesorderDetail.SalesorderDetailEntry;
import my.com.engpeng.engpengsalesorder.database.tableList.TableInfoEntry;
import my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail.TempSalesorderDetailEntry;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;

import static my.com.engpeng.engpengsalesorder.Global.DATE_TYPE_DAY;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_REVEAL_ANIMATION_SETTINGS;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_SALESORDER_ENTRY;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_TRANSITION_NAME;
import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_CONFIRM;
import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_DRAFT;
import static my.com.engpeng.engpengsalesorder.Global.sCompanyId;

public class SoDashboardFragment extends Fragment {

    public static final String tag = "SO_DASHBOARD_FRAGMENT";

    private RecyclerView rvDate, rvSo;
    private FloatingActionButton fabAdd, fabRefresh;
    private Chip cpDocumentDate, cpStatus;
    private ChipGroup cgFilter;
    private View rootView;

    private AppDatabase mDb;
    private SoDashboardDateAdapter dateAdapter;
    private SoDashboardSoAdapter soAdapter;
    private Bundle savedInstanceState;

    private boolean backdropShow = false;
    private String currentFilterStatus;
    private String currentFilterDocDate;

    private String currentFilterDateType;
    private String currentFilterDateTypeValue;

    //for delete item
    private boolean isDeleting = false;
    private int deletePosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_so_dashboard, container, false);

        rvDate = rootView.findViewById(R.id.so_dashboard_rv_date);
        rvSo = rootView.findViewById(R.id.so_dashboard_rv_so);
        fabAdd = rootView.findViewById(R.id.so_dashboard_fab_add);
        fabRefresh = rootView.findViewById(R.id.so_dashboard_fab_refresh);
        cpDocumentDate = rootView.findViewById(R.id.so_dashboard_cp_document_date);
        cpStatus = rootView.findViewById(R.id.so_dashboard_cp_status);
        cgFilter = rootView.findViewById(R.id.so_dashboard_cg_filter);

        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());
        getActivity().setTitle("Salesorder Dashboard");
        this.savedInstanceState = savedInstanceState;
        ((SalesorderActivity) getActivity()).setAppBarLayoutElevation(0);

        setupRecycleView();
        setupListener();

        setupStatusFilter(Global.SO_STATUS_ALL);

        retrieveDateList("MONTH", StringUtils.getCurrentYearMonth());

        currentFilterDocDate = StringUtils.getCurrentDate();
        retrieveSoList();

        return rootView;
    }

    public void setupListener() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        TableInfoEntry tableInfoEntry = mDb.tableInfoDao().loadTableInfoByType(SalesorderEntry.TABLE_NAME);
                        if (tableInfoEntry != null &&
                                tableInfoEntry.getLastSyncDate() != null &&
                                tableInfoEntry.getInsert() == tableInfoEntry.getTotal()) {

                            RevealAnimationSetting revealAnimationSetting
                                    = new RevealAnimationSetting(
                                    (int) (fabAdd.getX() + fabAdd.getWidth() / 2),
                                    (int) (fabAdd.getY() + fabAdd.getHeight() / 2),
                                    rootView.getWidth(),
                                    rootView.getHeight());

                            TempSoHeadFragment tempSoHeadFragment = new TempSoHeadFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(I_KEY_REVEAL_ANIMATION_SETTINGS, Parcels.wrap(revealAnimationSetting));
                            tempSoHeadFragment.setArguments(bundle);

                            ((NavigationHost) getActivity()).navigateTo(tempSoHeadFragment, TempSoHeadFragment.tag, true, null, null);

                        } else {
                            UiUtils.showAlertDialog(getFragmentManager(), getString(R.string.error), getString(R.string.dialog_error_msg_perform_retrieve_history_first));
                        }
                    }
                });
            }
        });

        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveDateList(null, null);
            }
        });

        cpStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFilterStatus.equals(Global.SO_STATUS_ALL)) {
                    setupStatusFilter(Global.SO_STATUS_DRAFT);
                    retrieveDateList(currentFilterDateType, currentFilterDateTypeValue);
                    retrieveSoList();
                } else if (currentFilterStatus.equals(Global.SO_STATUS_DRAFT)) {
                    setupStatusFilter(Global.SO_STATUS_CONFIRM);
                    retrieveDateList(currentFilterDateType, currentFilterDateTypeValue);
                    retrieveSoList();
                } else if (currentFilterStatus.equals(Global.SO_STATUS_CONFIRM)) {
                    setupStatusFilter(Global.SO_STATUS_ALL);
                    retrieveDateList(currentFilterDateType, currentFilterDateTypeValue);
                    retrieveSoList();
                }
            }
        });

        cpDocumentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                triggerBackdrop();
            }
        });

        cgFilter.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }

    private void setupStatusFilter(String status) {
        currentFilterStatus = status;
        cpStatus.setText(getString(R.string.status).concat(": ").concat(StringUtils.getDisplaySoStatus(status)));
    }

    private void setupRecycleView() {
        rvDate.setLayoutManager(new LinearLayoutManager(getActivity()));
        dateAdapter = new SoDashboardDateAdapter(getActivity(), new SoDashboardDateAdapter.SoDashboardDateAdapterListener() {
            @Override
            public void onDateSelected(String dateType, String date) {
                if (dateType.equals(DATE_TYPE_DAY)) {
                    triggerBackdrop();
                    currentFilterDocDate = date;
                    retrieveSoList();
                } else {
                    retrieveDateList(dateType, date);
                }
            }
        });
        rvDate.setAdapter(dateAdapter);

        rvSo.setLayoutManager(new LinearLayoutManager(getActivity()));
        soAdapter = new SoDashboardSoAdapter(getActivity(), new SoDashboardSoAdapter.SoDashboardSoAdapterListener() {
            @Override
            public void onSoActionBtnClicked(long salesorderId) {
                openEditOrView(salesorderId);
            }

            @Override
            public void onSoDeleteBtnClicked(long salesorderId, int deletePosition) {
                SoDashboardFragment.this.deletePosition = deletePosition;
                deleteSalesorder(salesorderId);
            }
        });
        rvSo.setAdapter(soAdapter);

        rvSo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabAdd.hide();
                } else {
                    fabAdd.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void retrieveDateList(String dateType, String date) {
        currentFilterDateType = dateType;
        currentFilterDateTypeValue = date;
        final LiveData<List<SoGroupByDateDisplay>> ld
                = mDb.salesorderDao().
                loadAllSoGroupViaQuery(
                        SalesorderEntry.constructSoGroupQuery(dateType, sCompanyId, date, currentFilterStatus)
                );
        ld.observe(this, new Observer<List<SoGroupByDateDisplay>>() {
            @Override
            public void onChanged(List<SoGroupByDateDisplay> soGroupByDateDisplayList) {
                ld.removeObserver(this);
                dateAdapter.setList(soGroupByDateDisplayList);
            }
        });
    }

    private void retrieveSoList() {
        cpDocumentDate.setText(getString(R.string.short_document_date) + ": " + currentFilterDocDate);
        final LiveData<List<SoDisplay>> ld
                = mDb.salesorderDao()
                .loadAllSoDisplayViaQuery(
                        SalesorderEntry.constructSoDisplayQuery(currentFilterDocDate, sCompanyId, currentFilterStatus)
                );
        ld.observe(this, new Observer<List<SoDisplay>>() {
            @Override
            public void onChanged(@Nullable List<SoDisplay> soDisplays) {
                if (isDeleting) {
                    rvSo.setLayoutAnimation(null);
                    soAdapter.setListAfterDelete(soDisplays, deletePosition);
                    isDeleting = false;
                } else {
                    LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);
                    rvSo.setLayoutAnimation(controller);
                    soAdapter.setList(soDisplays);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.dashboard_so, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            triggerBackdrop();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void triggerBackdrop() {
        backdropShow = !backdropShow;
        BackdropMenuAnimation.showBackdropMenu(getContext(), rootView.findViewById(R.id.so_dashboard_cl), new AccelerateDecelerateInterpolator(), backdropShow);
    }

    private void openEditOrView(final Long salesorderId) {
        final LiveData<SalesorderEntry> ld
                = mDb.salesorderDao().loadLiveSalesorder(salesorderId);
        ld.observe(this, new Observer<SalesorderEntry>() {
            @Override
            public void onChanged(final SalesorderEntry salesorderEntry) {
                ld.removeObserver(this);
                final String status = salesorderEntry.getStatus();

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        //Copy Selected SO detail into Temp SO Detail
                        mDb.tempSalesorderDetailDao().deleteAll();
                        List<SalesorderDetailEntry> soDetailEntryList = mDb.salesorderDetailDao().loadAllSalesorderDetailsBySalesorderId(salesorderId);
                        List<TempSalesorderDetailEntry> tempSoDetailEntryList = new ArrayList<>();
                        for (SalesorderDetailEntry soDetailEntry : soDetailEntryList) {
                            TempSalesorderDetailEntry tempSoDetailEntry = new TempSalesorderDetailEntry(
                                    soDetailEntry.getItemPackingId(),
                                    soDetailEntry.getQty(),
                                    soDetailEntry.getWeight(),
                                    soDetailEntry.getFactor(),
                                    soDetailEntry.getPrice(),
                                    soDetailEntry.getPriceSettingId(),
                                    soDetailEntry.getPriceMethod(),
                                    soDetailEntry.getTotalPrice());
                            tempSoDetailEntryList.add(tempSoDetailEntry);
                        }
                        mDb.tempSalesorderDetailDao().insertTempSalesorderDetail(tempSoDetailEntryList);

                        if (status.equals(SO_STATUS_CONFIRM)) {


                            TempSoConfirmFragment tempSoConfirmFragment = new TempSoConfirmFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(I_KEY_SALESORDER_ENTRY, Parcels.wrap(salesorderEntry));
                            tempSoConfirmFragment.setArguments(bundle);

                            ((NavigationHost) getActivity()).navigateTo(
                                    tempSoConfirmFragment,
                                    TempSoConfirmFragment.tag,
                                    true,
                                    null,
                                    null
                            );

                        } else if (status.equals(SO_STATUS_DRAFT)) {

                            TempSoHeadFragment tempSoHeadFragment = new TempSoHeadFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(I_KEY_SALESORDER_ENTRY, Parcels.wrap(salesorderEntry));
                            tempSoHeadFragment.setArguments(bundle);
                            ((NavigationHost) getActivity()).navigateTo(tempSoHeadFragment, TempSoHeadFragment.tag, true, null, null);
                        }
                    }
                });
            }
        });
    }

    private void deleteSalesorder(final Long salesorderId) {
        UiUtils.showConfirmDialog(getFragmentManager(),
                getString(R.string.dialog_title_delete_draft),
                getString(R.string.dialog_msg_delete_draft),
                getString(R.string.dialog_btn_positive_delete_draft),
                new ConfirmDialogFragment.ConfirmDialogFragmentListener() {
                    @Override
                    public void onPositiveButtonClicked() {
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                isDeleting = true;
                                mDb.salesorderDetailDao().deleteAllBySalesorderId(salesorderId);
                                mDb.salesorderDao().deleteById(salesorderId);
                            }
                        });
                    }
                });
    }
}
