/*
 * This file is generated by jOOQ.
 */
package com.coyotesong.examples.persistence.jooq.generated;


import com.coyotesong.examples.persistence.jooq.generated.tables.I18nLanguage;
import com.coyotesong.examples.persistence.jooq.generated.tables.I18nRegion;
import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;

import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes", "this-escape"})
public class Public extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * The table <code>public.i18n_language</code>.
     */
    public final I18nLanguage I18N_LANGUAGE = I18nLanguage.I18N_LANGUAGE;

    /**
     * The table <code>public.i18n_region</code>.
     */
    public final I18nRegion I18N_REGION = I18nRegion.I18N_REGION;

    /**
     * No further instances allowed
     */
    private Public() {
        super("public", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
                I18nLanguage.I18N_LANGUAGE,
                I18nRegion.I18N_REGION
        );
    }
}
