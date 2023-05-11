package com.example.linguasnap;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LoginAdapter extends FragmentPagerAdapter {

    private activity_loginds context;
    int totalTabs;

    public LoginAdapter(FragmentManager fm, activity_loginds context, int totalTabs){
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

    public Fragment getItem(int position){
        switch (position){
            case 0:
                login_tab loginTab = new login_tab();
                return  loginTab;

            case  1:
                register_tab registerTab = new register_tab();
                return  registerTab;

            default:
                return null;
        }

    }
}
