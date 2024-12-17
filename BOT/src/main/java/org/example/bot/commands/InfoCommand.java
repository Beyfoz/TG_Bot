package org.example.bot.commands;

public class InfoCommand extends AbstractCommand{

    public String getDescription(){
        return "Информаци о боте";
    }

    public String getMessage(){
        return "Привет, я бот погоды, я помогу узнать погоду в любом городе в любое время, " +
                "а также напишу самую популярную новость этого города\n" +
                "Пропиши /help чтобы узнать мои команды.";
    }
}