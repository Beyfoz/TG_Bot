package org.example.bot;

import org.example.bot.ListOfCommands;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class TestOne {
    @Test
    public void StartTest() {
        ListOfCommands listCommands = new ListOfCommands();
        String message = listCommands.findCommand("/start");
        assertEquals(message, "Приветствую. Напиши /info, чтобы получить больше информации");
    }
    @Test
    public void HelpTest() {
        ListOfCommands commandsList = new ListOfCommands();
        String message = commandsList.findCommand("/help");
        assertTrue(message.contains("/start"));
        assertTrue(message.contains("/info"));
        assertTrue(message.contains("/help"));
        assertTrue(message.contains("/authors"));
    }
}