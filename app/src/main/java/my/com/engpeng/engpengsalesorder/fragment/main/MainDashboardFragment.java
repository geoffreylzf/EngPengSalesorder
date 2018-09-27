package my.com.engpeng.engpengsalesorder.fragment.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.activity.SalesorderActivity;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;
import my.com.engpeng.engpengsalesorder.fragment.ConfirmDialogFragment;
import my.com.engpeng.engpengsalesorder.fragment.MainFragment;
import my.com.engpeng.engpengsalesorder.utilities.SharedPreferencesUtils;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;

import static my.com.engpeng.engpengsalesorder.Global.PREF_KEY;
import static my.com.engpeng.engpengsalesorder.Global.P_KEY_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.sCompanyCode;
import static my.com.engpeng.engpengsalesorder.Global.sCompanyId;
import static my.com.engpeng.engpengsalesorder.Global.sCompanyName;
import static my.com.engpeng.engpengsalesorder.Global.sCompanyShortName;

public class MainDashboardFragment extends Fragment {

    public static final String tag = "MAIN_DASHBOARD_FRAGMENT";
    private Button btnSo;
    private TextView tvCompany, tvVersion;

    private AppDatabase mDb;
    private FragmentActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_m_dashboard, container, false);

        btnSo = rootView.findViewById(R.id.m_dashboard_btn_so);
        tvCompany = rootView.findViewById(R.id.m_dashboard_tv_company);
        tvVersion = rootView.findViewById(R.id.m_dashboard_tv_version);

        setupVersion();
        setupListener();

        activity = getActivity();
        mDb = AppDatabase.getInstance(activity.getApplicationContext());
        getActivity().setTitle(getString(R.string.app_name));


        setupSharedPreferences();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setupVersion() {
        tvVersion.setText(getString(R.string.version).concat(StringUtils.getAppVersion(getContext())));
    }

    public void setupListener() {
        btnSo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sCompanyId != 0) {
                    startActivity(new Intent(activity, SalesorderActivity.class));
                } else {
                    UiUtils.showConfirmDialog(getFragmentManager(),
                            getString(R.string.dialog_title_select_company),
                            getString(R.string.dialog_msg_select_company),
                            getString(R.string.dialog_btn_positive_select_company),
                            new ConfirmDialogFragment.ConfirmDialogFragmentListener() {
                                @Override
                                public void onPositiveButtonClicked() {
                                    ((MainFragment) getParentFragment()).navigateTo(new CompanyFragment(), CompanyFragment.tag, true, null, null);
                                }
                            });
                }
            }
        });
    }

    private void setupSharedPreferences() {
        retrieveCompany();
        SharedPreferences sharedPreferences = SharedPreferencesUtils.getSharedPreferences(activity);
        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(P_KEY_COMPANY_ID)) {
                    retrieveCompany();
                }
            }
        });
    }

    private void retrieveCompany() {
        sCompanyId = SharedPreferencesUtils.getCompanyId(activity);
        if (sCompanyId != 0) {
            final LiveData<BranchEntry> ld = mDb.branchDao().loadLiveBranchById(sCompanyId);
            ld.observe(this, new Observer<BranchEntry>() {
                @Override
                public void onChanged(@Nullable BranchEntry branchEntry) {
                    ld.removeObserver(this);
                    if (branchEntry != null) {
                        sCompanyId = branchEntry.getId();
                        sCompanyCode = branchEntry.getBranchCode();
                        sCompanyName = branchEntry.getBranchName();
                        sCompanyShortName = branchEntry.getBranchShortName();
                        populateUi();
                    } else {
                        clearGlobalCompany();
                    }
                }
            });
        } else {
            clearGlobalCompany();
        }
    }

    private void clearGlobalCompany() {
        sCompanyId = 0;
        sCompanyCode = "Non";
        sCompanyName = "No Company Selected";
        sCompanyShortName = "No Company Selected";
        populateUi();
    }

    private void populateUi() {
        tvCompany.setText(sCompanyName);
    }

}
