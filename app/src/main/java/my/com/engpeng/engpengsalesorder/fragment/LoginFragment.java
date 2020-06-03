package my.com.engpeng.engpengsalesorder.fragment;

import android.app.Dialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.activity.MainActivity;
import my.com.engpeng.engpengsalesorder.asyncTask.LoginRunnable;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;
import my.com.engpeng.engpengsalesorder.model.Status;
import my.com.engpeng.engpengsalesorder.service.DownloadHistoryService;
import my.com.engpeng.engpengsalesorder.service.UpdateHouseKeepingService;
import my.com.engpeng.engpengsalesorder.utilities.JsonUtils;
import my.com.engpeng.engpengsalesorder.utilities.NetworkUtils;
import my.com.engpeng.engpengsalesorder.utilities.SharedPreferencesUtils;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;
import my.com.engpeng.engpengsalesorder.viewModel.LoginViewModel;
import my.com.engpeng.engpengsalesorder.viewModel.LoginViewModelFactory;

import static my.com.engpeng.engpengsalesorder.Global.ACTION_GET_ALL_TABLE;
import static my.com.engpeng.engpengsalesorder.Global.ACTION_REFRESH;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_ACTION;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_LOCAL;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_TABLE;
import static my.com.engpeng.engpengsalesorder.Global.RC_GOOGLE_SIGN_IN;

public class LoginFragment extends Fragment {

    public static final String tag = "LOGIN_FRAGMENT";

    private TextInputLayout tilUsername, tilPassword;
    private EditText etUsername, etPassword;
    private Button btnLogin, btnCancel;
    private CheckBox cbLocal;
    private TextView tvVersion;

    private Dialog dlProgress;
    private AppDatabase mDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        tilUsername = rootView.findViewById(R.id.login_til_username);
        tilPassword = rootView.findViewById(R.id.login_til_password);
        etUsername = rootView.findViewById(R.id.login_et_username);
        etPassword = rootView.findViewById(R.id.login_et_password);
        cbLocal = rootView.findViewById(R.id.login_cb_local);
        btnLogin = rootView.findViewById(R.id.login_btn_login);
        btnCancel = rootView.findViewById(R.id.login_btn_cancel);
        tvVersion =  rootView.findViewById(R.id.login_tv_version);

        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());

        SharedPreferencesUtils.clearUsernamePassword(getContext());
        SharedPreferencesUtils.clearUniqueId(getContext());
        SharedPreferencesUtils.clearCompanyId(getContext());

        dlProgress = UiUtils.getProgressDialog(getContext());

        nukeAllData();
        setupVersion();

        setupListener();

        return rootView;
    }

    private void setupVersion(){
        tvVersion.setText(getString(R.string.version).concat(StringUtils.getAppVersion(getContext())));
    }

    private void nukeAllData(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.salesorderDao().deleteAll();
                mDb.salesorderDetailDao().deleteAll();
                mDb.tableInfoDao().deleteAll();
                mDb.branchDao().deleteAll();
                mDb.customerCompanyDao().deleteAll();
                mDb.customerCompanyAddressDao().deleteAll();
                mDb.itemPackingDao().deleteAll();
                mDb.itemCompanyDao().deleteAll();
                mDb.priceSettingDao().deleteAll();
                mDb.logDao().deleteAll();
            }
        });
    }

    private void setupListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void attemptLogin() {
        tilUsername.setError(null);
        tilPassword.setError(null);
        if (etUsername.getText().length() == 0) {
            tilUsername.setError(getString(R.string.error_field_required));
            etUsername.requestFocus();
            return;
        }
        if (etPassword.getText().length() == 0) {
            tilPassword.setError(getString(R.string.error_field_required));
            etPassword.requestFocus();
            return;
        }

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String data = NetworkUtils.buildParam("data", "");

        LoginRunnable loginRunnable = new LoginRunnable(getActivity(), username, password, data, cbLocal.isChecked(), new LoginRunnable.LoginRunnableListener() {
            @Override
            public void onStart() {
                dlProgress.show();
            }

            @Override
            public void onResult(String json, String username, String password) {
                dlProgress.hide();
                if (json != null && !json.equals("")) {
                    Status status = JsonUtils.getAuthentication(json);
                    if (status.isSuccess()) {
                        Status loginStatus = JsonUtils.getLoginAuthentication(json);
                        if (loginStatus.isSuccess()) {
                            SharedPreferencesUtils.saveUsernamePassword(getContext(), username, password);
                            SharedPreferencesUtils.generateSaveUniqueId(getContext());

                            Intent intentHistory = new Intent(getActivity(), DownloadHistoryService.class);
                            intentHistory.putExtra(I_KEY_LOCAL, cbLocal.isChecked());
                            getActivity().stopService(intentHistory);
                            getActivity().startService(intentHistory);

                            Intent intentHk = new Intent(getActivity(), UpdateHouseKeepingService.class);
                            intentHk.putExtra(I_KEY_TABLE, ACTION_GET_ALL_TABLE);
                            intentHk.putExtra(I_KEY_ACTION, ACTION_REFRESH);
                            intentHk.putExtra(I_KEY_LOCAL, cbLocal.isChecked());
                            getActivity().stopService(intentHk);
                            getActivity().startService(intentHk);

                            ((MainActivity) getActivity()).performSuccessLogin();
                        } else {
                            UiUtils.showToastMessage(getContext(), loginStatus.getMessage());
                        }
                    } else {
                        UiUtils.showToastMessage(getContext(), status.getMessage());
                    }
                } else {
                    UiUtils.showToastMessage(getContext(), getString(R.string.msg_login_fail));
                }
            }
        });

        LoginViewModelFactory factory = new LoginViewModelFactory(loginRunnable);
        LoginViewModel loginViewModel = ViewModelProviders.of(this, factory).get(LoginViewModel.class);
        loginViewModel.setUsername(username);
        loginViewModel.setPassword(password);
        loginViewModel.execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dlProgress.dismiss();
    }
}
