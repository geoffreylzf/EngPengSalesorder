package my.com.engpeng.engpengsalesorder.fragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.activity.NavigationHost;
import my.com.engpeng.engpengsalesorder.activity.SalesorderActivity;
import my.com.engpeng.engpengsalesorder.adapter.SoDashboardDateAdapter;
import my.com.engpeng.engpengsalesorder.adapter.SoDashboardSoAdapter;
import my.com.engpeng.engpengsalesorder.animation.BackdropMenuAnimation;
import my.com.engpeng.engpengsalesorder.animation.RevealAnimationSetting;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.salesorder.SoDisplay;
import my.com.engpeng.engpengsalesorder.database.salesorder.SoGroupByDateDisplay;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;

import static my.com.engpeng.engpengsalesorder.Global.DATE_TYPE_DAY;
import static my.com.engpeng.engpengsalesorder.Global.DATE_TYPE_MONTH;
import static my.com.engpeng.engpengsalesorder.Global.DATE_TYPE_YEAR;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_REVEAL_ANIMATION_SETTINGS;

public class SoDashboardFragment extends Fragment {

    public static final String tag = "SO_DASHBOARD_FRAGMENT";

    private RecyclerView rvDate, rvSo;
    private FloatingActionButton fabAdd, fabRefresh;
    private Chip cpDocumentDate;
    private View rootView;

    private AppDatabase mDb;
    private SoDashboardDateAdapter dateAdapter;
    private SoDashboardSoAdapter soAdapter;

    private boolean backdropShow = false;

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

        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());
        getActivity().setTitle("Salesorder Dashboard");
        ((SalesorderActivity) getActivity()).setAppBarLayoutElevation(0);

        setupRecycleView();
        retrieveDateList("MONTH", StringUtils.getCurrentYearMonth());
        retrieveSoList(StringUtils.getCurrentDate());
        setupListener();

        return rootView;
    }

    public void setupListener() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RevealAnimationSetting revealAnimationSetting
                        = new RevealAnimationSetting(
                        (int) (fabAdd.getX() + fabAdd.getWidth() / 2),
                        (int) (fabAdd.getY() + fabAdd.getHeight() / 2),
                        rootView.getWidth(),
                        rootView.getHeight());

                TempSoHeadFragment tempSoHeadFragment = new TempSoHeadFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(I_KEY_REVEAL_ANIMATION_SETTINGS, revealAnimationSetting);
                tempSoHeadFragment.setArguments(bundle);

                ((NavigationHost) getActivity()).navigateTo(tempSoHeadFragment, TempSoHeadFragment.tag, true);

            }
        });

        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveDateList("", "");
            }
        });
    }

    private void setupRecycleView() {
        rvDate.setLayoutManager(new LinearLayoutManager(getActivity()));
        dateAdapter = new SoDashboardDateAdapter(getActivity(), new SoDashboardDateAdapter.SoDashboardDateAdapterListener() {
            @Override
            public void onDateSelected(String dateType, String date) {
                if (dateType.equals(DATE_TYPE_DAY)) {
                    triggerBackdrop();
                    retrieveSoList(date);
                } else {
                    retrieveDateList(dateType, date);
                }
            }
        });
        rvDate.setAdapter(dateAdapter);

        rvSo.setLayoutManager(new LinearLayoutManager(getActivity()));
        soAdapter = new SoDashboardSoAdapter(getActivity(), new SoDashboardSoAdapter.SoDashboardSoAdapterListener() {
            @Override
            public void onSoSelected(long id) {
                //TODO
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
        if (dateType.equals("")) {
            final LiveData<List<SoGroupByDateDisplay>> ld = mDb.salesorderDao().loadAllSoGroupByYearDisplay();
            ld.observe(this, new Observer<List<SoGroupByDateDisplay>>() {
                @Override
                public void onChanged(List<SoGroupByDateDisplay> soGroupByDateDisplayList) {
                    dateAdapter.setList(soGroupByDateDisplayList);
                }
            });
        } else if (dateType.equals(DATE_TYPE_YEAR)) {
            final LiveData<List<SoGroupByDateDisplay>> ld = mDb.salesorderDao().loadAllSoGroupByYearMonthDisplayByYear(date);
            ld.observe(this, new Observer<List<SoGroupByDateDisplay>>() {
                @Override
                public void onChanged(List<SoGroupByDateDisplay> soGroupByDateDisplayList) {
                    dateAdapter.setList(soGroupByDateDisplayList);
                }
            });
        } else if (dateType.equals(DATE_TYPE_MONTH)) {
            final LiveData<List<SoGroupByDateDisplay>> ld = mDb.salesorderDao().loadAllSoGroupByDateDisplay(date);
            ld.observe(this, new Observer<List<SoGroupByDateDisplay>>() {
                @Override
                public void onChanged(List<SoGroupByDateDisplay> soGroupByDateDisplayList) {
                    dateAdapter.setList(soGroupByDateDisplayList);
                }
            });
        }
    }

    private void retrieveSoList(String documentDate) {
        cpDocumentDate.setText("Document Date: " + documentDate);
        final LiveData<List<SoDisplay>> ld = mDb.salesorderDao().loadAllSoDisplayByDocumentDate(documentDate);
        ld.observe(this, new Observer<List<SoDisplay>>() {
            @Override
            public void onChanged(@Nullable List<SoDisplay> soDisplays) {
                LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_from_bottom);
                rvSo.setLayoutAnimation(controller);
                soAdapter.setList(soDisplays);
                rvSo.scheduleLayoutAnimation();
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
}
