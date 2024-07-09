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

package com.coyotesong.examples.persistence.jooq;

import com.coyotesong.examples.model.I18nRegion;
import com.coyotesong.examples.persistence.jooq.generated.tables.records.I18nRegionRecord;
import com.coyotesong.examples.repository.I18nRegionRepository;
import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.coyotesong.examples.persistence.jooq.generated.tables.I18nRegion.I18N_REGION;

/**
 * Implementation of I18nRegionRepository
 */
@Repository
public class I18nRegionRepositoryJooq extends DAOImpl<I18nRegionRecord, I18nRegion, Integer> implements I18nRegionRepository {

    @Autowired
    public I18nRegionRepositoryJooq(Configuration configuration) {
        super(I18N_REGION, I18nRegion.class, configuration);
    }

    @Override
    public Integer getId(I18nRegion region) {
        return region.getKey();
    }

    @Override
    public void delete() {
        ctx().deleteFrom(I18N_REGION).execute();
    }

    @Override
    public I18nRegion findByCodeAndLocale(String code, String locale) {
        return ctx().selectFrom(I18N_REGION).where(I18N_REGION.CODE.eq(code).and(I18N_REGION.HL.eq(locale)))
                .fetchSingleInto(I18nRegion.class);
    }

    @Override
    public List<I18nRegion> findAllForLocale(String locale) {
        return ctx().selectFrom(I18N_REGION).where(I18N_REGION.HL.eq(locale)).fetchInto(I18nRegion.class);
    }
}
