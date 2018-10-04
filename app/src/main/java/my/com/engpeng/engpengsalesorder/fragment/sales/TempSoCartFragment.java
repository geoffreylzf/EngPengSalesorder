package my.com.engpeng.engpengsalesorder.fragment.sales;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tubb.smrv.SwipeMenuRecyclerView;

import org.parceler.Parcels;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.activity.ItemSelectionActivity;
import my.com.engpeng.engpengsalesorder.activity.NavigationHost;
import my.com.engpeng.engpengsalesorder.adapter.TempSoCartAdapter;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.salesorder.SalesorderEntry;
import my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail.TempSalesorderDetailDisplay;
import my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail.TempSalesorderDetailEntry;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_CUSTOMER_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_DELIVERY_DATE;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_SALESORDER_ENTRY;

public class TempSoCartFragment extends Fragment {

    public static final String tag = "TEMP_SO_CART_FRAGMENT";

    private FloatingActionButton fabAdd;
    private TextView tvTotalPrice;
    private SwipeMenuRecyclerView rv;

    private AppDatabase mDb;
    private TempSoCartAdapter adapter;

    //receive from bundle
    private SalesorderEntry salesorderEntry;

    //for delete item
    private boolean isDeleting = false;
    private int deletePosition;

    private boolean allowRefresh = false;

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

        setupBundle();
        setupRecycleView();
        retrieveDetail();
        retrieveTotalPrice();
        setupListener();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (allowRefresh) {
            allowRefresh = false;

            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction trans = manager.beginTransaction();
            trans.remove(this);
            trans.commit();
            manager.popBackStack();

            TempSoCartFragment tempSoCartFragment = new TempSoCartFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(I_KEY_SALESORDER_ENTRY, Parcels.wrap(salesorderEntry));
            tempSoCartFragment.setArguments(bundle);
            ((NavigationHost) getActivity()).navigateTo(tempSoCartFragment, TempSoCartFragment.tag, true, null, null);
        }
    }

    private void setupBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            salesorderEntry = Parcels.unwrap(bundle.getParcelable(I_KEY_SALESORDER_ENTRY));
            if(salesorderEntry.getId() == 0){
                getActivity().setTitle("In New Cart List");
            }else{
                getActivity().setTitle("In Draft Cart List");
            }
        }
    }

    private void setupRecycleView() {
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TempSoCartAdapter(getActivity(), new TempSoCartAdapter.TempSalesorderSummaryAdapterListener() {
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
                rv.reset(); //reset swiped item
                if (isDeleting) {
                    adapter.setListAfterDelete(details, deletePosition);
                    isDeleting = false;
                } else {
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
                if (d == null) {
                    tvTotalPrice.setText(StringUtils.getDisplayPrice(0));
                } else {
                    tvTotalPrice.setText(StringUtils.getDisplayPrice(d));
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
        intent.putExtra(I_KEY_CUSTOMER_COMPANY_ID, salesorderEntry.getCustomerCompanyId());
        intent.putExtra(I_KEY_DELIVERY_DATE, salesorderEntry.getDeliveryDate());
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

            allowRefresh = true;

            final LiveData<List<TempSalesorderDetailEntry>> ld = mDb.tempSalesorderDetailDao().loadAllTempSalesorderDetails();
            ld.observe(this, new Observer<List<TempSalesorderDetailEntry>>() {
                @Override
                public void onChanged(@Nullable List<TempSalesorderDetailEntry> tempSalesorderDetailEntries) {
                    ld.removeObserver(this);

                    if(tempSalesorderDetailEntries.size() != 0){
                        TempSoConfirmFragment tempSoConfirmFragment = new TempSoConfirmFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(I_KEY_SALESORDER_ENTRY, Parcels.wrap(salesorderEntry));
                        tempSoConfirmFragment.setArguments(bundle);
                        ((NavigationHost) getActivity()).navigateTo(tempSoConfirmFragment, TempSoConfirmFragment.tag, true, null, null);

                    }else{
                        UiUtils.showAlertDialog(getFragmentManager(),
                                "Error",
                                getString(R.string.dialog_error_msg_so_no_item));
                    }
                }
            });

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
