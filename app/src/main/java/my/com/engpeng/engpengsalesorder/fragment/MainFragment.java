package my.com.engpeng.engpengsalesorder.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.activity.MainActivity;
import my.com.engpeng.engpengsalesorder.activity.NavigationHost;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;
import my.com.engpeng.engpengsalesorder.fragment.main.CompanyFragment;
import my.com.engpeng.engpengsalesorder.fragment.main.HistoryFragment;
import my.com.engpeng.engpengsalesorder.fragment.main.MainDashboardFragment;
import my.com.engpeng.engpengsalesorder.fragment.main.HouseKeepingFragment;
import my.com.engpeng.engpengsalesorder.fragment.main.UploadFragment;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;

import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_CONFIRM;
import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_DRAFT;
import static my.com.engpeng.engpengsalesorder.Global.sUniqueId;
import static my.com.engpeng.engpengsalesorder.Global.sUsername;

public class MainFragment extends Fragment implements NavigationHost {

    public static final String tag = "MAIN_FRAGMENT";

    private Toolbar tb;
    private DrawerLayout dl;
    private NavigationView nvStart;
    private TextView navStartTvUsername, navStartTvUniqueId;

    private Bundle savedInstanceState;
    private AppDatabase mDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        tb = rootView.findViewById(R.id.f_main_tb);
        dl = rootView.findViewById(R.id.main_dl);
        nvStart = rootView.findViewById(R.id.f_main_start_nv);

        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());
        this.savedInstanceState = savedInstanceState;

        setupDrawerLayout();
        setupMainDashboard();

        return rootView;
    }

    private void setupDrawerLayout() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(tb);
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), dl, tb,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dl.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = nvStart.getHeaderView(0);
        navStartTvUsername = headerView.findViewById(R.id.header_main_drawer_start_tv_username);
        navStartTvUniqueId = headerView.findViewById(R.id.header_main_drawer_start_tv_unique_id);

        navStartTvUsername.setText(sUsername);
        navStartTvUniqueId.setText(sUniqueId);

        nvStart.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.main_drawer_start_company) {
                    navigateTo(new CompanyFragment(), CompanyFragment.tag, true, null, null);
                } else if (id == R.id.main_drawer_start_house_keeping) {
                    navigateTo(new HouseKeepingFragment(), HouseKeepingFragment.tag, true, null, null);
                } else if (id == R.id.main_drawer_start_history) {
                    navigateTo(new HistoryFragment(), HistoryFragment.tag, true, null, null);
                } else if (id == R.id.main_drawer_start_upload) {
                    navigateTo(new UploadFragment(), UploadFragment.tag, true, null, null);
                } else if (id == R.id.main_drawer_start_house_log) {
                    onPerformLogout();
                }

                dl.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void setupMainDashboard() {
        if (savedInstanceState == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.f_main_fl, new MainDashboardFragment(), MainDashboardFragment.tag)
                    .commit();
        }
    }

    private void onPerformLogout() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int unuploadCount = mDb.salesorderDao().getCountByStatusUpload(SO_STATUS_CONFIRM, 0);
                if (unuploadCount != 0) {
                    UiUtils.showAlertDialog(getFragmentManager(), getString(R.string.error), getString(R.string.dialog_error_msg_got_un_upload));
                } else {
                    int draftCount = mDb.salesorderDao().getCountByStatusUpload(SO_STATUS_DRAFT, 0);
                    if (draftCount != 0) {
                        UiUtils.showConfirmDialog(getFragmentManager(),
                                getString(R.string.dialog_title_logout_with_draft),
                                getString(R.string.dialog_msg_logout_with_draft),
                                getString(R.string.dialog_btn_positive_logout_with_draft),
                                new ConfirmDialogFragment.ConfirmDialogFragmentListener() {
                                    @Override
                                    public void onPositiveButtonClicked() {
                                        ((MainActivity) getActivity()).performLogout();
                                    }
                                });
                    } else {
                        UiUtils.showConfirmDialog(getFragmentManager(),
                                getString(R.string.dialog_title_logout),
                                getString(R.string.dialog_msg_logout),
                                getString(R.string.dialog_btn_positive_logout),
                                new ConfirmDialogFragment.ConfirmDialogFragmentListener() {
                                    @Override
                                    public void onPositiveButtonClicked() {
                                        ((MainActivity) getActivity()).performLogout();
                                    }
                                });
                    }
                }
            }
        });

    }

    @Override
    public void navigateTo(Fragment fragment, String tag, boolean addToBackStack, View sharedView, String transitionName) {
        Fragment currentFragment = getChildFragmentManager().findFragmentById(R.id.f_main_fl);

        if (currentFragment.getClass() != fragment.getClass()) {
            String backStateName = fragment.getClass().getName();

            FragmentManager fm = getChildFragmentManager();
            boolean fragmentPopped = fm.popBackStackImmediate(backStateName, 0);

            if (!fragmentPopped) {
                FragmentTransaction transaction =
                        getChildFragmentManager()
                                .beginTransaction()
                                .replace(R.id.f_main_fl, fragment, tag);

                if (sharedView != null && transitionName != null) {
                    transaction.addSharedElement(sharedView, transitionName);
                }

                if (addToBackStack) {
                    transaction.addToBackStack(backStateName);
                }
                transaction.commit();
            }
        }
    }

    @Override
    public void navigateDefault() {
        getChildFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public boolean closeDrawerLayout() {
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }
}
