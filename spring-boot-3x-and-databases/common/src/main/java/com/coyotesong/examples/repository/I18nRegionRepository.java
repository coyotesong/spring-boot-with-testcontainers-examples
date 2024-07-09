package com.coyotesong.examples.repository;

import com.coyotesong.examples.model.I18nRegion;

import java.util.Collection;
import java.util.List;

public interface I18nRegionRepository {
    void delete();

    void insert(Collection<I18nRegion> regions);

    List<I18nRegion> findAll();

    I18nRegion findByCodeAndLocale(String code, String locale);

    List<I18nRegion> findAllForLocale(String locale);
}
