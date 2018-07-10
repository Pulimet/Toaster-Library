package net.alexandroid.utils.toaster;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.ContentFrameLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class Toaster implements
        View.OnClickListener,
        View.OnTouchListener {

    private static final int TOAST_ANIM_DURATION = 600;
    private static final int TOAST_TIME_TO_SHOW = 700;
    public static final int DIALOG_ANIM_DURATION = 500;
    private static boolean sVisible;

    private String mTitle;
    private String mText;
    private String mPositive;
    private String mNegative;

    private WeakReference<Activity> mWeakActivity;
    private View mLayout;
    private WeakReference<DialogCallback> mCallback;

    private int mDefaultLayout = R.layout.custom_toast_dialog;
    private int mAnimationDuration;

    // Toast
    public static void showToast(Context context, String msg) {
        showToast(context, msg, TOAST_ANIM_DURATION, TOAST_TIME_TO_SHOW);
    }

    public static void showToast(Context context, String msg, int animationDuration, int visibleDuration) {
        showToast(context, msg, animationDuration, visibleDuration, R.layout.custom_toast);
    }

    public static void showToast(Context context, String msg, int animationDuration, int visibleDuration, int layoutRes) {
        final View layout = LayoutInflater.from(context).inflate(layoutRes, null);
        if (animationDuration < 0) {
            throw new IllegalArgumentException("Animation duration can't be negative");
        }
        if (visibleDuration < 100) {
            throw new IllegalArgumentException("Visible duration must be more then 99ms");
        }

        TextView text = layout.findViewById(R.id.text);
        text.setText(msg);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);

        final ContentFrameLayout contentFrameLayout = ((Activity) context).findViewById(android.R.id.content);
        contentFrameLayout.addView(layout);

        float systemAnimationsDuration = getSystemAnimationsDuration(context); // 0.0 => 1.0
        int animDuration = Math.round(animationDuration * systemAnimationsDuration);

        ObjectAnimator.ofFloat(layout, "alpha", 0f, 1f)
                .setDuration(animDuration)
                .start();

        if (systemAnimationsDuration > 0) {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(layout, "alpha", 1f, 0f);
            fadeOut.setStartDelay(animDuration + visibleDuration);
            fadeOut.setDuration(animDuration);
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    contentFrameLayout.removeView(layout);
                }
            });
            fadeOut.start();
        } else {
            layout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    contentFrameLayout.removeView(layout);
                }
            }, visibleDuration);
        }
    }

    private static float getSystemAnimationsDuration(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getFloat(
                    context.getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 0);
        } else {
            return Settings.System.getFloat(
                    context.getContentResolver(), Settings.System.ANIMATOR_DURATION_SCALE, 0);
        }
    }

    // Dialog
    private Toaster() {

    }

    public void show() {
        Activity activity = mWeakActivity.get();
        if (sVisible || activity == null) {
            return;
        }

        sVisible = true;

        mLayout = LayoutInflater.from(activity).inflate(mDefaultLayout, null);
        Button btnPositive = mLayout.findViewById(R.id.btnPositive);
        Button btnNegative = mLayout.findViewById(R.id.btnNegative);

        ((TextView) mLayout.findViewById(R.id.title)).setText(mTitle);
        ((TextView) mLayout.findViewById(R.id.text)).setText(mText);
        if (mPositive != null) {
            btnPositive.setVisibility(View.VISIBLE);
            btnPositive.setText(mPositive);
        }
        if (mNegative != null) {
            btnNegative.setVisibility(View.VISIBLE);
            btnNegative.setText(mNegative);
        }

        mLayout.findViewById(R.id.cardView).setOnTouchListener(this);
        mLayout.setOnTouchListener(this);
        btnPositive.setOnClickListener(this);
        btnNegative.setOnClickListener(this);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mLayout.setLayoutParams(params);

        final ContentFrameLayout ContentFrameLayout = activity.findViewById(android.R.id.content);
        ContentFrameLayout.addView(mLayout);

        ObjectAnimator.ofFloat(mLayout, "alpha", 0f, 1f).setDuration(mAnimationDuration).start();
    }

    public void hide() {
        Activity activity = mWeakActivity.get();
        if (activity == null) {
            return;
        }
        sVisible = false;
        final ContentFrameLayout contentFrameLayout = activity.findViewById(android.R.id.content);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mLayout, "alpha", 1f, 0f);
        fadeOut.setDuration(mAnimationDuration);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                contentFrameLayout.removeView(mLayout);
            }
        });
        fadeOut.start();
    }

    public static boolean isVisible() {
        return sVisible;
    }

    @Override
    public void onClick(View view) {
        if (mCallback == null) {
            return;
        }
        int id = view.getId();
        if (id == R.id.btnPositive) {
            hide();
            if (mCallback.get() != null) {
                mCallback.get().onPositiveClick();
            }
        } else if (id == R.id.btnNegative) {
            hide();
            if (mCallback.get() != null) {
                mCallback.get().onNegativeClick();
            }
        }
        mCallback = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mCallback == null) {
            return true;
        }

        int id = v.getId();
        if (id == R.id.cardView) {
            return true;
        } else if (id == R.id.dialogLayout) {
            mCallback.get().onOutOfTheBoundClick();
            mCallback = null;
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            v.performClick();
        }
        return false;
    }

    public boolean onBackPressed() {
        if (sVisible) {
            hide();
            return false;
        } else {
            return true;
        }
    }

    public static class Builder {

        private final Toaster mToaster;

        public Builder(Activity activity) {
            mToaster = new Toaster();
            mToaster.mWeakActivity = new WeakReference<>(activity);
            mToaster.mAnimationDuration = DIALOG_ANIM_DURATION;
        }

        public Builder setTitle(String title) {
            mToaster.mTitle = title;
            return this;
        }

        public Builder setText(String text) {
            mToaster.mText = text;
            return this;
        }

        public Builder setPositive(String positive) {
            mToaster.mPositive = positive;
            return this;
        }

        public Builder setNegative(String negative) {
            mToaster.mNegative = negative;
            return this;
        }

        public Builder setCallBack(DialogCallback callback) {
            mToaster.mCallback = new WeakReference<>(callback);
            return this;
        }

        public Builder setCustomLayout(int customLayout) {
            mToaster.mDefaultLayout = customLayout;
            return this;
        }

        public Builder setAnimationDuration(int animationDuration) {
            mToaster.mAnimationDuration = animationDuration;
            return this;
        }

        public Toaster build() {
            return mToaster;
        }
    }

    public interface DialogCallback {
        void onPositiveClick();

        void onNegativeClick();

        void onOutOfTheBoundClick();
    }
}
