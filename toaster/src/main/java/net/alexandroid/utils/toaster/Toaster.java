package net.alexandroid.utils.toaster;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.support.v7.widget.ContentFrameLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Toaster implements View.OnClickListener {

    private View mLayout;
    private static boolean sVisible;

    private String mTitle;
    private String mText;
    private String mPositive;
    private String mNegative;

    private DialogCallback mCallback;

    private int mDefaultLayout = R.layout.custom_toast_dialog;

    public static void showToast(String msg, Activity activity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.custom_toast, null);
        TextView text = layout.findViewById(R.id.text);
        text.setText(msg);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);

        final ContentFrameLayout contentFrameLayout = activity.findViewById(android.R.id.content);
        contentFrameLayout.addView(layout);

        ObjectAnimator.ofFloat(layout, "alpha", 0f, 1f).setDuration(800).start();

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(layout, "alpha", 1f, 0f);
        fadeOut.setStartDelay(1500);
        fadeOut.setDuration(800);
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

    public void show(Activity activity) {
        if (sVisible) {
            return;
        }
        sVisible = true;

        LayoutInflater inflater = activity.getLayoutInflater();
        mLayout = inflater.inflate(mDefaultLayout, null);
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

        ObjectAnimator.ofFloat(mLayout, "alpha", 0f, 1f).setDuration(600).start();
    }

    public void hide(Activity activity) {
        sVisible = false;

        final ContentFrameLayout contentFrameLayout = activity.findViewById(android.R.id.content);

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mLayout, "alpha", 1f, 0f);
        fadeOut.setDuration(600);
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
            mCallback.onPositiveClick();
        } else if (i == R.id.btnNegative) {
            mCallback.onNegativeClick();
        } else if (i == R.id.dialogLayout) {
            mCallback.onNegativeClick();
        }
    }

    public boolean isVisible() {
        return sVisible;
    }

    public static class Builder {

        private final Toaster mToaster;

        public Builder() {
            mToaster = new Toaster();
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
            mToaster.mCallback = callback;
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
