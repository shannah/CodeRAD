/*
 * Copyright 2020 shannah.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codename1.rad.schemas;

import com.codename1.rad.models.Tag;

/**
 *
 * @author shannah
 */
public interface MediaObject extends CreativeWork {
    public static final Tag associatedArticle = new Tag("associatedArticle"),
            bitrate = new Tag("bitrate"),
            contentSize = new Tag("contentSize"),
            contentUrl = new Tag("contentUrl"),
            duration = new Tag("duration"),
            embedUrl = new Tag("embedUrl"),
            encodesCreativeWork = new Tag("encodesCreativeWork"),
            encodingFormat = new Tag("encodingFormat"),
            endTime = new Tag("endTime"),
            height = new Tag("height"),
            playerType = new Tag("playerType"),
            productionCompany = new Tag("productionCompany"),
            regionsAllowed = new Tag("regionsAllowed"),
            requiresSubscription = new Tag("requiresSubscription"),
            startTime = new Tag("startTime"),
            uploadDate = new Tag("uploadDate"),
            width = new Tag("width");
}
