package org.example.bot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class API {
    private static final String WEATHER_API_KEY = "A_key"; // API ключ для погоды
    private static final String NEWS_API_KEY = "N_key"; //  API ключ для новостей
    private static final URI WEATHER_API_URL_BASE;
    private static final URI GEOCODING_API_URL;
    private static final URI NEWS_API_URL;
    private static List<String> cities;


    static {
        try {
            WEATHER_API_URL_BASE = new URI("https", "api.weather.yandex.ru", "/v2/forecast", null);
            GEOCODING_API_URL = new URI("https", "nominatim.openstreetmap.org", "/search", null);
            NEWS_API_URL = new URI("https", "newsapi.org", "/v2/everything", null);
            loadCities();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Ошибка при создании URI", e);
        }
    }

    private static void loadCities() {
        cities = new ArrayList<>();
        try (InputStream inputStream = API.class.getResourceAsStream("/cities.txt")) {
            if (inputStream == null) {
                throw new FileNotFoundException("Файл cities.txt не найден в ресурсах.");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        cities.add(line.trim());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String fetchWeatherByCity(String cityName) {
        Coordinates coordinates = getCoordinates(cityName);
        if (coordinates == null) {
            List<String> suggestions = findSimilarCities(cityName);
            return "Не удалось найти координаты для города: " + cityName +
                    (suggestions.isEmpty() ? "" : ". Возможно, вы имели в виду: " + String.join(", ", suggestions));
        }
        String weatherForecast = fetchWeatherForecast(coordinates);
        String news = fetchCityNews(cityName);
        return weatherForecast + "\n\n" + news; // Объединяем прогноз погоды и новости
    }

    List<String> findSimilarCities(String input) {
        List<String> similarCities = new ArrayList<>();
        String lowerCaseInput = input.toLowerCase();

        // Сначала ищем совпадения по подстроке
        for (String city : cities) {
            if (city.toLowerCase().contains(lowerCaseInput)) {
                similarCities.add(city);
            }
        }

        // Если не нашли совпадений, используем расстояние Левенштейна
        if (similarCities.isEmpty()) {
            for (String city : cities) {
                if (getLevenshteinDistance(city.toLowerCase(), lowerCaseInput) <= 2) {
                    similarCities.add(city);
                }
            }
        }

        return similarCities;
    }

    // Метод для вычисления расстояния Левенштейна
    int getLevenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j; // Если a пустая
                } else if (j == 0) {
                    dp[i][j] = i; // Если b пустая
                } else {
                    int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
                }
            }
        }
        return dp[a.length()][b.length()];
    }

    public String fetchWeatherForecast(Coordinates coordinates) {
        try {
            URI apiUrl = new URI(WEATHER_API_URL_BASE.toString() + "?lat=" + coordinates.getLatitude() + "&lon=" + coordinates.getLongitude());
            return fetchWeatherData(apiUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "Ошибка при создании URL для прогноза погоды.";
        }
    }

    private Coordinates getCoordinates(String city) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URI url = new URI(GEOCODING_API_URL.toString() + "?q=" + city.replace(" ", "%20") + "&format=json&limit=1");

            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    return parseCoordinatesResponse(jsonResponse);
                } else {
                    return null;
                }
            }
        } catch (IOException | URISyntaxException e) {
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

    private String fetchWeatherData(URI apiUrl) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl);
            request.addHeader("X-Yandex-Weather-Key", WEATHER_API_KEY);

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

    private String parseWeatherResponse(String jsonResponse) {
        StringBuilder weatherBuilder = new StringBuilder();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            JsonNode factNode = rootNode.path("fact");
            String description = factNode.path("condition").asText();
            double temperature = factNode.path("temp").asDouble();

            // Генерация уведомления об изменении погоды
            String weatherChangeNotification = generateWeatherChangeNotification(temperature, description, rootNode);
            weatherBuilder.append(weatherChangeNotification);

            weatherBuilder.append("Температура: ").append(String.format("%.2f", temperature)).append(" °C\n");
            weatherBuilder.append("Описание: ").append(translateCondition(description)).append("\n");

            JsonNode forecastsNode = rootNode.path("forecasts");
            if (forecastsNode.isArray() && !forecastsNode.isEmpty()) {
                for (JsonNode forecastNode : forecastsNode) {
                    String date = forecastNode.path("date").asText();
                    double dayTemp = forecastNode.path("parts").path("day").path("temp_avg").asDouble();
                    String dayCondition = forecastNode.path("parts").path("day").path("condition").asText();

                    weatherBuilder.append("Дата: ").append(date).append("\n");
                    weatherBuilder.append("Средняя температура: ").append(String.format("%.2f", dayTemp)).append(" °C\n");
                    weatherBuilder.append("Условия: ").append(translateCondition(dayCondition)).append("\n\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка при обработке данных о погоде.";
        }
        return weatherBuilder.toString();
    }

    private String generateWeatherChangeNotification(double currentAverageTemperature, String currentCondition, JsonNode rootNode) {
        StringBuilder notification = new StringBuilder();

        // Получаем данные о погоде на завтра
        JsonNode forecastsNode = rootNode.path("forecasts");
        if (forecastsNode.isArray() && forecastsNode.size() > 1) {
            JsonNode todayForecast = forecastsNode.get(1);
            JsonNode tomorrowForecast = forecastsNode.get(2);

            double todayAverageTemperature = todayForecast.path("parts").path("day").path("temp_avg").asDouble(); // Средняя температура сегодня
            double tomorrowAverageTemperature = tomorrowForecast.path("parts").path("day").path("temp_avg").asDouble(); // Средняя температура завтра
            String tomorrowCondition = tomorrowForecast.path("parts").path("day").path("condition").asText(); // Состояние погоды на завтра

            // Сравнение средней температуры на сегодня с завтрашней средней температурой
            if (tomorrowAverageTemperature > todayAverageTemperature) {
                notification.append("Погода улучшится: температура повысится на ")
                        .append(tomorrowAverageTemperature - todayAverageTemperature)
                        .append(" °C завтра.\n");
            } else if (tomorrowAverageTemperature < todayAverageTemperature) {
                notification.append("Погода ухудшится: температура понизится на ")
                        .append(todayAverageTemperature - tomorrowAverageTemperature)
                        .append(" °C завтра.\n");
            } else {
                notification.append("Температура останется на уровне ")
                        .append(todayAverageTemperature)
                        .append(" °C завтра.\n");
            }

            if (tomorrowAverageTemperature < 0) {
                notification.append("Обратите внимание: завтрашняя температура будет ниже нуля.\n");
            }

            if (!tomorrowCondition.equals(currentCondition)) {
                notification.append("Состояние погоды изменится: ")
                        .append(translateCondition(currentCondition))
                        .append(" -> ")
                        .append(translateCondition(tomorrowCondition))
                        .append(".\n");
            } else {
                notification.append("Состояние погоды останется прежним: ")
                        .append(translateCondition(currentCondition))
                        .append(".\n");
            }
        }

        return notification.toString();
    }




    private String translateCondition(String condition) {
        switch (condition) {
            case "clear":
                return "ясно";
            case "partly-cloudy":
                return "переменная облачность";
            case "cloudy":
                return "облачно";
            case "overcast":
                return "пасмурно";
            case "drizzle":
                return "морось";
            case "light-rain":
                return "небольшой дождь";
            case "rain":
                return "дождь";
            case "moderate-rain":
                return "умеренный дождь";
            case "heavy-rain":
                return "сильный дождь";
            case "light-snow":
                return "небольшой снег";
            case "snow":
                return "снег";
            case "snow-showers":
                return "снегопад";
            case "hail":
                return "град";
            case "fog":
                return "туман";
            case "wind":
                return "ветрено";
            case "storm":
                return "шторм";
            case "hurricane":
                return "ураган";
            default:
                return "неизвестно";
        }
    }

    String fetchCityNews(String cityName) {
        try {
            LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            String fromDate = oneWeekAgo.format(formatter) + "Z"; // Добавляем Z для указания на UTC

            URI apiUrl = new URI(NEWS_API_URL.toString() + "?q=" + cityName + "&apiKey="
                    + NEWS_API_KEY + "&sortBy=popularity&from=" + fromDate);
            return fetchNewsData(apiUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "Ошибка при создании URL для получения новостей.";
        }
    }

    private String fetchNewsData(URI apiUrl) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    return parseNewsResponse(jsonResponse);
                } else {
                    return "Не удалось получить данные о новостях. Статус: " + response.getStatusLine().getStatusCode();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка при получении данных о новостях.";
        }
    }

    private String parseNewsResponse(String jsonResponse) {
        StringBuilder newsBuilder = new StringBuilder();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode articlesNode = rootNode.path("articles");

            if (articlesNode.isArray() && articlesNode.size() > 0) {
                JsonNode topArticle = articlesNode.get(0);
                String title = topArticle.path("title").asText();
                String url = topArticle.path("url").asText();
                String description = topArticle.path("description").asText();

                newsBuilder.append("Самая популярная новость:\n");
                newsBuilder.append("Заголовок: ").append(title).append("\n");
                newsBuilder.append("Описание: ").append(description).append("\n");
                newsBuilder.append("Ссылка: ").append(url).append("\n");
            } else {
                newsBuilder.append("Нет доступных новостей для этого города.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка при обработке данных о новостях.";
        }
        return newsBuilder.toString();
    }
}