package com.github.pennyfive.cinemafinlando.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.pennyfive.cinemafinlando.CinemaFinlandoApplication.InjectUtils;
import com.github.pennyfive.cinemafinlando.CinemaFinlandoIntents;
import com.github.pennyfive.cinemafinlando.R;
import com.github.pennyfive.cinemafinlando.api.model.ContentDescriptor;
import com.github.pennyfive.cinemafinlando.api.model.DetailedEvent;
import com.github.pennyfive.cinemafinlando.api.model.DetailedEventContainer;
import com.github.pennyfive.cinemafinlando.api.model.DetailedEventGallery;
import com.github.pennyfive.cinemafinlando.api.model.DetailedEventGallery.Image;
import com.github.pennyfive.cinemafinlando.api.service.GetEventsCommand;
import com.github.pennyfive.cinemafinlando.ui.ApiQueryLoader;
import com.github.pennyfive.cinemafinlando.ui.UiUtils;
import com.github.pennyfive.cinemafinlando.ui.adapter.BinderAdapter;
import com.github.pennyfive.cinemafinlando.ui.fragment.generic.MultiStateFragment;
import com.squareup.picasso.Picasso;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.inject.Inject;

import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.AdapterView.OnItemClickListener;
import it.sephiroth.android.library.widget.HListView;

/**
 *
 */
public class EventDetailsFragment extends MultiStateFragment implements LoaderCallbacks<DetailedEventContainer>, OnItemClickListener {
    private static final DateTimeFormatter RELEASE_DATE_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy");

    private static class GalleryAdapter extends BinderAdapter<Image> {

        public GalleryAdapter(Context context, DetailedEventGallery gallery) {
            super(context, gallery.getImages());
        }

        @Override
        protected View newView(LayoutInflater inflater, ViewGroup parent) {
            return inflater.inflate(R.layout.item_event_gallery, parent, false);
        }

        @Override
        protected void bindView(View view, Image item, int position) {
            ImageView target = (ImageView) view.findViewById(R.id.image);
            Picasso.with(getContext()).load(item.getThumbnailUrl()).placeholder(android.R.color.black).into(target);
        }
    }

    @Inject Picasso picasso;
    private DetailedEvent event;

    private View eventDetailsView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectUtils.injectMembers(this);
        getLoaderManager().initLoader(0, savedInstanceState, this);
    }

    @Override
    protected int getStartupState() {
        return STATE_LOADING;
    }

    @Override
    protected View onCreateStateView(int state) {
        switch (state) {
            case STATE_LOADING:
                View view = UiUtils.inflateDefaultLoadingView(getActivity());
                view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                return view;
            case STATE_CONTENT:
                ensureEventDetailsView();
                return eventDetailsView;
            case STATE_ERROR:
                return UiUtils.inflateDefaultConnectionErrorView(getActivity(), new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setState(STATE_LOADING);
                    }
                });
            default:
                throw new IllegalStateException("state: " + state);
        }
    }

    @Override
    protected void onStateChanged(int state, View view) {
        super.onStateChanged(state, view);
        switch (state) {
            case STATE_LOADING:
                getLoaderManager().restartLoader(0, null, this);
                break;
            default:
                break;
        }
    }

    @Override
    public Loader<DetailedEventContainer> onCreateLoader(int id, Bundle args) {
        setState(STATE_LOADING);
        String eventId = getArguments().getString(CinemaFinlandoIntents.EXTRA_EVENT_ID);
        GetEventsCommand command = GetEventsCommand.forEvent(UiUtils.getQueryLanguage(getActivity()), eventId);
        return new ApiQueryLoader<>(getActivity(), command);
    }

    @Override
    public void onLoadFinished(Loader<DetailedEventContainer> loader, DetailedEventContainer data) {
        if (data != null) {
            event = data.getItems().get(0);
            fillEventDetailsView();
            setState(STATE_CONTENT);
        } else {
            setState(STATE_ERROR);
        }
    }

    @Override
    public void onLoaderReset(Loader<DetailedEventContainer> loader) {

    }

    private void ensureEventDetailsView() {
        if (eventDetailsView == null) {
            eventDetailsView = View.inflate(getActivity(), R.layout.fragment_event_details, null);
        }
    }

    private void fillEventDetailsView() {
        ensureEventDetailsView();

        ((TextView) eventDetailsView.findViewById(R.id.synopsis)).setText(event.getSynopsis());
        ((TextView) eventDetailsView.findViewById(R.id.release_date)).setText(event.getReleaseDate().toString(RELEASE_DATE_FORMATTER));
        ((TextView) eventDetailsView.findViewById(R.id.production_year)).setText(String.valueOf(event.getProductionYear()));

        HListView galleryView = (HListView) eventDetailsView.findViewById(R.id.gallery);
        galleryView.setAdapter(new GalleryAdapter(getActivity(), event.getGallery()));
        galleryView.setOnItemClickListener(this);

        ((TextView) eventDetailsView.findViewById(R.id.age_rating)).setText(String.valueOf(event.getAgeRating()));

        ViewGroup descriptorIconLayout = (ViewGroup) eventDetailsView.findViewById(R.id.descriptor_icons);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        for (ContentDescriptor descriptor : event.getContentDescriptors().getItems()) {
            ImageView descriptorIconView = (ImageView) inflater.inflate(R.layout.descriptor_icon, descriptorIconLayout, false);
            descriptorIconLayout.addView(descriptorIconView);
            picasso.load(descriptor.getImageUrl()).into(descriptorIconView);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(CinemaFinlandoIntents.ACTION_VIEW_GALLERY);
        intent.putExtra(CinemaFinlandoIntents.EXTRA_TITLE, event.getTitle());
        intent.putExtra(CinemaFinlandoIntents.EXTRA_GALLERY, event.getGallery());
        intent.putExtra(CinemaFinlandoIntents.EXTRA_POSITION, i);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
