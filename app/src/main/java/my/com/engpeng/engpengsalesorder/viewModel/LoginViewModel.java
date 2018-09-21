package my.com.engpeng.engpengsalesorder.viewModel;

import android.arch.lifecycle.ViewModel;

import my.com.engpeng.engpengsalesorder.asyncTask.LoginAsyncTask;
import my.com.engpeng.engpengsalesorder.asyncTask.LoginRunnable;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;

public class LoginViewModel extends ViewModel {
    private LoginRunnable loginRunnable;

    public LoginViewModel(LoginRunnable loginRunnable) {
        this.loginRunnable = loginRunnable;
    }

    public void execute() {
        AppExecutors.getInstance().diskIO().execute(this.loginRunnable);
    }
}
