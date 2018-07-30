package com.bitsescrow.app.bitsescrow.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.LruCache;
import android.view.View;
import android.widget.TextView;

import com.bitsescrow.app.bitsescrow.R;
import com.bitsescrow.app.bitsescrow.models.User;
import com.bitsescrow.app.bitsescrow.ui.base.BaseActivity;
import com.bitsescrow.app.bitsescrow.ui.fragments.AccountFragment;
import com.bitsescrow.app.bitsescrow.ui.fragments.MainFragment;
import com.bitsescrow.app.bitsescrow.ui.fragments.UserEscrowsFragment;
import com.bitsescrow.app.bitsescrow.ui.fragments.WalletFragment;
import com.bitsescrow.app.bitsescrow.utils.L;

import net.steamcrafted.materialiconlib.MaterialIconView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Lekan Adigun on 5/16/2018.
 */

public class MainActivity extends BaseActivity {


    @BindView(R.id.iv_wallet)
    MaterialIconView walletIconView;
    @BindView(R.id.iv_account)
    MaterialIconView accountIconView;
    @BindView(R.id.iv_escrow)
    MaterialIconView escrowIconView;
    @BindView(R.id.tv_escrow)
    TextView escrowTextView;
    @BindView(R.id.tv_account)
    TextView accountTextView;
    @BindView(R.id.tv_wallet)
    TextView walletTextView;

    private LruCache<String, Fragment> mFragmentLruCache = new LruCache<>(3);

    private User mAccount;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_main);

        select(escrowIconView, escrowTextView, walletIconView, walletTextView, accountIconView, accountTextView);
        swap(UserEscrowsFragment.newInstance(), MainFragment.TAG);

        mAccount = User.user();
        L.fine(mAccount.json());
    }

    private void swap(Fragment mainFragment, String tag) {

        Fragment fragment = mFragmentLruCache.get(tag);
        if (fragment == null) {
            fragment = mainFragment;
            mFragmentLruCache.put(tag, fragment);
        }

        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_cointainer_main_activity,
                fragment, tag);
        fragmentTransaction.commit();
    }

    @OnClick({R.id.tab_account, R.id.tab_wallet, R.id.tab_escrow}) public void onInteractWithTabs(View view) {

        switch (view.getId()) {
            case R.id.tab_account:
                swap(AccountFragment.newInstance(), AccountFragment.TAG);
                select(accountIconView, accountTextView, walletIconView, walletTextView, escrowIconView, escrowTextView);
                break;
            case R.id.tab_escrow:
                swap(UserEscrowsFragment.newInstance(), MainFragment.TAG);
                select(escrowIconView, escrowTextView, walletIconView, walletTextView, accountIconView, accountTextView);
                break;
            case R.id.tab_wallet:
                swap(WalletFragment.newInstance(), WalletFragment.TAG);
                select(walletIconView, walletTextView, accountTextView, accountIconView, escrowIconView, escrowTextView);
                break;
        }
    }

    private void select(MaterialIconView to, TextView textView, View... others) {

        to.setAlpha(0.9f);
        to.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));

        for (View view : others) {

            if (view instanceof MaterialIconView) {

                MaterialIconView iconView = (MaterialIconView) view;
                iconView.setColorFilter(ContextCompat.getColor(this, R.color.textColorPrimary));
                iconView.setAlpha(0.5f);

            }else {
                TextView tv = (TextView) view;
                tv.setTextColor(ContextCompat.getColor(this, R.color.textColorSecondary));
            }

        }
    }
}
