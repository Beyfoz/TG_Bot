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
import static org.mockito.ArgumentMatchers.any;
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
    void testFetchWeatherForecast_Success() throws IOException {
        String mockGeoResponse = "[{\"lat\":55.625578,\"lon\":37.6063916}]";
        String mockWeatherResponse = "{\"fact\":{\"temp\":20,\"condition\":\"clear\"}}";

        CloseableHttpResponse mockGeoResponseEntity = mock(CloseableHttpResponse.class);
        StatusLine mockGeoStatusLine = mock(StatusLine.class);
        when(mockGeoStatusLine.getStatusCode()).thenReturn(200);
        when(mockGeoResponseEntity.getStatusLine()).thenReturn(mockGeoStatusLine);
        when(mockGeoResponseEntity.getEntity()).thenReturn(new StringEntity(mockGeoResponse));

        CloseableHttpResponse mockWeatherResponseEntity = mock(CloseableHttpResponse.class);
        StatusLine mockWeatherStatusLine = mock(StatusLine.class);
        when(mockWeatherStatusLine.getStatusCode()).thenReturn(200);
        when(mockWeatherResponseEntity.getStatusLine()).thenReturn(mockWeatherStatusLine);
        when(mockWeatherResponseEntity.getEntity()).thenReturn(new StringEntity(mockWeatherResponse));

        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockGeoResponseEntity).thenReturn(mockWeatherResponseEntity);

        String weatherData = api.fetchWeatherForecast("Москва");
        assertNotNull(weatherData);
    }

    @Test
    void testFetchWeatherForecast_CoordinatesNotFound() throws IOException {
        String mockGeoResponse = "[]"; // No coordinates found

        CloseableHttpResponse mockGeoResponseEntity = mock(CloseableHttpResponse.class);
        StatusLine mockGeoStatusLine = mock(StatusLine.class);
        when(mockGeoStatusLine.getStatusCode()).thenReturn(200);
        when(mockGeoResponseEntity.getStatusLine()).thenReturn(mockGeoStatusLine);
        when(mockGeoResponseEntity.getEntity()).thenReturn(new StringEntity(mockGeoResponse));

        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockGeoResponseEntity);

        String weatherData = api.fetchWeatherForecast("НеизвестныйГород"); // Assuming this method exists
        assertEquals("Не удалось найти координаты для города: НеизвестныйГород", weatherData);
    }

    @Test
    void testFetchWeatherForecast_WeatherDataNotFound() throws IOException {
        String mockGeoResponse = "[{\"lat\":55.625578,\"lon\":37.6063916}]";

        // Мокируем ответ геолокационного API
        CloseableHttpResponse mockGeoResponseEntity = mock(CloseableHttpResponse.class);
        StatusLine mockGeoStatusLine = mock(StatusLine.class);
        when(mockGeoStatusLine.getStatusCode()).thenReturn(200);
        when(mockGeoResponseEntity.getStatusLine()).thenReturn(mockGeoStatusLine);
        when(mockGeoResponseEntity.getEntity()).thenReturn(new StringEntity(mockGeoResponse));

        // Мокируем ответ API погоды с ошибкой 403
        CloseableHttpResponse mockWeatherResponseEntity = mock(CloseableHttpResponse.class);
        StatusLine mockWeatherStatusLine = mock(StatusLine.class);
        when(mockWeatherStatusLine.getStatusCode()).thenReturn(403); // Симулируем ошибку доступа
        when(mockWeatherResponseEntity.getStatusLine()).thenReturn(mockWeatherStatusLine);

        // Настраиваем мок для HTTP клиента
        when(mockHttpClient.execute(any(HttpGet.class)))
                .thenReturn(mockGeoResponseEntity)
                .thenReturn(mockWeatherResponseEntity);

        // Выполняем метод и проверяем результат
        String weatherData = api.fetchWeatherForecast("1237612");
        assertEquals("Не удалось найти координаты для города: 1237612", weatherData);
    }
}
