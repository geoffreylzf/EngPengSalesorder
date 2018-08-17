package my.com.engpeng.engpengsalesorder.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.adapter.CustomerSelectionAdapter;
import my.com.engpeng.engpengsalesorder.adapter.ItemSelectionAdapter;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingEntry;
import my.com.engpeng.engpengsalesorder.fragment.SearchBarFragment;

public class ItemSelectionActivity extends AppCompatActivity implements SearchBarFragment.SearchBarFragmentListener {

    private Toolbar tb;
    private DrawerLayout dl;
    private NavigationView nvStart;

    private RecyclerView rv;
    private ItemSelectionAdapter adapter;

    private AppDatabase mDb;
    public static final String ITEM_PACKING_ID = "ITEM_PACKING_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_selection);

        setTitle("Item Packing Selection");

        this.getWindow().setStatusBarColor(this.getColor(R.color.colorTransparent));

        tb = findViewById(R.id.item_selection_tb);
        dl = findViewById(R.id.item_selection_dl);
        nvStart = findViewById(R.id.item_selection_start_nv);
        rv = findViewById(R.id.item_selection_rv_list);

        mDb = AppDatabase.getInstance(getApplicationContext());

        setupLayout();
        setupRecycleView();
        retrieveItemPacking("");
    }

    private void setupLayout() {
        setSupportActionBar(tb);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dl, tb,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dl.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupRecycleView() {
        //rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new ItemSelectionAdapter(this, new ItemSelectionAdapter.ItemSelectionAdapterListener() {
            @Override
            public void onItemSelected(Long id) {
                Intent data = new Intent();
                data.putExtra(ITEM_PACKING_ID, id);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        rv.setAdapter(adapter);
    }

    private void retrieveItemPacking(final String filter) {
        final LiveData<List<ItemPackingEntry>> cc = mDb.itemPackingDao().loadLiveAllItemPackingsByFilter("%" + filter + "%");
        cc.observe(this, new Observer<List<ItemPackingEntry>>() {
            @Override
            public void onChanged(@Nullable List<ItemPackingEntry> itemPackingEntries) {
                cc.removeObserver(this);
                adapter.setItemEntryList(itemPackingEntries);
            }
        });
    }

    @Override
    public void onFilterChanged(String filter) {
        retrieveItemPacking(filter);
    }
}
