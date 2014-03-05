package com.github.pennyfive.finnkino;

import android.content.Context;

import com.github.pennyfive.finnkino.api.FinnkinoApi;
import com.github.pennyfive.finnkino.api.xml.Serializers;
import com.github.pennyfive.finnkino.ui.ApiQueryLoader;
import com.github.pennyfive.finnkino.ui.EventActivity;
import com.github.pennyfive.finnkino.ui.EventListActivity;
import com.github.pennyfive.finnkino.ui.EventListFragment;
import com.github.pennyfive.finnkino.ui.NavigationFragment;
import com.github.pennyfive.finnkino.util.HttpClient;
import com.squareup.picasso.Picasso;

import org.simpleframework.xml.Serializer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 *
 */
@Module(
        complete = true,
        injects = {
                FinnkinoApplication.class,
                FinnkinoApi.class,
                ApiQueryLoader.class,
                EventListActivity.class,
                EventListFragment.class,
                EventActivity.class,
                NavigationFragment.class
        }
)
public class FinnkinoModule {
    private final Context context;

    FinnkinoModule(Context context) {
        this.context = context.getApplicationContext();
    }

    @Provides @Singleton HttpClient provideHttpClient() {
        return new HttpClient();
    }

    @Provides @Singleton Serializer provideSerializer() {
        return Serializers.DEFAULT;
    }

    @Provides @Singleton FinnkinoApi provideFinnkinoApi() {
        return new FinnkinoApi();
    }

    @Provides @Singleton Picasso providePicasso() {
        return Picasso.with(context);
    }
}
