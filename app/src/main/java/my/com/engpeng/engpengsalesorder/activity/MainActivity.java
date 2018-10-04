package my.com.engpeng.engpengsalesorder.activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.fragment.dialog.ConfirmDialogFragment;
import my.com.engpeng.engpengsalesorder.fragment.LoginFragment;
import my.com.engpeng.engpengsalesorder.fragment.MainFragment;
import my.com.engpeng.engpengsalesorder.fragment.main.MainDashboardFragment;
import my.com.engpeng.engpengsalesorder.model.User;
import my.com.engpeng.engpengsalesorder.utilities.ScheduleUtils;
import my.com.engpeng.engpengsalesorder.utilities.SharedPreferencesUtils;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;

import static my.com.engpeng.engpengsalesorder.Global.*;

public class MainActivity extends AppCompatActivity {

    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.savedInstanceState = savedInstanceState;
        setupGlobalVariables();
    }

    private void setupGlobalVariables() {

        User user = SharedPreferencesUtils.getUsernamePassword(this);
        if (user == null) {
            openLoginFragment(false);
        } else {
            sUsername = user.getUsername();
            sPassword = user.getPassword();
            sUniqueId = SharedPreferencesUtils.getUniqueId(this);
            ScheduleUtils.scheduleAutoUpdate(this);
            ScheduleUtils.scheduleAutoUpload(this);
            openMainFragment();
        }
    }

    private void openMainFragment() {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_fl, new MainFragment(), MainFragment.tag)
                    .commit();
        }
    }

    private void openLoginFragment(boolean isLogout) {
        if (isLogout || savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_fl, new LoginFragment(), LoginFragment.tag)
                    .commit();
        }
    }

    public void performSuccessLogin() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(getSupportFragmentManager().findFragmentByTag(LoginFragment.tag));
        fragmentTransaction.commit();
        setupGlobalVariables();
    }

    public void performLogout() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(getSupportFragmentManager().findFragmentByTag(MainFragment.tag));
        fragmentTransaction.commit();
        openLoginFragment(true);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fl);
        if (fragment != null) {
            if (fragment instanceof LoginFragment) {
                super.onBackPressed();
            } else if (fragment instanceof MainFragment) {
                if (!((MainFragment) fragment).closeDrawerLayout()) {
                    Fragment fmChild = fragment.getChildFragmentManager().findFragmentById(R.id.f_main_fl);
                    if (fmChild instanceof MainDashboardFragment) {
                        exitConfirmation();
                    } else {
                        fmChild.getFragmentManager().popBackStack();
                    }
                }
            } else {
                exitConfirmation();
            }
        } else {
            exitConfirmation();
        }
    }

    private void exitConfirmation() {
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
