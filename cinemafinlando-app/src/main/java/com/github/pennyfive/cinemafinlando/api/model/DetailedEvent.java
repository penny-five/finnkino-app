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

package com.github.pennyfive.cinemafinlando.api.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 */
@Root(strict = false)
public class DetailedEvent extends Event {
    @Element(name = "ID")
    private String id;
    @Element(name = "ShortSynopsis", required = false)
    private String shortSynopsis;
    @Element(name = "Synopsis", required = false)
    private String synopsis;
    @Element(name = "Gallery")
    private DetailedEventGallery gallery;
    @Element(name = "ContentDescriptors")
    private ContentDescriptorContainer descriptors;

    @Override
    public String getEventId() {
        return id;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getShortSynopsis() {
        return shortSynopsis;
    }

    public DetailedEventGallery getGallery() {
        return gallery;
    }

    public ContentDescriptorContainer getContentDescriptors() {
        return descriptors;
    }
}
