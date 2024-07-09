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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

/**
 * YouTube I18nRegion
 * <p>
 * YouTube provides information on 110 regions
 * <p>
 * YouTube provides information in 83 locales (80 unique - (en, en-GB, en-IN) and (es-US, es-419) share parent etag
 * <p>
 * see <a href="https://googleapis.dev/java/google-api-services-youtube/latest/com/google/api/services/youtube/model/I18nRegion.html">I18nRegion</a>
 */
@SuppressWarnings("unused")
public class I18nRegion implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer key;
    private String code;
    private String hl;
    private String name;
    private String gl;  // two-letter ISO 3166 country code

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGl() {
        return gl;
    }

    public void setGl(String gl) {
        this.gl = gl;
    }

    // alias
    public String getHl() {
        return hl;
    }

    public void setHl(String hl) {
        this.hl = hl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final I18nRegion that = (I18nRegion) o;

        return new EqualsBuilder()
                // DO NOT INCLUDE KEY!
                .append(code, that.code)
                .append(name, that.name)
                .append(hl, that.hl)
                .append(gl, that.gl)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(code)
                .append(name)
                .append(hl)
                .append(gl)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("key", key)
                .append("code", code)
                .append("hl", hl)
                .append("name", name)
                .append("gl", gl)
                .toString();
    }
}
