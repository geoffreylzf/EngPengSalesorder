package my.com.engpeng.engpengsalesorder.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;
import my.com.engpeng.engpengsalesorder.fragment.ConfirmDialogFragment;
import my.com.engpeng.engpengsalesorder.fragment.MainFragment;
import my.com.engpeng.engpengsalesorder.model.User;
import my.com.engpeng.engpengsalesorder.utilities.ScheduleUtils;
import my.com.engpeng.engpengsalesorder.utilities.SharedPreferencesUtils;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;

import static my.com.engpeng.engpengsalesorder.Global.*;

public class MainActivity extends AppCompatActivity {

    private AppDatabase mDb;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getWindow().setStatusBarColor(getColor(R.color.colorTransparent));
        mDb = AppDatabase.getInstance(getApplicationContext());

        this.savedInstanceState = savedInstanceState;
        openMainFragment();
        //setupGlobalVariables();
    }

    private void setupGlobalVariables() {

        User user = SharedPreferencesUtils.getUsernamePassword(this);
        if (user == null) {
            //TODO open login fragment
            Log.e("MAIN", "open login fragment");
        } else {
            sUsername = user.getUsername();
            sPassword = user.getPassword();

            sCompanyId = SharedPreferencesUtils.getCompanyId(this);
            if (sCompanyId != 0) {
                retrieveCompany(sCompanyId);
            } else {
                clearGlobalCompany();
            }

            ScheduleUtils.scheduleAutoUpdate(this);
            openMainFragment();
        }
    }

    private void retrieveCompany(long companyId) {
        final LiveData<BranchEntry> ld = mDb.branchDao().loadLiveBranchById(companyId);
        ld.observe(this, new Observer<BranchEntry>() {
            @Override
            public void onChanged(@Nullable BranchEntry branchEntry) {
                ld.removeObserver(this);
                if (branchEntry != null) {
                    sCompanyId = branchEntry.getId();
                    sCompanyCode = branchEntry.getBranchCode();
                    sCompanyName = branchEntry.getBranchName();
                    sCompanyShortName = branchEntry.getBranchShortName();
                } else {
                    clearGlobalCompany();
                }
            }
        });
    }

    private void clearGlobalCompany() {
        sCompanyId = 0;
        sCompanyCode = "Non";
        sCompanyName = "No Company Selected";
        sCompanyShortName = "Non";
    }

    private void openMainFragment() {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_fl, new MainFragment(), MainFragment.tag)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fl);
        if(fragment != null){
            if(fragment instanceof MainFragment){
                if(!((MainFragment)fragment).closeDrawerLayout()){
                    exitConfirmation();
                }
            }else{
                exitConfirmation();
            }
        }else{
            exitConfirmation();
        }
    }

    private void exitConfirmation(){
        UiUtils.showConfirmDialog(getSupportFragmentManager(),
                getString(R.string.dialog_title_exit_app),
                getString(R.string.dialog_msg_exit_app),
                getString(R.string.dialog_btn_positive_exit_app),
                new ConfirmDialogFragment.ConfirmDialogFragmentListener() {
                    @Override
                    public void onPositiveButtonClicked() {
                        MainActivity.super.onBackPressed();
                    }
                });
    }
}
