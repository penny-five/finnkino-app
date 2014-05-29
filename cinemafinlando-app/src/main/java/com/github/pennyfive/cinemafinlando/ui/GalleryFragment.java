package com.github.pennyfive.cinemafinlando.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.github.pennyfive.cinemafinlando.CinemaFinlandoApplication.InjectUtils;
import com.github.pennyfive.cinemafinlando.CinemaFinlandoIntents;
import com.github.pennyfive.cinemafinlando.api.model.DetailedEventGallery.Image;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

/**
 *
 */
public class GalleryFragment extends MultiStateFragment implements Callback {
    @Inject Picasso picasso;
    private ImageView imageView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InjectUtils.injectMembers(this);

        imageView = new ImageView(getActivity());

        Image image = getArguments().getParcelable(CinemaFinlandoIntents.EXTRA_IMAGE);
        picasso.load(image.getUrl()).into(imageView, this);
    }

    @Override
    public void onDestroyView() {
        // cancel request or otherwise Picasso keeps a reference to set Callback
        picasso.cancelRequest(imageView);
        super.onDestroyView();
    }

    @Override
    protected void onStateLayoutReady(Bundle savedInstanceState) {
        switchView(UiUtils.inflateDefaultLoadingView(getActivity()));
    }

    @Override
    public void onSuccess() {
        switchView(imageView);
    }

    @Override
    public void onError() {

    }
}
