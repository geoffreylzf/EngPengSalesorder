package my.com.engpeng.engpengsalesorder.activity;

import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.animation.FabOpenAnimation;
import my.com.engpeng.engpengsalesorder.fragment.sales.SoDashboardFragment;
import my.com.engpeng.engpengsalesorder.fragment.sales.TempSoCartFragment;
import my.com.engpeng.engpengsalesorder.fragment.sales.TempSoConfirmFragment;
import my.com.engpeng.engpengsalesorder.fragment.sales.TempSoHeadFragment;

public class SalesorderActivity extends AppCompatActivity implements NavigationHost {

    private Toolbar tb;
    private AppBarLayout abl;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesorder);

        tb = findViewById(R.id.salesorder_tb);
        abl = findViewById(R.id.salesorder_abl);
        this.savedInstanceState = savedInstanceState;

        setSupportActionBar(tb);
        setupSoDashBoard();
    }

    private void setupSoDashBoard(){
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.salesorder_fl, new SoDashboardFragment(), SoDashboardFragment.tag)
                    .commit();
        }
    }

    @Override
    public void navigateTo(Fragment fragment, String tag, boolean addToBackStack, View sharedView, String transitionName) {
        setAppBarLayoutElevation(4);
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction();

        if (sharedView != null && transitionName != null) {
            transaction.addSharedElement(sharedView, transitionName);
        }

        if (fragment instanceof TempSoCartFragment || fragment instanceof TempSoConfirmFragment) {
            transaction.setCustomAnimations(
                    R.animator.fragment_slide_in_left,
                    R.animator.fragment_slide_out_right,
                    R.animator.fragment_slide_in_left_pop,
                    R.animator.fragment_slide_out_right_pop
            );
        }

        transaction.replace(R.id.salesorder_fl, fragment, tag);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void navigateDefault() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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

    @Override
    public void onBackPressed() {
        final Fragment showFragment = getSupportFragmentManager().findFragmentById(R.id.salesorder_fl);
        if(showFragment instanceof TempSoHeadFragment){
            ((FabOpenAnimation.Dismissible) showFragment).dismiss(new FabOpenAnimation.Dismissible.OnDismissedListener() {
                @Override
                public void onDismissed() {
                    getSupportFragmentManager().beginTransaction().remove(showFragment).commit();
                    getSupportFragmentManager().executePendingTransactions();
                    getSupportFragmentManager().popBackStack();
                }
            });
        }else{
            super.onBackPressed();
        }


    }
}
