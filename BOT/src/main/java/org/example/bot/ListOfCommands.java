package org.example.bot;

import org.example.bot.commands.*;
import java.util.HashMap;

public class ListOfCommands {
    private final HashMap<String, AbstractCommand> commandHashMap = new HashMap<String, AbstractCommand>();

    public ListOfCommands() {
        commandHashMap.put("/info", new InfoCommand());
        commandHashMap.put("/start", new StartCommand());
        commandHashMap.put("/authors", new AuthorsCommand());
        commandHashMap.put("/help", new HelpCommand(commandHashMap));
        commandHashMap.put("/pogoda", new PogodaCommand());
        commandHashMap.put("/pashalko", new PrivetCommand());
    }


    public String findCommand(String text) {
        if (commandHashMap.containsKey(text)) {
            commandHashMap.get(text);
            return commandHashMap.get(text).getMessage();
        }
        return "Command Not Found";
    }
}