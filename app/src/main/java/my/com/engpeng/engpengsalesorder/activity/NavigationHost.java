package my.com.engpeng.engpengsalesorder.activity;

import android.support.v4.app.Fragment;
import android.view.View;

public interface NavigationHost {
    void navigateTo(Fragment fragment, String tag, boolean addToBackStack, View sharedView, String transitionName);
    void clearAllNavigateTo(Fragment fragment, String tag);
    void navigateDefault();
}
