package com.ensharp.haxi.InternationalTaxi;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ensharp.haxi.R;

public class InternationalTaxiActivity extends AppCompatActivity {

    private Thread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_international_taxi);

        showProgressDeterminateDialog();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Info"));
        tabLayout.addTab(tabLayout.newTab().setText("Fare"));
        tabLayout.addTab(tabLayout.newTab().setText("Book"));
        tabLayout.addTab(tabLayout.newTab().setText("Confirm"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public void showProgressDeterminateDialog() {
        new MaterialDialog.Builder(this)
                .title("Loading...")
                .content("페이지를 로딩중입니다")
                .contentGravity(GravityEnum.CENTER)
                .progress(false, 100, true)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (mThread != null)
                            mThread.interrupt();
                    }
                })
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                        startThread(new Runnable() {
                            @Override
                            public void run() {
                                while (dialog.getCurrentProgress() != dialog.getMaxProgress() &&
                                        !Thread.currentThread().isInterrupted()) {
                                    if (dialog.isCancelled())
                                        break;
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {
                                        break;
                                    }
                                    dialog.incrementProgress(1);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mThread = null;
                                        dialog.dismiss();
                                    }
                                });

                            }
                        });
                    }
                }).show();
    }

    private void startThread(Runnable run) {
        if (mThread != null)
            mThread.interrupt();
        mThread = new Thread(run);
        mThread.start();
    }

}

