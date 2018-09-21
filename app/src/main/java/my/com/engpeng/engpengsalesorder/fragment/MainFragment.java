package my.com.engpeng.engpengsalesorder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.activity.HouseKeepingActivity;
import my.com.engpeng.engpengsalesorder.activity.SalesorderActivity;
import my.com.engpeng.engpengsalesorder.database.salesorder.SalesorderEntry;

import static my.com.engpeng.engpengsalesorder.Global.sUsername;

public class MainFragment extends Fragment {

    public static final String tag = "MAIN_FRAGMENT";

    private Toolbar tb;
    private DrawerLayout dl;
    private Button btnSo;
    private NavigationView nvStart;

    private TextView navStartTvUsername;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        tb = rootView.findViewById(R.id.main_tb);
        dl = rootView.findViewById(R.id.main_dl);
        btnSo = rootView.findViewById(R.id.main_btn_so);
        nvStart = rootView.findViewById(R.id.main_start_nv);

        getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.colorTransparent));
        getActivity().setTitle("Salesorder");

        setupDrawerLayout();
        setupListener();

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
                    //TODO open company
                } else if (id == R.id.main_drawer_start_house_keeping) {
                    startActivity(new Intent(getActivity(), HouseKeepingActivity.class));
                } else if (id == R.id.main_drawer_start_history) {
                    //TODO open history
                } else if (id == R.id.main_drawer_start_upload) {
                    //TODO open upload
                } else if (id == R.id.main_drawer_start_house_log) {
                    //TODO perform logout function
                }

                dl.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void setupListener() {
        btnSo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SalesorderActivity.class));
            }
        });
    }

    public boolean closeDrawerLayout(){
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }
}
