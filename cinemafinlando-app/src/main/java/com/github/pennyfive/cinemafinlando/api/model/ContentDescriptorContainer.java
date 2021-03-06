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

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Root(strict = false)
public class ContentDescriptorContainer extends Container<ContentDescriptor> {
    @ElementList(entry = "ContentDescriptor", inline = true, required = false)
    private List<ContentDescriptor> descriptors = new ArrayList<>();

    @Override
    public List<ContentDescriptor> getItems() {
        return descriptors;
    }
}
