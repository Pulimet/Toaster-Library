package net.alexandroid.utils.toasterapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import net.alexandroid.utils.toaster.Toaster;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, Toaster.DialogCallback {

    private Toaster mToaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.btnShowToast).setOnClickListener(this);
        findViewById(R.id.btnShowDialog).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShowToast:
                showToast();
                break;
            case R.id.btnShowDialog:
                showDialog();
                break;
        }
    }

    private void showToast() {
        Toaster.showToast(this, "This is our awesome toast", 300, 500);
    }

    private void showDialog() {
        mToaster = new Toaster.Builder(this)
                .setTitle("Dialog title")
                .setText("Text of the dialog here")
                .setPositive("OK")
                .setNegative("CANCEL")
                .setAnimationDuration(300)
                .setCallBack(this).build();
        mToaster.show();
    }

    @Override
    public void onPositiveClick() {
        Log.d("TAG", "Positive");
    }

    @Override
    public void onNegativeClick() {
        Log.d("TAG", "Negative");
    }

    @Override
    public void onBackPressed() {
        if (mToaster == null || mToaster.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
