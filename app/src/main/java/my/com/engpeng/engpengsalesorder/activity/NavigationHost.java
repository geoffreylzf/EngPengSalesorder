package my.com.engpeng.engpengsalesorder.activity;

import androidx.fragment.app.Fragment;
import android.view.View;

public interface NavigationHost {
    void navigateTo(Fragment fragment, String tag, boolean addToBackStack, View sharedView, String transitionName);
    void navigateDefault();
}
