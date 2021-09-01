package org.prgrms.kdt.domain.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
public class CustomerJdbcRepository implements CustomerRepository {

    private static final String SELECT_BY_NAME_SQL = "SELECT * FROM customers WHERE name = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM customers WHERE customer_id = UUID_TO_BIN(?)";
    private static final String SELECT_BY_EMAIL_SQL = "SELECT * FROM customers WHERE email = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM customers";
    private static final String COUNT_SQL = "SELECT COUNT(*) FROM customers";
    private static final String INSERT_SQL = "INSERT INTO customers(customer_id, name, email, created_at) VALUES (UUID_TO_BIN(?), ?, ?, ?)";
    private static final String UPDATE_BY_ID_SQL = "UPDATE customers SET name = ?, email = ?, last_login_at = ? WHERE customer_id = UUID_TO_BIN(?)";
    private static final String DELETE_ALL_SQL = "DELETE FROM customers";

    private static final RowMapper<Customer> customerRowMapper = (resultSet, i) -> {
        var customerName = resultSet.getString("name");
        var email = resultSet.getString("email");
        var customerId = toUUID(resultSet.getBytes("customer_id"));
        var lastLoginAt = resultSet.getTimestamp("last_login_at") != null ?
                resultSet.getTimestamp("last_login_at").toLocalDateTime() : null;
        var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        return new Customer(customerId, customerName, email, lastLoginAt, createdAt);
    };

    private final JdbcTemplate jdbcTemplate;

    public CustomerJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    static UUID toUUID(byte[] bytes) {
        var byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

    @Override
    public Customer insert(Customer customer) {
        var update = jdbcTemplate.update(INSERT_SQL,
                customer.getCustomerId().toString().getBytes(),
                customer.getName().getName(),
                customer.getEmail().getEmail(),
                Timestamp.valueOf(customer.getCreatedAt())
        );
        if (update != 1) {
            throw new RuntimeException("Nothing was inserted");
        }
        return customer;
    }

    @Override
    public Customer update(Customer customer) {
        var update = jdbcTemplate.update(UPDATE_BY_ID_SQL,
                customer.getName().getName(),
                customer.getEmail().getEmail(),
                customer.getLastLoginAt() != null ? Timestamp.valueOf(customer.getLastLoginAt()) : null,
                customer.getCustomerId().toString().getBytes()
        );
        if (update != 1) {
            throw new RuntimeException("Nothing was updated");
        }
        return customer;
    }

    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, customerRowMapper);
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(SELECT_BY_ID_SQL,
                            customerRowMapper,
                            customerId.toString()));
        } catch (EmptyResultDataAccessException e) {
            log.error("Got empty result", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByName(Name name) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(SELECT_BY_NAME_SQL,
                            customerRowMapper,
                            name.getName()));
        } catch (EmptyResultDataAccessException e) {
            log.error("Got empty result", e);
            return Optional.empty();
        }
    }

    @Override
    public int count() {
        return jdbcTemplate.queryForObject(COUNT_SQL, Integer.class);
    }

    @Override
    public Optional<Customer> findByEmail(Email email) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(SELECT_BY_EMAIL_SQL,
                            customerRowMapper,
                            email.getEmail()));
        } catch (EmptyResultDataAccessException e) {
            log.error("Got empty result", e);
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update(DELETE_ALL_SQL);
    }
}
