package net.alexandroid.utils.toaster;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.ContentFrameLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class Toaster implements View.OnClickListener {

    private static final int TOAST_ANIM_DURATION = 800;
    private static final int TOAST_TIME_TO_SHOW = 700;
    public static final int DIALOG_ANIM_DURATION = 600;
    private static boolean sVisible;

    private String mTitle;
    private String mText;
    private String mPositive;
    private String mNegative;

    private WeakReference<Activity> mWeakActivity;
    private View mLayout;
    private WeakReference<DialogCallback> mCallback;

    private int mDefaultLayout = R.layout.custom_toast_dialog;

    // Toast
    public static void showToast(Context context, String msg) {
        showToast(context, msg, TOAST_ANIM_DURATION, TOAST_TIME_TO_SHOW);
    }

    public static void showToast(Context context, String msg, int animationDuration, int visibleDuration) {
        if (animationDuration < 0) {
            throw new IllegalArgumentException("Animation duration can't be negative");
        }
        if (visibleDuration < 100) {
            throw new IllegalArgumentException("Visible duration must be more then 99ms");
        }

        final View layout = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
        TextView text = layout.findViewById(R.id.text);
        text.setText(msg);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);

        final ContentFrameLayout contentFrameLayout = ((Activity) context).findViewById(android.R.id.content);
        contentFrameLayout.addView(layout);

        ObjectAnimator.ofFloat(layout, "alpha", 0f, 1f).setDuration(TOAST_ANIM_DURATION).start();

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(layout, "alpha", 1f, 0f);
        fadeOut.setStartDelay(animationDuration + visibleDuration);
        fadeOut.setDuration(animationDuration);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                contentFrameLayout.removeView(layout);
            }
        });
        fadeOut.start();
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
        btnPositive.setText(mPositive);
        btnNegative.setText(mNegative);

        mLayout.setOnClickListener(this);
        btnPositive.setOnClickListener(this);
        btnNegative.setOnClickListener(this);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mLayout.setLayoutParams(params);

        final ContentFrameLayout ContentFrameLayout = activity.findViewById(android.R.id.content);
        ContentFrameLayout.addView(mLayout);

        ObjectAnimator.ofFloat(mLayout, "alpha", 0f, 1f).setDuration(DIALOG_ANIM_DURATION).start();
    }

    public void hide() {
        Activity activity = mWeakActivity.get();
        if (activity == null) {
            return;
        }
        sVisible = false;
        final ContentFrameLayout contentFrameLayout = activity.findViewById(android.R.id.content);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mLayout, "alpha", 1f, 0f);
        fadeOut.setDuration(DIALOG_ANIM_DURATION);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                contentFrameLayout.removeView(mLayout);
            }
        });
        fadeOut.start();
    }

    @Override
    public void onClick(View view) {
        if (mCallback == null) {
            return;
        }
        int i = view.getId();
        if (i == R.id.btnPositive) {
            hide();
            if (mCallback.get() != null) {
                mCallback.get().onPositiveClick();
            }
        } else if (i == R.id.btnNegative || i == R.id.dialogLayout) {
            hide();
            if (mCallback.get() != null) {
                mCallback.get().onNegativeClick();
            }
        }
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

        public Toaster build() {
            return mToaster;
        }
    }

    public interface DialogCallback {
        void onPositiveClick();

        void onNegativeClick();
    }
}
