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

import com.coyotesong.examples.model.I18nLanguage;
import com.coyotesong.examples.persistence.jooq.generated.tables.records.I18nLanguageRecord;
import com.coyotesong.examples.repository.I18nLanguageRepository;
import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.coyotesong.examples.persistence.jooq.generated.tables.I18nLanguage.I18N_LANGUAGE;

/**
 * Implementation of I18nLanguageRepository
 */
@Repository
public class I18nLanguageRepositoryJooq extends DAOImpl<I18nLanguageRecord, I18nLanguage, Integer> implements I18nLanguageRepository {
    private static final Logger LOG = LoggerFactory.getLogger(I18nLanguageRepositoryJooq.class);

    @Autowired
    public I18nLanguageRepositoryJooq(Configuration configuration) {
        super(I18N_LANGUAGE, I18nLanguage.class, configuration);
    }

    @Override
    public Integer getId(I18nLanguage language) {
        return language.getKey();
    }

    @Override
    public void delete() {
        ctx().deleteFrom(I18N_LANGUAGE).execute();
        // super.delete();
    }

    @Override
    public void insert(I18nLanguage language) {
        LOG.info("insert: {}", language);
        super.insert(language);
        //ctx().insertInto(I18N_LANGUAGE).set(I18nLanguageRecord...
    }

    @Override
    public void insert(Collection<I18nLanguage> languages) {
        LOG.info("insert: {}", languages.stream().map(I18nLanguage::toString).collect(Collectors.joining(", ")));
        super.insert(languages);
    }


    @Override
    public I18nLanguage findByCodeAndLocale(String code, String locale) {
        return ctx().selectFrom(I18N_LANGUAGE).where(I18N_LANGUAGE.CODE.eq(code).and(I18N_LANGUAGE.HL.eq(locale)))
                .fetchOneInto(I18nLanguage.class);
    }

    @Override
    public List<I18nLanguage> findAllForLocale(String locale) {
        return ctx().selectFrom(I18N_LANGUAGE).where(I18N_LANGUAGE.HL.eq(locale)).fetchInto(I18nLanguage.class);
    }
}
