package org.example.bot;

import org.example.bot.commands.AuthorsCommand;
import org.example.bot.commands.InfoCommand;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestOne {

    @Test
    public void StartTest() {
        ListOfCommands listCommands = new ListOfCommands();
        String message = listCommands.findCommand("/start");
        assertEquals("Приветствую. Напиши /info, чтобы получить больше информации", message);
    }

    @Test
    public void HelpTest() {
        ListOfCommands commandsList = new ListOfCommands();
        String message = commandsList.findCommand("/help");
        assertTrue(message.contains("/start"));
        assertTrue(message.contains("/info"));
        assertTrue(message.contains("/help"));
        assertTrue(message.contains("/authors"));
        assertTrue(message.contains("/pashalko"));
        assertTrue(message.contains("/poglock"));
        assertTrue(message.contains("/bolshiegoroda"));
    }

    @Test
    public void InfoCommandTest() {
        // Создаем экземпляр команды InfoCommand
        InfoCommand infoCommand = new InfoCommand();

        // Проверяем описание команды
        assertEquals("Информаци о боте", infoCommand.getDescription());

        // Проверяем сообщение команды
        String expectedMessage = "Привет, я бот погоды, я помогу узнать погоду в любом городе в любое время, а также напишу самую популярную новость этого города\n" +
                "Пропиши /help чтобы узнать мои команды.";
        assertEquals(expectedMessage, infoCommand.getMessage());
    }

    @Test
    public void AuthorsCommandTest() {
        // Создаем экземпляр команды AuthorsCommand
        AuthorsCommand authorsCommand = new AuthorsCommand();

        // Проверяем описание команды
        assertEquals("Информация о разработчиках", authorsCommand.getDescription());

        // Проверяем сообщение команды
        String expectedMessage = "Авторами проекта являются студенты 2-го курса специалитета \"Компьютерная безопасность\" Ахметчин Ярослав и Батков Сергей";
        assertEquals(expectedMessage, authorsCommand.getMessage());
    }
}
