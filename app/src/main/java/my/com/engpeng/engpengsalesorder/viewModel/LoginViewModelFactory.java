package my.com.engpeng.engpengsalesorder.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import my.com.engpeng.engpengsalesorder.asyncTask.LoginAsyncTask;
import my.com.engpeng.engpengsalesorder.asyncTask.LoginRunnable;

public class LoginViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private LoginRunnable loginRunnable;

    public LoginViewModelFactory(LoginRunnable loginRunnable) {
        this.loginRunnable = loginRunnable;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LoginViewModel(loginRunnable);
    }
}
