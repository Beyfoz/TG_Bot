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
        // Check if the bot is waiting for location from the user
        if (userAwaitingLocation.getOrDefault(userId, false) && location != null) {
            userAwaitingLocation.put(userId, false); // Reset waiting state
            // Create a Coordinates object from location
            Coordinates coordinates = new Coordinates(location.getLatitude(), location.getLongitude());
            return api.fetchWeatherForecast(coordinates);
        }

        // Check if the bot is waiting for city name from the user
        if (userAwaitingCity.getOrDefault(userId, false)) {
            userAwaitingCity.put(userId, false); // Reset waiting state
            return api.fetchWeatherByCity(userMessage.trim());
        }

        // Command handling
        if (userMessage.startsWith("/pogoda")) {
            userAwaitingLocation.put(userId, true); // Set waiting state for location
            return "Пожалуйста, отправьте свою геолокацию.";
        } else if (userMessage.startsWith("/city")) {
            userAwaitingCity.put(userId, true); // Set waiting state for city name
            return "Пожалуйста, введите название города.";
        } else {
            return commandsList.findCommand(userMessage);
        }
    }
}
