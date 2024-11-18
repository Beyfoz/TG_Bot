package org.example.bot.commands;

public class AuthorsCommand extends AbstractCommand {

    public String getDescription() {
        return "Информация о разработчиках";
    }

    public String getMessage() {
        return "Авторами проекта являются студенты 2-го курса специалитета \"Компьютерная безопасность\" Ахметчин Ярослав и Батков Сергей";
    }
}