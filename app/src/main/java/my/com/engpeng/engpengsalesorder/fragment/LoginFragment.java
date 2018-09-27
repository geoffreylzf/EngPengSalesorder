package my.com.engpeng.engpengsalesorder.fragment;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private TextView tvVersion;

    private GoogleSignInClient googleSignInClient;
    private String email;
    private SignInButton sibtnEmail;

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
        btnLogin = rootView.findViewById(R.id.login_btn_login);
        btnCancel = rootView.findViewById(R.id.login_btn_cancel);
        sibtnEmail = rootView.findViewById(R.id.login_btn_sign_in_gmail);
        tvVersion =  rootView.findViewById(R.id.login_tv_version);

        getActivity().getWindow().setStatusBarColor(UiUtils.getPrimaryDarkColorId(getContext()));
        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());

        SharedPreferencesUtils.clearUsernamePassword(getContext());
        SharedPreferencesUtils.clearUniqueId(getContext());
        SharedPreferencesUtils.clearCompanyId(getContext());

        dlProgress = UiUtils.getProgressDialog(getContext());

        nukeAllData();
        setupVersion();

        setupGoogleSignIn();
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
                mDb.priceSettingDao().deleteAll();
            }
        });
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        sibtnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
                if (account != null) {
                    googleSignInClient.signOut();
                    populateUi(GoogleSignIn.getLastSignedInAccount(getContext()));
                    UiUtils.showToastMessage(getContext(), "Sign out from google account");
                } else {
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                populateUi(account);
            } catch (ApiException e) {
                if (e.getStatusCode() != 12501) {
                    UiUtils.showToastMessage(getContext(), "Error : " + e.toString());
                }
                populateUi(null);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        populateUi(account);
    }

    private void populateUi(GoogleSignInAccount account) {
        if (account != null) {
            email = account.getEmail();
            setGoogleSignInButtonText(sibtnEmail, account.getEmail() + " (Sign out)");
        } else {
            email = null;
            setGoogleSignInButtonText(sibtnEmail, "Sign in");
        }
    }

    private void setGoogleSignInButtonText(SignInButton signInButton, String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
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

        if (email == null || email.isEmpty()) {
            UiUtils.showToastMessage(getContext(), "Please sign in google account before login");
            return;
        }

        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();
        String data = NetworkUtils.buildParam(NetworkUtils.PARAM_EMAIL, email);

        LoginRunnable loginRunnable = new LoginRunnable(getActivity(), username, password, data, false, new LoginRunnable.LoginRunnableListener() {
            @Override
            public void onStart() {
                dlProgress.show();
            }

            @Override
            public void onResult(String json) {
                dlProgress.hide();
                if (json != null && !json.equals("")) {
                    Status status = JsonUtils.getAuthentication(json);
                    if (status.isSuccess()) {
                        Status loginStatus = JsonUtils.getLoginAuthentication(json);
                        if (loginStatus.isSuccess()) {
                            SharedPreferencesUtils.saveUsernamePassword(getContext(), username, password);
                            SharedPreferencesUtils.generateSaveUniqueId(getContext());

                            Intent intentHistory = new Intent(getActivity(), DownloadHistoryService.class);
                            intentHistory.putExtra(I_KEY_LOCAL, false);
                            getActivity().stopService(intentHistory);
                            getActivity().startService(intentHistory);

                            Intent intentHk = new Intent(getActivity(), UpdateHouseKeepingService.class);
                            intentHk.putExtra(I_KEY_TABLE, ACTION_GET_ALL_TABLE);
                            intentHk.putExtra(I_KEY_ACTION, ACTION_REFRESH);
                            intentHk.putExtra(I_KEY_LOCAL, false);
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
        loginViewModel.execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dlProgress.dismiss();
    }
}
