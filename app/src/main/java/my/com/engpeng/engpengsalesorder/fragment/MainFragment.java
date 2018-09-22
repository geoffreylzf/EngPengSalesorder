package my.com.engpeng.engpengsalesorder.fragment;

import android.content.Intent;
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
import my.com.engpeng.engpengsalesorder.activity.HouseKeepingActivity;
import my.com.engpeng.engpengsalesorder.activity.MainActivity;
import my.com.engpeng.engpengsalesorder.activity.NavigationHost;
import my.com.engpeng.engpengsalesorder.fragment.main.MainCompanyFragment;
import my.com.engpeng.engpengsalesorder.fragment.main.MainDashboardFragment;

import static my.com.engpeng.engpengsalesorder.Global.sUsername;

public class MainFragment extends Fragment implements NavigationHost {

    public static final String tag = "MAIN_FRAGMENT";

    private Toolbar tb;
    private DrawerLayout dl;
    private NavigationView nvStart;

    private TextView navStartTvUsername;
    private Bundle savedInstanceState;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        tb = rootView.findViewById(R.id.f_main_tb);
        dl = rootView.findViewById(R.id.main_dl);
        nvStart = rootView.findViewById(R.id.f_main_start_nv);

        getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.colorTransparent));

        this.savedInstanceState = savedInstanceState;

        setupDrawerLayout();
        setupDashboard();

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
        navStartTvUsername.setText(sUsername);

        nvStart.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.main_drawer_start_company) {
                    navigateTo(new MainCompanyFragment(), MainCompanyFragment.tag, true);
                } else if (id == R.id.main_drawer_start_house_keeping) {
                    startActivity(new Intent(getActivity(), HouseKeepingActivity.class));
                } else if (id == R.id.main_drawer_start_history) {
                    //TODO open history
                } else if (id == R.id.main_drawer_start_upload) {
                    //TODO open upload
                } else if (id == R.id.main_drawer_start_house_log) {
                    //TODO logout confirm
                    ((MainActivity) getActivity()).performLogout();
                }

                dl.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void setupDashboard() {
        if (savedInstanceState == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.f_main_fl, new MainDashboardFragment(), MainDashboardFragment.tag)
                    .commit();
        }
    }

    @Override
    public void navigateTo(Fragment fragment, String tag, boolean addToBackStack) {
        //TODO perform exist checking
        FragmentTransaction transaction =
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.f_main_fl, fragment, tag);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void clearAllNavigateTo(Fragment fragment, String tag) {
        getChildFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        navigateTo(fragment, tag, false);
    }

    @Override
    public void navigateDefault() {
        getChildFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        setupDashboard();
    }

    public boolean closeDrawerLayout() {
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }
}
