package my.com.engpeng.engpengsalesorder.viewModel;

import androidx.lifecycle.ViewModel;

import my.com.engpeng.engpengsalesorder.asyncTask.LoginRunnable;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;

public class LoginViewModel extends ViewModel {
    private LoginRunnable loginRunnable;

    public LoginViewModel(LoginRunnable loginRunnable) {
        this.loginRunnable = loginRunnable;
    }

    public void setUsername(String username) {
        loginRunnable.setUsername(username);
    }

    public void setPassword(String password) {
        loginRunnable.setPassword(password);
    }

    public void execute() {
        AppExecutors.getInstance().diskIO().execute(this.loginRunnable);
    }
}
