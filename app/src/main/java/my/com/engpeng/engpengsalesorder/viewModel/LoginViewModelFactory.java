package my.com.engpeng.engpengsalesorder.viewModel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

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
