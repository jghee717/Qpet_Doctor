package com.ccit19.merdog_doctor.ui.dashboard.taps;

import android.graphics.drawable.Drawable;

import com.ccit19.merdog_doctor.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    // Count number of tabs
    private int tabCount;
    Drawable myDrawable;
    private String[] tabTitles = new String[]{"진행중인 상담", "완료된 상담"};


    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return tabTitles[position];
    }


    @Override
    public Fragment getItem(int position) {

        // Returning the current tabs
        switch (position) {
            case 0:
                TabFragmentChatRoomIng tabFragmentChatRoomIng = new TabFragmentChatRoomIng();
                return tabFragmentChatRoomIng ;
            case 1:
                TabFragmentChatRoomFinish tabFragmentChatRoomFinish = new TabFragmentChatRoomFinish();
                return tabFragmentChatRoomFinish ;
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return tabCount;
    }
}
