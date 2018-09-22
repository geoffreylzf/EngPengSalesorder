package my.com.engpeng.engpengsalesorder.activity;

import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.animation.FabOpenAnimation;
import my.com.engpeng.engpengsalesorder.fragment.SoDashboardFragment;
import my.com.engpeng.engpengsalesorder.fragment.TempSoHeadFragment;

public class SalesorderActivity extends AppCompatActivity implements NavigationHost {

    private Toolbar tb;
    private AppBarLayout abl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesorder);

        tb = findViewById(R.id.salesorder_selection_tb);
        abl = findViewById(R.id.salesorder_selection_abl);

        setSupportActionBar(tb);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    //.add(R.id.temp_salesorder_fl, new TempSoHeadFragment())
                    .add(R.id.salesorder_fl, new SoDashboardFragment(), SoDashboardFragment.tag)
                    .commit();
        }
    }

    @Override
    public void navigateTo(Fragment fragment, String tag, boolean addToBackStack) {
        setAppBarLayoutElevation(4);
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.salesorder_fl, fragment, tag);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void clearAllNavigateTo(Fragment fragment, String tag) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        navigateTo(fragment, tag, false);
    }

    @Override
    public void navigateDefault() {
        //do nothing
    }

    public void setAppBarLayoutElevation(int dps) {
        if (dps == 0) {
            abl.setTargetElevation(0);
        } else {
            final float scale = this.getResources().getDisplayMetrics().density;
            int pixels = (int) (dps * scale + 0.5f);
            abl.setElevation(pixels);
        }
    }
}
