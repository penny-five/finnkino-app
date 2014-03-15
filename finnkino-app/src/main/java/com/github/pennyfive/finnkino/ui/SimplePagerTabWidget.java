package com.github.pennyfive.finnkino.ui;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.pennyfive.finnkino.R;

/**
 * Simple tab widget to be used with {@link android.support.v4.view.ViewPager}.
 */
public class SimplePagerTabWidget extends LinearLayout implements OnClickListener, OnPageChangeListener {
    private ViewPager pager;

    public SimplePagerTabWidget(Context context) {
        super(context);
    }

    public SimplePagerTabWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimplePagerTabWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPager(ViewPager pager) {
        this.pager = pager;
        PagerAdapter adapter = pager.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            addTab(adapter.getPageTitle(i));
        }
        setSelection(pager.getCurrentItem());
        pager.setOnPageChangeListener(this);
    }

    private void addTab(CharSequence title) {
        TextView view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.simple_tab_item, null);
        view.setText(title);
        view.setTag(getChildCount());
        view.setOnClickListener(this);
        view.setGravity(Gravity.CENTER);
        addView(view, new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
        setWeightSum(getChildCount());
    }

    @Override
    public final void onClick(View v) {
        int position = (int) v.getTag();
        setSelection(position);
        pager.setCurrentItem(position);
    }

    private void setSelection(int position) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setSelected(position == i);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setSelection(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
