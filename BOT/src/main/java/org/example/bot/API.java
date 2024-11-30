package org.example.bot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class API {
    private static final String API_KEY = System.getenv("AKEY");
    private static final String WEATHER_API_URL_BASE = "https://api.weather.yandex.ru/v2/forecast?lat=%s&lon=%s";
    private static final String GEOCODING_API_URL = "https://nominatim.openstreetmap.org/search?q=%s&format=json&limit=1";

    // Метод для получения прогноза погоды по координатам
    public String fetchWeatherForecast(Coordinates coordinates) {
        String apiUrl = String.format(WEATHER_API_URL_BASE, coordinates.getLatitude(), coordinates.getLongitude());
        return fetchWeatherData(apiUrl);
    }

    // Метод для получения прогноза погоды по названию города
    public String fetchWeatherByCity(String cityName) {
        Coordinates coordinates = getCoordinates(cityName);
        if (coordinates == null) {
            return "Не удалось найти координаты для города: " + cityName;
        }
        return fetchWeatherForecast(coordinates);
    }

    // Метод для получения координат города
    private Coordinates getCoordinates(String city) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = String.format(GEOCODING_API_URL, city.replace(" ", "%20"));
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    return parseCoordinatesResponse(jsonResponse);
                } else {
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Метод для парсинга ответа с координатами
    private Coordinates parseCoordinatesResponse(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode arrayNode = objectMapper.readTree(jsonResponse);
            if (arrayNode.isArray() && arrayNode.size() > 0) {
                double lat = arrayNode.get(0).path("lat").asDouble();
                double lon = arrayNode.get(0).path("lon").asDouble();
                return new Coordinates(lat, lon);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Метод для получения данных о погоде
    private String fetchWeatherData(String apiUrl) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl);
            request.addHeader("X-Yandex-Weather-Key", API_KEY);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    return parseWeatherResponse(jsonResponse);
                } else {
                    return "Не удалось получить данные о погоде. Статус: " + response.getStatusLine().getStatusCode();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка при получении данных о погоде.";
        }
    }

    // Метод для парсинга ответа о погоде
    private String parseWeatherResponse(String jsonResponse) {
        StringBuilder weatherBuilder = new StringBuilder();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            JsonNode factNode = rootNode.path("fact");
            String description = factNode.path("condition").asText();
            double temperature = factNode.path("temp").asDouble();

            weatherBuilder.append("Температура: ").append(String.format("%.2f", temperature)).append(" °C\n");
            weatherBuilder.append("Описание: ").append(description).append("\n");

            JsonNode forecastsNode = rootNode.path("forecasts");
            if (forecastsNode.isArray() && !forecastsNode.isEmpty()) {
                for (JsonNode forecastNode : forecastsNode) {
                    String date = forecastNode.path("date").asText();
                    double dayTemp = forecastNode.path("parts").path("day").path("temp_avg").asDouble();
                    String dayCondition = forecastNode.path("parts").path("day").path("condition").asText();

                    weatherBuilder.append("Дата: ").append(date).append("\n");
                    weatherBuilder.append("Средняя температура: ").append(String.format("%.2f", dayTemp)).append(" °C\n");
                    weatherBuilder.append("Условия: ").append(dayCondition).append("\n\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка при обработке данных о погоде.";
        }
        return weatherBuilder.toString();
    }
}
