package my.com.engpeng.engpengsalesorder.animation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Interpolator;

import my.com.engpeng.engpengsalesorder.R;

public class BackdropMenuAnimation {

    public static void showBackdropMenu(Context context, View sheet, Interpolator interpolator, boolean backdropShown) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.removeAllListeners();
        animatorSet.end();
        animatorSet.cancel();

        final int translateY = height -
                context.getResources().getDimensionPixelSize(R.dimen.so_dashboard_reveal_height);

        ObjectAnimator animator = ObjectAnimator.ofFloat(sheet, "translationY", backdropShown ? translateY : 0);
        animator.setDuration(500);
        if (interpolator != null) {
            animator.setInterpolator(interpolator);
        }
        animatorSet.play(animator);
        animator.start();
    }


}
