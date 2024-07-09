package com.coyotesong.examples.repository;

import com.coyotesong.examples.model.I18nLanguage;

import java.util.Collection;
import java.util.List;

public interface I18nLanguageRepository {
    void delete();

    void insert(I18nLanguage language);

    void insert(Collection<I18nLanguage> languages);

    List<I18nLanguage> findAll();

    I18nLanguage findByCodeAndLocale(String code, String locale);

    List<I18nLanguage> findAllForLocale(String locale);
}
