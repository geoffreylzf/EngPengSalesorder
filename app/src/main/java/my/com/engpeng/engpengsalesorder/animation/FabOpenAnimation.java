package my.com.engpeng.engpengsalesorder.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewAnimationUtils;

public class FabOpenAnimation {

    public interface AnimationFinishedListener {
        void onAnimationFinished();
    }

    public interface Dismissible {
        interface OnDismissedListener {
            void onDismissed();
        }
        void dismiss(OnDismissedListener listener);
    }

    public static void registerCircularRevealAnimation(final Context context,
                                                       final View view,
                                                       final RevealAnimationSetting revealSettings,
                                                       final int startColor,
                                                       final int endColor,
                                                       final AnimationFinishedListener listener) {

        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                int cx = revealSettings.getCenterX();
                int cy = revealSettings.getCenterY();
                int width = revealSettings.getWidth();
                int height = revealSettings.getHeight();
                int duration = context.getResources().getInteger(android.R.integer.config_mediumAnimTime);

                //Simply use the diagonal of the view
                float finalRadius = (float) Math.sqrt(width * width + height * height);
                Animator anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius).setDuration(duration);
                anim.setInterpolator(new FastOutSlowInInterpolator());
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        listener.onAnimationFinished();
                    }
                });
                anim.start();
                startColorAnimation(view, startColor, endColor, duration);
            }
        });

    }

    public static void startCircularExitAnimation(final Context context,
                                                  final View view,
                                                  final RevealAnimationSetting revealSettings,
                                                  final int startColor,
                                                  final int endColor,
                                                  final AnimationFinishedListener listener) {
        int cx = revealSettings.getCenterX();
        int cy = revealSettings.getCenterY();
        int width = revealSettings.getWidth();
        int height = revealSettings.getHeight();
        int duration = context.getResources().getInteger(android.R.integer.config_mediumAnimTime);

        float initRadius = (float) Math.sqrt(width * width + height * height);
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initRadius, 0);
        anim.setDuration(duration);
        anim.setInterpolator(new FastOutSlowInInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //view.setVisibility(View.GONE);
                listener.onAnimationFinished();
            }
        });
        anim.start();
        startColorAnimation(view, startColor, endColor, duration);
    }

    static void startColorAnimation(final View view, final int startColor, final int endColor, int duration) {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(startColor, endColor);
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });
        anim.setDuration(duration);
        anim.start();
    }


}
