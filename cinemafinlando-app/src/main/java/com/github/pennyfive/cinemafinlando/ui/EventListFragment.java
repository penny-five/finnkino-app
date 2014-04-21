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

import android.content.Context;
import android.os.Bundle;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.RenderScript.ContextType;
import android.support.v8.renderscript.RenderScript.Priority;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.pennyfive.cinemafinlando.CinemaFinlandoApplication.InjectUtils;
import com.github.pennyfive.cinemafinlando.CinemaFinlandoIntents;
import com.github.pennyfive.cinemafinlando.R;
import com.github.pennyfive.cinemafinlando.api.model.Event;
import com.github.pennyfive.cinemafinlando.api.model.Events;
import com.github.pennyfive.cinemafinlando.api.model.Show;
import com.github.pennyfive.cinemafinlando.api.model.TheatreArea;
import com.github.pennyfive.cinemafinlando.api.service.Command;
import com.github.pennyfive.cinemafinlando.api.service.GetEventsCommand;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import javax.inject.Inject;

/**
 *
 */
public class EventListFragment extends QueryAbsListFragment<Event, Events> {
    public static final String EXTRA_LIST_TYPE = "list_type";
    public static final String LIST_TYPE_NOW_IN_THEATRES = "now_playing";
    public static final String LIST_TYPE_COMING_SOON = "coming_soon";
    public static final String LIST_TYPE_THEATRE_AREA = "theatre_area";

    public interface Callbacks {
        void onEventSelected(Event event);
    }

    @Inject Picasso picasso;
    @Inject RenderScript rs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectUtils.injectMembers(this);
        rs = RenderScript.create(getActivity(), ContextType.DEBUG);
        rs.setPriority(Priority.LOW);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rs.destroy();
    }

    @Override
    protected Command<Events> onCreateCommand() {
        Bundle args = getArguments();
        switch (args.getString(EXTRA_LIST_TYPE)) {
            case LIST_TYPE_COMING_SOON:
                return GetEventsCommand.comingSoon();
            case LIST_TYPE_NOW_IN_THEATRES:
                return GetEventsCommand.nowInTheatres();
            case LIST_TYPE_THEATRE_AREA:
                return GetEventsCommand.nowInTheatres((TheatreArea) args.get(CinemaFinlandoIntents.EXTRA_THEATRE_AREA));
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    protected AbsListView createAbsListView() {
        return (AbsListView) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_event_list, null);
    }

    @Override
    protected View newView(Context context, LayoutInflater inflater, Event event) {
        return inflater.inflate(R.layout.item_event, null);
    }

    @Override
    protected void bindView(Context context, final View view, Event event) {
        ((TextView) view.findViewById(R.id.text)).setText(event.getTitle());
        Transformation transform = new EventBackgroundBlurTransformation(rs, view);
        ImageView target = (ImageView) view.findViewById(R.id.image);
        picasso.load(event.getImageUrl(Show.SIZE_LANDSCAPE_LARGE)).placeholder(R.drawable.event_placeholder).transform(transform).into(target);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((Callbacks) getActivity()).onEventSelected(getItem(position));
    }
}
