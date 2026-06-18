package com.igodating.commons.postgre_repostiry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igodating.commons.dto.Page;
import com.igodating.commons.dto.PaginationRequest;
import kr.pe.kwonnam.freemarkerdynamicqlbuilder.DynamicQuery;
import kr.pe.kwonnam.freemarkerdynamicqlbuilder.FreemarkerDynamicQlBuilder;
import org.postgresql.util.PGInterval;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class AbstractDbRepository {
    protected static final TypeReference<Map<String, JsonNode>> TYPE_REFERENCE_MAP_STRING_JSON_NODE = new TypeReference<>() {
    };
    protected static final TypeReference<Map<String, Object>> TYPE_REFERENCE_MAP_STRING_OBJECT = new TypeReference<>() {
    };

    protected final NamedParameterJdbcTemplate jdbcTemplate;
    protected final ObjectMapper objectMapper;

    protected AbstractDbRepository(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * @param field      named field in sql query
     * @param objectType array element type. Note if you fetch jsonb array from postgres, getArrayList(rs, "field", String.class) would fail.
     *                   Use getArrayList(rs, "field", PGobject.class) instead.
     *                   {@link org.postgresql.util.PGobject}
     */
    public static <T> List<T> getListArray(ResultSet rs, String field, Class<? extends T> objectType) {
        try {
            final Array array = rs.getArray(field);
            if (array == null) {
                return Collections.emptyList();
            }
            final ResultSet arrayResultSet = array.getResultSet();
            final ArrayList<T> arr = new ArrayList<>();
            while (arrayResultSet.next()) {
                //column index 1 is array index, so we use 2 to fetch real object
                final T object = arrayResultSet.getObject(2, objectType);
                arr.add(object);
            }
            return arr;
        } catch (SQLException e) {
            throw new UncategorizedSQLException("Get array", null, e);
        }
    }


    @SuppressWarnings("ConstantConditions")
    protected <T> Page<T> pageQuery(RowMapper<T> mapper, MapSqlParameterSource parameters, FreemarkerDynamicQlBuilder freemarkerDynamicQlBuilder, String freemarkerQueryTemplate) {
        parameters.addValue("onlyCount", true);
        DynamicQuery dynamicQuery = freemarkerDynamicQlBuilder.buildQuery(freemarkerQueryTemplate, parameters.getValues());
        final long totalCount = this.jdbcTemplate.getJdbcOperations().queryForObject(dynamicQuery.getQueryString(), Long.class, dynamicQuery.getQueryParameterArray());
        if (totalCount != 0L) {
            parameters.addValue("onlyCount", false);
            dynamicQuery = freemarkerDynamicQlBuilder.buildQuery(freemarkerQueryTemplate, parameters.getValues());
            final List<T> objects = this.jdbcTemplate.getJdbcOperations().query(dynamicQuery.getQueryString(), mapper, dynamicQuery.getQueryParameterArray());
            return new Page<>(objects, totalCount);
        }
        return Page.empty();
    }

    protected List<Long> generateSeqIds(String seqName, int rowNum) {
        final MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("sequenceName", seqName)
                .addValue("rowNum", rowNum);
        return this.jdbcTemplate.queryForList("select nextval(:sequenceName) from generate_series(1, :rowNum)", map, Long.class);
    }

    protected List<Long> generateRandomSeqIds(String seqName, int rowNum) {
        final MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("sequenceName", seqName)
                .addValue("rowNum", rowNum);
        return this.jdbcTemplate.queryForList("select nextval_rand(:sequenceName) from generate_series(1, :rowNum)", map, Long.class);
    }

    protected Long generateSeqId(String seqName) {
        return this.generateSeqIds(seqName, 1).getFirst();
    }

    protected Map<String, Integer> paginationToValueParam(PaginationRequest request) {
        return this.paginationToValueParam(request.getNum(), request.getSize());
    }

    protected Map<String, Integer> paginationToValueParam(int pageNum, int pageSize) {
        return Map.of("offset", (pageNum - 1) * pageSize, "limit", pageSize);
    }

    public Optional<String> extractConstraint(DuplicateKeyException e) {
        final PSQLException cause = (PSQLException) e.getCause();
        return Optional.ofNullable(cause.getServerErrorMessage()).map(ServerErrorMessage::getConstraint);
    }

    public Optional<String> extractConstraint(DataIntegrityViolationException e) {
        final PSQLException cause = (PSQLException) e.getCause();
        return Optional.ofNullable(cause.getServerErrorMessage()).map(ServerErrorMessage::getTable);
    }

    protected <T> ResultSetExtractor<Page<T>> pageResultSetExtractor(RowMapper<? extends T> rowMapper) {
        return this.pageResultSetExtractor("total_count", rowMapper);
    }


    protected <V, T> ResultSetExtractor<Page<V>> pageResultSetExtractor(Function<? super T, ? extends V> converter,
                                                                        RowMapper<? extends T> rowMapper) {
        return this.pageResultSetExtractor("total_count", converter, rowMapper);
    }

    public static @Nullable Duration mapFromInterval(@Nullable PGInterval pgi) {
        if (pgi == null) return null;
        return Duration.ofDays(pgi.getDays())
                .plus(pgi.getHours(), ChronoUnit.HOURS)
                .plus(pgi.getMinutes(), ChronoUnit.MINUTES)
                .plus(pgi.getWholeSeconds(), ChronoUnit.SECONDS)
                .plus(pgi.getMicroSeconds(), ChronoUnit.MICROS);
    }

    protected <T> ResultSetExtractor<Page<T>> pageResultSetExtractor(String totalCountColumnName,
                                                                     RowMapper<? extends T> rowMapper) {
        return rs -> {
            if (rs.next()) {
                final long totalCount = rs.getLong(totalCountColumnName);
                final List<T> result = new ArrayList<>();
                do {
                    result.add(rowMapper.mapRow(rs, rs.getRow()));
                } while (rs.next());
                return new Page<>(result, totalCount);
            } else {
                return Page.empty();
            }
        };
    }

    protected <V, T> ResultSetExtractor<Page<V>> pageResultSetExtractor(String totalCountColumnName,
                                                                        Function<? super T, ? extends V> converter,
                                                                        RowMapper<? extends T> rowMapper) {
        return rs -> {
            if (rs.next()) {
                final long totalCount = rs.getLong(totalCountColumnName);
                final List<V> result = new ArrayList<>();
                do {
                    result.add(converter.apply(rowMapper.mapRow(rs, rs.getRow())));
                } while (rs.next());
                return new Page<>(result, totalCount);
            } else {
                return Page.empty();
            }
        };
    }

    protected <V, T> ResultSetExtractor<List<V>> listResultSetExtractor(Function<? super T, ? extends V> converter,
                                                                        RowMapper<? extends T> rowMapper) {
        return rs -> {
            List<V> result = new ArrayList<>();
            while (rs.next()) {
                result.add(converter.apply(rowMapper.mapRow(rs, rs.getRow())));
            }
            return result;
        };
    }


    protected String toJson(Object object) {
        if (object == null) return null;
        try {
            return this.objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected JsonNode fromJson(String json) {
        if (json == null) return null;
        try {
            return this.objectMapper.readTree(json);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected <T> T fromJson(String json, Class<T> target) {
        if (json == null) return null;
        try {
            return this.objectMapper.readValue(json, target);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    protected <T> T fromJson(String json, TypeReference<T> target) {
        if (json == null) return null;
        try {
            return this.objectMapper.readValue(json, target);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
