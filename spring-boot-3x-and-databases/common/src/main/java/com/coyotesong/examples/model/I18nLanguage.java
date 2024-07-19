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

package com.coyotesong.examples.model;

//import jakarta.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

/**
 * YouTube I18nLanguage
 *
 * Youtube provides information in 83 locales (82 unique - 'en' and 'en-GB' share parent etag)
 *
 * see <a href="https://googleapis.dev/java/google-api-services-youtube/latest/com/google/api/services/youtube/model/I18nLanguage.html">I18nLanguage</a>
 * see https://en.wikipedia.org/wiki/Codes_for_constructed_languages
 * see https://en.wikipedia.org/wiki/IETF_language_tag
 */
//@Table
@SuppressWarnings("JavadocBlankLines")
public class I18nLanguage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer key;
    private String code;
    private String name;
    private String hl;  // ISO 639 / BCP-47 code

    //@Id
    //@GeneratedValue(strategy = AUTO)
    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    //@Column(nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    //@Column(nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //@Column(nullable = false)
    public String getHl() {
        return hl;
    }

    public void setHl(String hl) {
        this.hl = hl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof I18nLanguage that) {
            return new EqualsBuilder()
                    // DO NOT INCLUDE KEY!
                    .append(code, that.code)
                    .append(name, that.name)
                    .append(hl, that.hl)
                    .isEquals();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(code)
                .append(name)
                .append(hl)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("key", key)
                .append("code", code)
                .append("hl", hl)
                .append("name", name)
                .toString();
    }
}
