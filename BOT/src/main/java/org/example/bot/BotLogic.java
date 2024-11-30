package org.example.bot;

import org.telegram.telegrambots.meta.api.objects.Location;

import java.util.HashMap;
import java.util.Map;

public class BotLogic {
    private final ListOfCommands commandsList;
    private final API api;
    private final Map<String, Boolean> userAwaitingLocation;
    private final Map<String, Boolean> userAwaitingCity;

    public BotLogic() {
        this.commandsList = new ListOfCommands();
        this.api = new API();
        this.userAwaitingLocation = new HashMap<>();
        this.userAwaitingCity = new HashMap<>();
    }

    public String handleUserMessage(String userId, String userMessage, Location location) {
        if (userAwaitingLocation.getOrDefault(userId, false) && location != null) {
            userAwaitingLocation.put(userId, false);

            Coordinates coordinates = new Coordinates(location.getLatitude(), location.getLongitude());
            return api.fetchWeatherForecast(coordinates);
        }


        if (userAwaitingCity.getOrDefault(userId, false)) {
            userAwaitingCity.put(userId, false);
            return api.fetchWeatherByCity(userMessage.trim());
        }

        if (userMessage.startsWith("/pogoda")) {
            userAwaitingLocation.put(userId, true);
            return "Пожалуйста, отправьте свою геолокацию.";
        } else if (userMessage.startsWith("/city")) {
            userAwaitingCity.put(userId, true);
            return "Пожалуйста, введите название города.";
        } else {
            return commandsList.findCommand(userMessage);
        }
    }
}
