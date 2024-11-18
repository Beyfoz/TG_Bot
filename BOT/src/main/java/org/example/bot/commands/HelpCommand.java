package org.example.bot.commands;

import java.util.HashMap;
import java.util.Map;

public class HelpCommand extends AbstractCommand {

    private final HashMap<String, AbstractCommand> actionCommands;

    public HelpCommand(HashMap<String, AbstractCommand> actionCommands) {
        this.actionCommands = actionCommands;
    }

    public String getDescription() {
        return "Список команд";
    }

    public String getMessage() {
        StringBuilder msg = new StringBuilder("Вот список доступных мне команд:\n");
        for (Map.Entry<String, AbstractCommand> entry : actionCommands.entrySet()) {
            String commandText = entry.getKey();
            String description = entry.getValue().getDescription();
            msg.append(commandText).append(" - ").append(description).append("\n");
        }
        return msg.toString();
    }
}