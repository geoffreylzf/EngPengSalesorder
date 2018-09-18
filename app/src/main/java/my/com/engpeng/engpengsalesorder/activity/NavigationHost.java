package my.com.engpeng.engpengsalesorder.activity;

import android.support.v4.app.Fragment;

public interface NavigationHost {
    void navigateTo(Fragment fragment, String tag, boolean addToBackStack);
    void clearAllNavigateTo(Fragment fragment, String tag);
}
