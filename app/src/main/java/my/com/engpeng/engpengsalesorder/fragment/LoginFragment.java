package my.com.engpeng.engpengsalesorder.fragment;

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
import my.com.engpeng.engpengsalesorder.utilities.SharedPreferencesUtils;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;

import static my.com.engpeng.engpengsalesorder.Global.RC_GOOGLE_SIGN_IN;

public class LoginFragment extends Fragment {

    public static final String tag = "LOGIN_FRAGMENT";

    private TextInputLayout tilUsername, tilPassword;
    private EditText etUsername, etPassword;
    private Button btnLogin, btnCancel;

    private GoogleSignInClient googleSignInClient;
    private String signInEmail;
    private SignInButton sibtnGmail;

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
        sibtnGmail = rootView.findViewById(R.id.login_btn_sign_in_gmail);

        SharedPreferencesUtils.clearUsernamePassword(getContext());
        SharedPreferencesUtils.clearCompanyId(getContext());

        //TODO clear HK and History data
        //TODO setup version

        setupGoogleSignIn();
        setupListener();


        return rootView;
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        sibtnGmail.setOnClickListener(new View.OnClickListener() {
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
            signInEmail = account.getEmail();
            setGoogleSignInButtonText(sibtnGmail, account.getEmail() + " (Sign out)");
        } else {
            signInEmail = null;
            setGoogleSignInButtonText(sibtnGmail, "Sign in");
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

        if (signInEmail == null || signInEmail.isEmpty()) {
            UiUtils.showToastMessage(getContext(), "Please sign in google account before login");
            return;
        }
    }
}
