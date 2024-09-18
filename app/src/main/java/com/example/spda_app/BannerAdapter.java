package com.example.spda_app;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class BannerAdapter extends FragmentStateAdapter {

    private int[] images;

    public BannerAdapter(@NonNull FragmentActivity fragmentActivity, int[] images) {
        super(fragmentActivity);
        this.images = images;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 이미지를 표시하는 Fragment를 반환합니다.
        return ImageFragment.newInstance(images[position]);
    }

    @Override
    public int getItemCount() {
        // 이미지의 개수를 반환합니다.
        return images.length;
    }
}