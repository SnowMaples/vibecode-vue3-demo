package cloud.lazycat.app.crud;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;

@Configuration
public class DatabaseInitializer {
    
    @Bean
    public CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            // Create tables if they don't exist
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "created_at TEXT NOT NULL, " +
                "updated_at TEXT NOT NULL)" 
            );
            
            // Check if table is empty
            Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM items", Long.class);
            if (count == 0) {
                // Insert sample data
                jdbcTemplate.batchUpdate("""
                    INSERT INTO items (name, description, created_at, updated_at) 
                    VALUES (?, ?, ?, ?)
                """, Arrays.asList(
                    new Object[]{"First Item", "This is the first item", "2024-01-01T10:00:00", "2024-01-01T10:00:00"},
                    new Object[]{"Second Item", "This is the second item", "2024-01-02T12:00:00", "2024-01-02T12:00:00"},
                    new Object[]{"Third Item", "This is the third item", "2024-01-03T15:00:00", "2024-01-03T15:00:00"}
                ));
            }
        };
    }
}