//package com.example.spda_app;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//
//import androidx.viewpager.widget.ViewPager;
//
//public class NewViewPager extends ViewPager {
//    private boolean swipeEnabled = false;
//
//    public NewViewPager(Context context) {
//        super(context);
//    }
//
//    public NewViewPager(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        // 터치 이벤트를 무시하여 슬라이드를 방지합니다.
//        return swipeEnabled && super.onTouchEvent(event);
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        // 터치 이벤트를 가로채서 슬라이드를 방지합니다.
//        return false;
//    }
//}

