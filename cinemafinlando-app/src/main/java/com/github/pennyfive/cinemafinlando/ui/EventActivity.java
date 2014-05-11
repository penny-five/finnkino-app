/*
 * Copyright 2014 Joonas Lehtonen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.pennyfive.cinemafinlando.ui;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.pennyfive.cinemafinlando.CinemaFinlandoApplication.InjectUtils;
import com.github.pennyfive.cinemafinlando.CinemaFinlandoIntents;
import com.github.pennyfive.cinemafinlando.R;
import com.github.pennyfive.cinemafinlando.api.model.DetailedEvent;
import com.github.pennyfive.cinemafinlando.api.model.EventGallery;
import com.github.pennyfive.cinemafinlando.ui.view.CustomTypefaceTextView.CustomTypeface;
import com.github.pennyfive.cinemafinlando.ui.view.ListenableScrollView;
import com.github.pennyfive.cinemafinlando.ui.view.ListenableScrollView.OnScrollListener;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * <p>
 * Shows information for a single {@link DetailedEvent}.
 * </p>
 * <p>
 * Action bar fade effect is based on <a href="http://cyrilmottier.com/2013/05/24/pushing-the-actionbar-to-the-next-level/">this blog
 * post</a>.
 * </p>
 */
public class EventActivity extends FragmentActivity implements OnScrollListener {
    @Inject Picasso picasso;

    @InjectView(R.id.scrollview) ListenableScrollView scrollView;
    @InjectView(R.id.poster) ImageView posterImageView;
    @InjectView(R.id.image) ImageView eventImageView;
    @InjectView(R.id.name) TextView nameTextView;
    @InjectView(R.id.genres) TextView genreTextView;
    @InjectView(R.id.length) TextView durationTextView;

    private Drawable actionBarBackgroundDrawable;
    private Interpolator actionBarBackgroundAlphaInterpolator = new DecelerateInterpolator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        InjectUtils.injectAll(this);

        if (savedInstanceState == null) {
            Fragment detailsFragment = UiUtils.instantiateWithIntent(EventDetailsFragment.class, getIntent());
            getSupportFragmentManager().beginTransaction().replace(R.id.container, detailsFragment).commit();
        }

        Bundle extras = getIntent().getExtras();

        getActionBar().setTitle(CustomTypeface.ROBOTO_LIGHT.wrap(this, extras.getString(CinemaFinlandoIntents.EXTRA_TITLE)));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);

        scrollView.setOnScrollListener(this);
        actionBarBackgroundDrawable = getResources().getDrawable(R.drawable.ab_solid_cinemafinlando);
        actionBarBackgroundDrawable.setAlpha(0);
        if (VERSION.SDK_INT <= VERSION_CODES.JELLY_BEAN_MR1) {
            actionBarBackgroundDrawable.setCallback(actionBarBackgroundCallback);
        }
        getActionBar().setBackgroundDrawable(actionBarBackgroundDrawable);

        EventGallery eventImageGallery = extras.getParcelable(CinemaFinlandoIntents.EXTRA_IMAGES);
        picasso.load(eventImageGallery.getUrl(EventGallery.SIZE_LANDSCAPE_LARGE)).into(eventImageView);
        picasso.load(eventImageGallery.getUrl(EventGallery.SIZE_PORTRAIT_LARGE)).into(posterImageView);

        nameTextView.setText(extras.getString(CinemaFinlandoIntents.EXTRA_ORIGINAL_TITLE));
        genreTextView.setText(extras.getString(CinemaFinlandoIntents.EXTRA_GENRES));
        durationTextView.setText(getString(R.string.minutes, extras.getInt(CinemaFinlandoIntents.EXTRA_LENGTH)));
    }

    @Override
    public void onScroll(int position) {
        float ratio = Math.max(0, Math.min(1, position / (float) eventImageView.getHeight()));
        float interpolatedRatio = actionBarBackgroundAlphaInterpolator.getInterpolation(ratio);
        actionBarBackgroundDrawable.setAlpha((int) (interpolatedRatio * 255));
    }

    /**
     * Devices older than JELLY_BEAN_MR2 require some extra work for the fading action bar to work.
     */
    private final Callback actionBarBackgroundCallback = new Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {

        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {

        }
    };
}
