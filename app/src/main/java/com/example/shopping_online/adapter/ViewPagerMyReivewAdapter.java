package com.example.shopping_online.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.shopping_online.fragment.MyReviewHaveItemFragment;
import com.example.shopping_online.fragment.MyReviewNoneFragment;

public class ViewPagerMyReivewAdapter extends FragmentStateAdapter {

    private final boolean hasItems;

    public ViewPagerMyReivewAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, boolean hasItems) {
        super(fragmentManager, lifecycle);
        this.hasItems = hasItems;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Trả về fragment dựa trên trạng thái dữ liệu
        if (hasItems) {
            return new MyReviewHaveItemFragment();
        } else {
            return new MyReviewNoneFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 1; // Chỉ hiển thị một fragment tại một thời điểm
    }
}
