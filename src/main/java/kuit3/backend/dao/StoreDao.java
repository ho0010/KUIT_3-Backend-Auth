package kuit3.backend.dao;

import kuit3.backend.dto.store.GetStoreResponse;
import kuit3.backend.dto.store.PostStoreRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class StoreDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StoreDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long createStore(PostStoreRequest storeRequest) {
        String sql = "INSERT INTO Store (name, address,food_category,type,phone_number)" +
                "VALUES (:name, :address, :foodCategory, :type, :phoneNumber)";

        BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(storeRequest);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, paramSource, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public GetStoreResponse findStoreById(long storeId) {
        String sql = "SELECT store_id, name, address, food_category, type, phone_number FROM Store WHERE store_id = :storeId";
        return jdbcTemplate.queryForObject(sql, Collections.singletonMap("storeId", storeId), (rs, rowNum) -> new GetStoreResponse(
                rs.getLong("store_id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("food_category"),
                rs.getInt("type"),
                rs.getString("phone_number")
        ));
    }
    /*
    public List<GetStoreResponse> findAllStores() {
        String sql = "SELECT store_id, name, address, food_category, type, phone_number FROM Store";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new GetStoreResponse(
                rs.getLong("store_id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("food_category"),
                rs.getInt("type"),
                rs.getString("phone_number")
        ));
    }
     */

    public boolean hasDuplicateStoreName(String storename) {
        String sql = "select exists(select name from store where name =:name)";
        Map<String, Object> param = Map.of("name", storename);
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, param, boolean.class));
    }

    public int modifyFoodCategory(long storeId, String foodCategory) {
        String sql = "UPDATE store SET food_category = :foodCategory WHERE store_id = :storeId";
        Map<String, Object> params = Map.of(
                "storeId", storeId,
                "foodCategory", foodCategory

        );
        return jdbcTemplate.update(sql, params);
    }

    // 특정 스토어 주소 조회
    public Optional<String> findStoreAddressById(long storeId) {
        String sql = "SELECT address FROM store WHERE store_id = :storeId";
        Map<String, Object> params = Map.of("storeId", storeId);
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, params, String.class));
    }

    public List<GetStoreResponse> findStoresFromIndex(int endIndex, int limit) {
        String sql = "SELECT * FROM Store ORDER BY store_id LIMIT :limit OFFSET :endIndex";
        // store_id로 정렬하고, LIMIT & OFFSET를 이용해 페이징 처리
        Map<String, Object> params = Map.of(
                "endIndex", endIndex,
                "limit", limit
        );
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new GetStoreResponse(
                rs.getLong("store_id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("food_category"),
                rs.getInt("type"),
                rs.getString("phone_number")
        ));
    }
}

