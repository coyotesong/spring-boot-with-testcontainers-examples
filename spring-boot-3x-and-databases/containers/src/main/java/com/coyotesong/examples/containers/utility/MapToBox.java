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

package com.coyotesong.examples.containers.utility;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Utility method that creates a boxed table
 *
 * This can be used to highlight information in log files. It should be
 * used sparingly, but is particularly useful when periodically recording
 * details about connection servers.
 */
@SuppressWarnings("JavadocBlankLines")
public class MapToBox {
    private static final Logger LOG = LoggerFactory.getLogger(MapToBox.class);

    private static final int MAX_COLUMNS = 80;

    // we can't load this from a property file since they use ISO-8859-1, not UTF-8
    private static final String HEADER_START = "╔═";
    private static final String HEADER_TICK = "═╤═";
    private static final String HEADER_MIDDLE =
            "════════════════════════════════════════════════════════════════════════════════" +
                    "════════════════════════════════════════════════════════════════════════════════" +
                    "════════════════════════════════════════════════════════════════════════════════" +
                    "════════════════════════════════════════════════════════════════════════════════";
    private static final String HEADER_END = "═╗\n";

    private static final String CONTENT_LINE_START = "║ ";
    private static final String CONTENT_TICK = " │ ";
    private static final String CONTENT_LINE_MIDDLE =
            "                                                                                " +
                    "                                                                                " +
                    "                                                                                " +
                    "                                                                                ";
    private static final String CONTENT_LINE_END = " ║\n";

    private static final String SEPARATOR_LINE_START = "╟─";
    private static final String SEPARATOR_TICK = "─┼─";
    private static final String SEPARATOR_LINE_MIDDLE =
            "──────────────────────────────────────────────────────────────────────────────" +
                    "──────────────────────────────────────────────────────────────────────────────" +
                    "──────────────────────────────────────────────────────────────────────────────" +
                    "──────────────────────────────────────────────────────────────────────────────";
    private static final String SEPARATOR_LINE_END = "─╢\n";

    private static final String FOOTER_START = "╚═";
    private static final String FOOTER_TICK = "═╧═";
    private static final String FOOTER_MIDDLE = HEADER_MIDDLE;
    private static final String FOOTER_END = "═╝";

    private final List<String> labels;
    private final List<List<String>> cache = new ArrayList<>();

    protected MapToBox(List<String> labels) {
        this.labels = new ArrayList<>(labels);
    }

    protected MapToBox(ResultSet rs, Predicate<ResultSet> p) throws SQLException {
        final ResultSetMetaData md = rs.getMetaData();

        // sanity check
        if (md.getColumnCount() == 0) {
            this.labels = new ArrayList<>();
            this.labels.add("(no data)");
            return;
        }

        // create list of labels
        this.labels = new ArrayList<>(md.getColumnCount());
        for (int i = 0; i < md.getColumnCount(); i++) {
            labels.add(md.getColumnLabel(1 + i));
        }

        // add contents
        while (rs.next()) {
            if (p.test(rs)) {
                // or make protective copy...
                final List<String> values = new ArrayList<>(md.getColumnCount());
                try {
                    for (int i = 0; i < md.getColumnCount(); i++) {
                        String value = rs.getString(1 + i);
                        if (isNotBlank(value)) {
                            value = value.replace("\n", " ").replace("\r", "").replace("\t", " ");
                            if (value.length() > MAX_COLUMNS) {
                                value = value.substring(0, MAX_COLUMNS - 3) + "...";
                            }
                        }
                        values.add(value);
                    }
                } catch (SQLException e) {
                    LOG.info("{}: error while retrieving record: {}", e.getClass().getName(), e.getMessage());
                }
                cache.add(values);
            }
        }
    }

    /**
     * Add a line of data
     *
     * @param values values
     */
    void addData(List<String> values) {
        // make defensive copy, limit string length
        final List<String> copy = new ArrayList<>(values.size());
        for (String value : values) {
            if (isNotBlank(value)) {
                value = value.replace("\n", " ").replace("\r", "").replace("\t", " ");
                if (value.length() > MAX_COLUMNS) {
                    value = value.substring(0, MAX_COLUMNS - 3) + "...";
                }
            }
            copy.add(value);
        }
        cache.add(copy);
    }

    /**
     * Get header
     *
     * @param columnSizes size of each column
     * @return formatted line
     */
    String getHeader(Collection<Integer> columnSizes) {
        final StringBuilder sb = new StringBuilder(HEADER_START);
        for (int len : columnSizes) {
            sb.append(HEADER_MIDDLE, 0, Math.min(HEADER_MIDDLE.length(), len));
            sb.append(HEADER_TICK);
        }
        if (!columnSizes.isEmpty()) {
            sb.setLength(sb.length() - HEADER_TICK.length());
        }
        sb.append(HEADER_END);

        return sb.toString();
    }

    /**
     * Get footer
     *
     * @param columnSizes size of each column
     * @return formatted line
     */
    String getFooter(Collection<Integer> columnSizes) {
        final StringBuilder sb = new StringBuilder(FOOTER_START);
        for (int len : columnSizes) {
            sb.append(FOOTER_MIDDLE, 0, Math.min(FOOTER_MIDDLE.length(), len));
            sb.append(FOOTER_TICK);
        }
        if (!columnSizes.isEmpty()) {
            sb.setLength(sb.length() - FOOTER_TICK.length());
        }
        sb.append(FOOTER_END);

        return sb.toString();
    }

    /**
     * Get separator line
     *
     * @param columnSizes size of each column
     * @return formatted line
     */
    String getSeparatorLine(Collection<Integer> columnSizes) {
        final StringBuilder sb = new StringBuilder(SEPARATOR_LINE_START);
        for (int len : columnSizes) {
            sb.append(SEPARATOR_LINE_MIDDLE, 0, Math.min(SEPARATOR_LINE_MIDDLE.length(), len));
            sb.append(SEPARATOR_TICK);
        }
        if (!columnSizes.isEmpty()) {
            sb.setLength(sb.length() - SEPARATOR_TICK.length());
        }
        sb.append(SEPARATOR_LINE_END);

        return sb.toString();
    }

    /**
     * Get header line
     *
     * @param labels      table labels
     * @param columnSizes size of each column
     * @return formatted line
     */
    String getHeader(List<String> labels, List<Integer> columnSizes) {
        final StringBuilder sb = new StringBuilder(CONTENT_LINE_START);
        for (int i = 0; i < labels.size(); i++) {
            String value = labels.get(i);
            sb.append(value);
            sb.append(CONTENT_LINE_MIDDLE, 0, Math.max(0, Math.min(CONTENT_LINE_MIDDLE.length(), columnSizes.get(i) - value.length())));
            sb.append(CONTENT_TICK);
        }
        if (!labels.isEmpty()) {
            sb.setLength(sb.length() - CONTENT_TICK.length());
        }
        sb.append(CONTENT_LINE_END);

        return sb.toString();
    }

    /**
     * Get content line
     *
     * @param values      table content
     * @param columnSizes size of each column
     * @return formatted line
     */
    String getContentLine(List<String> values, List<Integer> columnSizes) {
        final StringBuilder sb = new StringBuilder(CONTENT_LINE_START);
        for (int i = 0; i < values.size(); i++) {
            final String value = values.get(i);
            if (value == null) {
                sb.append(CONTENT_LINE_MIDDLE, 0, Math.min(CONTENT_LINE_MIDDLE.length(), columnSizes.get(i)));
            } else {
                sb.append(value);
                sb.append(CONTENT_LINE_MIDDLE, 0, Math.max(0, Math.min(CONTENT_LINE_MIDDLE.length(), columnSizes.get(i) - value.length())));
            }
            sb.append(CONTENT_TICK);
        }
        if (!values.isEmpty()) {
            sb.setLength(sb.length() - CONTENT_TICK.length());
        }
        sb.append(CONTENT_LINE_END);

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        // calculate the column sizes
        final List<Integer> columnSizes = getColumnSizes();

        final StringBuilder sb = new StringBuilder();
        sb.append(getHeader(columnSizes));
        sb.append(getHeader(labels, columnSizes));
        sb.append(getSeparatorLine(columnSizes));
        for (List<String> values : cache) {
            sb.append(getContentLine(values, columnSizes));
        }
        sb.append(getFooter(columnSizes));
        return sb.toString();
    }

    @NotNull
    private List<Integer> getColumnSizes() {
        final List<Integer> columnSizes = new ArrayList<>(labels.size());
        for (String label : labels) {
            columnSizes.add(label.length());
        }
        for (List<String> values : cache) {
            for (int i = 0; i < Math.min(columnSizes.size(), values.size()); i++) {
                if (values.get(i) != null) {
                    columnSizes.set(i, Math.max(columnSizes.get(i), values.get(i).length()));
                }
            }
        }
        return columnSizes;
    }

    /**
     * Write contents of a query to a box of text
     *
     * @param dataSource data source
     * @param query      SQL query
     * @return initialized builder
     * @throws SQLException an error occurred
     */
    @SuppressWarnings("unused")
    public static Builder newBuilder(DataSource dataSource, String query) throws SQLException {
        return new Builder(dataSource, query, line -> true);
    }

    /**
     * Write contents of a query to a box of text
     *
     * @param dataSource data source
     * @param query      SQL query
     * @param p          filter
     * @return initialized builder
     * @throws SQLException an error occurred
     */
    @SuppressWarnings("unused")
    public static Builder newBuilder(DataSource dataSource, String query, Predicate<ResultSet> p) throws SQLException {
        return new Builder(dataSource, query, p);
    }

    /**
     * Write contents of a query to a box of text
     *
     * @param rs ResultSet containing data
     * @return initialized builder
     * @throws SQLException an error occurred
     */
    @SuppressWarnings("unused")
    public static Builder newBuilder(ResultSet rs) throws SQLException {
        return new Builder(rs, line -> true);
    }

    /**
     * Write contents of a query to a box of text
     *
     * @param rs ResultSet containing data
     * @param p  filter
     * @return initialized builder
     * @throws SQLException an error occurred
     */
    @SuppressWarnings("unused")
    public static Builder newBuilder(ResultSet rs, Predicate<ResultSet> p) throws SQLException {
        return new Builder(rs, p);
    }

    /**
     * Write arbitrary contents to a box of text
     *
     * @param labels column headers
     * @return builder that must be populated with addData()
     */
    @SuppressWarnings("unused")
    public static Builder newBuilder(List<String> labels) {
        return new Builder(labels);
    }

    /**
     * Builder class
     */
    @SuppressWarnings("unused")
    public static class Builder {
        private final MapToBox box;

        /**
         * Write contents of a query to a box of text
         *
         * @param dataSource data source
         * @param query      SQL query
         * @param p          filter
         * @throws SQLException an error occurred
         */
        @SuppressWarnings("SqlSourceToSinkFlow")
        Builder(DataSource dataSource, String query, Predicate<ResultSet> p) throws SQLException {
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                 ResultSet rs = stmt.executeQuery(query)) {

                this.box = new MapToBox(rs, p);
            }
        }

        /**
         * Write contents of a query to a box of text
         *
         * @param rs ResultSet containing data
         * @param p  filter
         * @throws SQLException an error occurred
         */
        Builder(ResultSet rs, Predicate<ResultSet> p) throws SQLException {
            this.box = new MapToBox(rs, p);
        }

        /**
         * Write arbitrary contents to a box of text
         *
         * @param labels column headings
         */
        Builder(List<String> labels) {
            this.box = new MapToBox(labels);
        }

        public Builder addData(List<String> values) {
            box.addData(values);
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder addData(String... values) {
            return addData(Arrays.asList(values));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return box.toString();
        }
    }
}
