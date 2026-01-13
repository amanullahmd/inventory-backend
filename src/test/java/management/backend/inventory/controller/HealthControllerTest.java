package management.backend.inventory.controller;

import management.backend.inventory.controller.HealthController.HealthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for HealthController
 * Tests health check endpoint functionality
 */
@ExtendWith(MockitoExtension.class)
class HealthControllerTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @InjectMocks
    private HealthController healthController;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
    }

    @Test
    @DisplayName("Health check returns UP when database is connected")
    void health_ReturnsUp_WhenDatabaseConnected() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isClosed()).thenReturn(false);

        // Act
        ResponseEntity<HealthResponse> response = healthController.health();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().getStatus());
        assertNotNull(response.getBody().getMessage());
        assertTrue(response.getBody().getMessage().contains("healthy"));
        assertNotNull(response.getBody().getDetails());
        assertEquals("UP", response.getBody().getDetails().get("status"));
        
        verify(dataSource).getConnection();
        verify(connection).isClosed();
        verify(connection).close();
    }

    @Test
    @DisplayName("Health check returns DOWN when database connection fails")
    void health_ReturnsDown_WhenDatabaseConnectionFails() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection refused"));

        // Act
        ResponseEntity<HealthResponse> response = healthController.health();

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DOWN", response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Database connection failed"));
        assertNotNull(response.getBody().getDetails());
        assertEquals("DOWN", response.getBody().getDetails().get("status"));
        
        verify(dataSource).getConnection();
    }

    @Test
    @DisplayName("Health check returns DOWN when connection is closed")
    void health_ReturnsDown_WhenConnectionIsClosed() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isClosed()).thenReturn(true);

        // Act
        ResponseEntity<HealthResponse> response = healthController.health();

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DOWN", response.getBody().getStatus());
        
        verify(dataSource).getConnection();
        verify(connection).isClosed();
    }

    @Test
    @DisplayName("Health check returns DOWN when connection is null")
    void health_ReturnsDown_WhenConnectionIsNull() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(null);

        // Act
        ResponseEntity<HealthResponse> response = healthController.health();

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DOWN", response.getBody().getStatus());
        
        verify(dataSource).getConnection();
    }

    @Test
    @DisplayName("HealthResponse contains all required fields")
    void healthResponse_ContainsAllFields() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isClosed()).thenReturn(false);

        // Act
        ResponseEntity<HealthResponse> response = healthController.health();

        // Assert
        HealthResponse body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.getStatus());
        assertTrue(body.getTimestamp() > 0);
        assertNotNull(body.getMessage());
        assertNotNull(body.getDetails());
        assertTrue(body.getDetails().containsKey("database"));
        assertEquals("PostgreSQL", body.getDetails().get("database"));
    }
}
