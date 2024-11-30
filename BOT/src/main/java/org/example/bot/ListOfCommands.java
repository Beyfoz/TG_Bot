package org.example.bot;

import org.example.bot.commands.*;
import java.util.HashMap;

public class ListOfCommands {
    private final HashMap<String, AbstractCommand> commandHashMap = new HashMap<>();

    public ListOfCommands() {
        commandHashMap.put("/info", new InfoCommand());
        commandHashMap.put("/start", new StartCommand());
        commandHashMap.put("/authors", new AuthorsCommand());
        commandHashMap.put("/help", new HelpCommand(commandHashMap));
        commandHashMap.put("/poglock", new PogodaCommand());
        commandHashMap.put("/bolshiegoroda", new CityCommand());
        commandHashMap.put("/pashalko", new PrivetCommand());
    }

    public String findCommand(String text) {
        if (commandHashMap.containsKey(text)) {
            return commandHashMap.get(text).getMessage();
        }
        return "Команда не найдена";
    }
}
