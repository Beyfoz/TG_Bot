package org.example.bot.commands;

public class CityCommand extends AbstractCommand {
    @Override
    public String getDescription() {
        return "Погода по названию города";
    }
    public String getMessage() {
        return "Введите название города, чтобы получить прогноз погоды.";
    }
}
