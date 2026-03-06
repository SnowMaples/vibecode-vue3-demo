package cloud.lazycat.app.crud.repository;

import cloud.lazycat.app.crud.model.Item;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ItemRepository {
    private final JdbcTemplate jdbcTemplate;
    
    public ItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        initializeDatabase();
    }
    
    private void initializeDatabase() {
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT,
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL
            )
        """;
        jdbcTemplate.execute(createTableSql);
    }
    
    private RowMapper<Item> itemRowMapper = (ResultSet rs, int rowNum) -> {
        return new Item(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("description"),
            LocalDateTime.parse(rs.getString("created_at")),
            LocalDateTime.parse(rs.getString("updated_at"))
        );
    };
    
    public List<Item> findAll() {
        String sql = "SELECT * FROM items ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, itemRowMapper);
    }
    
    public Optional<Item> findById(Long id) {
        String sql = "SELECT * FROM items WHERE id = ?";
        try {
            Item item = jdbcTemplate.queryForObject(sql, itemRowMapper, id);
            return Optional.ofNullable(item);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public Item save(Item item) {
        if (item.getId() == null) {
            return create(item);
        } else {
            return update(item);
        }
    }
    
    private Item create(Item item) {
        String sql = """
            INSERT INTO items (name, description, created_at, updated_at) 
            VALUES (?, ?, ?, ?)
        """;
        
        jdbcTemplate.update(sql,
            item.getName(),
            item.getDescription(),
            item.getCreatedAt().toString(),
            item.getUpdatedAt().toString()
        );
        
        Long generatedId = jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
        item.setId(generatedId);
        return item;
    }
    
    private Item update(Item item) {
        String sql = """
            UPDATE items 
            SET name = ?, description = ?, updated_at = ? 
            WHERE id = ?
        """;
        
        jdbcTemplate.update(sql,
            item.getName(),
            item.getDescription(),
            item.getUpdatedAt().toString(),
            item.getId()
        );
        
        return item;
    }
    
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM items WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }
}