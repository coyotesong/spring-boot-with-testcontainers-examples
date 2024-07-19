/*
 * Copyright (c) 2024 Bear Giles <bgiles@coyotesong.com>.
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.coyotesong.examples.repository;

import com.coyotesong.examples.model.I18nLanguage;
import com.coyotesong.examples.model.I18nRegion;
import org.springframework.stereotype.Component;

@Component
public class TestObjectFactory {

    public I18nLanguage newLanguage(int idx) {
        final I18nLanguage language = new I18nLanguage();

        switch (idx) {
            case 1:
                language.setCode("en");
                language.setName("English");
                language.setHl("en");
                break;

            case 2:
                language.setCode("es");
                language.setName("Español");
                language.setHl("es");
                break;

            default:
                throw new RuntimeException("index out of range!");
        }

        return language;
    }

    public I18nRegion newRegion(int idx) {
        final I18nRegion region = new I18nRegion();

        switch (idx) {
            case 1:
                region.setCode("US");
                region.setName("United States");
                region.setHl("en");
                region.setGl("US");
                break;

            case 2:
                region.setCode("US");
                region.setName("Estados Unidos");
                region.setHl("es");
                region.setGl("US");
                break;

            default:
                throw new RuntimeException("index out of range!");
        }

        return region;
    }
}
