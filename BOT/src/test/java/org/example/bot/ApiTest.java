package org.example.bot;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiTest {

    private API api;
    private CloseableHttpClient mockHttpClient;

    @BeforeEach
    void setUp() {
        api = new API();
        mockHttpClient = mock(CloseableHttpClient.class);
    }
    @Test
    void testGetCoordinates_Success() throws IOException {
        String mockGeoResponse = "[{\"lat\":55.625578,\"lon\":37.6063916}]";
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        when(mockResponse.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(mockResponse.getStatusLine().getStatusCode()).thenReturn(200);
        when(mockResponse.getEntity()).thenReturn(new StringEntity(mockGeoResponse));

        // Мокаем HTTP клиент
        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockResponse);

        double[] coordinates = api.getCoordinates("Москва");
        assertNotNull(coordinates);
        assertEquals(55.625578, coordinates[0], 0.0001); // Допуск для сравнения
        assertEquals(37.6063916, coordinates[1], 0.0001); // Допуск для сравнения
    }
}
