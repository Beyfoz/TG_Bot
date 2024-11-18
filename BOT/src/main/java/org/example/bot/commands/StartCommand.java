package org.example.bot.commands;

public class StartCommand extends AbstractCommand{

    public String getDescription(){
        return "Перезапуск бота";
    }

    public String getMessage(){
        return "Приветствую. Напиши /info, чтобы получить больше информации";
    }
}
