/*
 * This file is generated by jOOQ.
 */
package com.coyotesong.examples.persistence.jooq.generated;


import com.coyotesong.examples.persistence.jooq.generated.tables.I18nLanguage;
import com.coyotesong.examples.persistence.jooq.generated.tables.I18nRegion;
import com.coyotesong.examples.persistence.jooq.generated.tables.records.I18nLanguageRecord;
import com.coyotesong.examples.persistence.jooq.generated.tables.records.I18nRegionRecord;

import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in
 * public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<I18nLanguageRecord> I18N_LANGUAGE_PKEY = Internal.createUniqueKey(I18nLanguage.I18N_LANGUAGE, DSL.name("i18n_language_pkey"), new TableField[] { I18nLanguage.I18N_LANGUAGE.KEY }, true);
    public static final UniqueKey<I18nRegionRecord> I18N_REGION_PKEY = Internal.createUniqueKey(I18nRegion.I18N_REGION, DSL.name("i18n_region_pkey"), new TableField[] { I18nRegion.I18N_REGION.KEY }, true);
}
