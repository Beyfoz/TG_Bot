package org.example.bot;

import org.example.bot.commands.PogodaCommand;
import org.example.bot.ListOfCommands;
import org.example.bot.API;

import java.util.HashMap;
import java.util.Map;

public class BotLogic {
    private final ListOfCommands commandsList;
    private final API api;
    private final Map<String, Boolean> userAwaitingCity; // Хранит состояние ожидания для каждого пользователя

    public BotLogic() {
        this.commandsList = new ListOfCommands();
        this.api = new API();
        this.userAwaitingCity = new HashMap<>(); // Инициализация карты состояния
    }

    public String handleUserMessage(String userId, String userMessage) {
        // Проверка, ожидает ли бот название города от пользователя
        if (userAwaitingCity.getOrDefault(userId, false)) {
            String city = userMessage.trim();
            userAwaitingCity.put(userId, false); // Сброс состояния ожидания
            return api.fetchWeatherForecast(city);
        }

        if (userMessage.startsWith("/pogoda")) {
            userAwaitingCity.put(userId, true); // Установка состояния ожидания
            return "Пожалуйста, введите название города:";
        } else {
            return commandsList.findCommand(userMessage);
        }
    }
}
