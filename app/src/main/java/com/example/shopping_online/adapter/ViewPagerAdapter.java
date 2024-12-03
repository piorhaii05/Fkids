package com.example.shopping_online.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.shopping_online.fragment.SettingFragment;
import com.example.shopping_online.fragment.ContactFragment;
import com.example.shopping_online.fragment.HomeFragment;
import com.example.shopping_online.fragment.ListLikeFragment;

// Thiết lập Adapter extends FragmentStateAdapter
public class ViewPagerAdapter extends FragmentStateAdapter {
    public final HomeFragment homeFragment;
    public final ListLikeFragment listLikeFragment;
    public final SettingFragment settingFragment;
    public final ContactFragment contactFragment;

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        homeFragment = new HomeFragment();
        listLikeFragment = new ListLikeFragment();
        settingFragment = new SettingFragment();
        contactFragment = new ContactFragment();
    }

    // Trả về số thứ tự của các Fragment
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return listLikeFragment;
            case 2:
                return contactFragment;
            case 3:
                return settingFragment;
            default:
                return homeFragment;
        }
    }

    // Trả về số fragment có trên tablayout
    @Override
    public int getItemCount() {
        return 4;
    }

}
