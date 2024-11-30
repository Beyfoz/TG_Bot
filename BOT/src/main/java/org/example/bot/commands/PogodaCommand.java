package org.example.bot.commands;

public class PogodaCommand extends AbstractCommand {
    public String getDescription() {
        return "Погода по геолокации";
    }

    public String getMessage() {
        return "Пожалуйста, укажите название города после команды.\nПример: /pogoda Москва";
    }
}