package my.com.engpeng.engpengsalesorder.fragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tubb.smrv.SwipeMenuRecyclerView;

import java.util.List;

import my.com.engpeng.engpengsalesorder.Global;
import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.activity.ItemSelectionActivity;
import my.com.engpeng.engpengsalesorder.activity.NavigationHost;
import my.com.engpeng.engpengsalesorder.adapter.TempSalesorderSummaryAdapter;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail.TempSalesorderDetailDisplay;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_CUSTOMER_ADDRESS_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_CUSTOMER_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_DELIVERY_DATE;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_DOCUMENT_DATE;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_LPO;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_REMARK;

public class TempSoCartFragment extends Fragment {

    private FloatingActionButton fabAdd;
    private TextView tvTotalPrice;
    private SwipeMenuRecyclerView rv;

    private AppDatabase mDb;
    private TempSalesorderSummaryAdapter adapter;

    //receive from bundle
    private Long companyId;
    private Long customerCompanyId;
    private Long customerAddressId;
    private String documentDate;
    private String deliveryDate;
    private String lpo;
    private String remark;

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
        View rootView = inflater.inflate(R.layout.fragment_temp_so_cart, container, false);

        rv = rootView.findViewById(R.id.temp_so_cart_rv);
        fabAdd = rootView.findViewById(R.id.temp_so_cart_fab_add);
        tvTotalPrice = rootView.findViewById(R.id.temp_so_cart_tv_total_price);

        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());
        getActivity().setTitle("In Cart List");

        setupBundle();
        setupRecycleView();
        retrieveDetail();
        retrieveTotalPrice();
        setupListener();

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

    private void setupRecycleView() {
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TempSalesorderSummaryAdapter(getActivity(), new TempSalesorderSummaryAdapter.TempSalesorderSummaryAdapterListener() {
            @Override
            public void afterItemDelete(final long item_packing_id, final int position) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        isDeleting = true;
                        deletePosition = position;
                        mDb.tempSalesorderDetailDao().deleteByItemPackingId(item_packing_id);
                    }
                });
            }
        });
        rv.setAdapter(adapter);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    private void retrieveDetail() {
        final LiveData<List<TempSalesorderDetailDisplay>> cc = mDb.tempSalesorderDetailDao().loadAllTempSalesorderDetailsWithItemPacking();
        cc.observe(this, new Observer<List<TempSalesorderDetailDisplay>>() {
            @Override
            public void onChanged(@Nullable List<TempSalesorderDetailDisplay> details) {
                if (isDeleting) {
                    rv.reset();
                    adapter.setListAfterDelete(details, deletePosition);
                    isDeleting = false;
                } else {
                    rv.reset();
                    adapter.setList(details);
                }
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

    private void setupListener() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callItemSelection();
            }
        });
    }

    private void callItemSelection() {
        Intent intent = new Intent(getActivity(), ItemSelectionActivity.class);
        intent.putExtra(I_KEY_CUSTOMER_COMPANY_ID, customerCompanyId);
        intent.putExtra(I_KEY_DELIVERY_DATE, deliveryDate);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.temp_so_cart, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_checkout) {

            TempSoConfirmFragment tempSoConfirmFragment = new TempSoConfirmFragment();
            Bundle bundle = new Bundle();
            bundle.putLong(I_KEY_COMPANY_ID, companyId);
            bundle.putLong(I_KEY_CUSTOMER_COMPANY_ID, customerCompanyId);
            bundle.putLong(I_KEY_CUSTOMER_ADDRESS_ID, customerAddressId);
            bundle.putString(I_KEY_DOCUMENT_DATE, documentDate);
            bundle.putString(I_KEY_DELIVERY_DATE, deliveryDate);
            bundle.putString(I_KEY_LPO, lpo);
            bundle.putString(I_KEY_REMARK, remark);
            tempSoConfirmFragment.setArguments(bundle);
            ((NavigationHost) getActivity()).navigateTo(tempSoConfirmFragment, true);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
