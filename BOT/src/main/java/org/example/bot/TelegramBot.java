package org.example.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot extends TelegramLongPollingBot {
    private final BotLogic botLogic;

    public TelegramBot() {
        this.botLogic = new BotLogic();
    }

    @Override
    public String getBotUsername() {
        return "YOUR_BOT_USERNAME"; // Замените на имя вашего бота
    }

    @Override
    public String getBotToken() {
        return System.getenv("Token"); // Токен хранится в переменной окружения
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                Message inMessage = update.getMessage();
                String chatId = inMessage.getChatId().toString();

                if (inMessage.hasText()) {
                    String userMessage = inMessage.getText();
                    String responseMessage = botLogic.handleUserMessage(chatId, userMessage, null);
                    sendMessage(chatId, responseMessage);
                } else if (inMessage.hasLocation()) {
                    Location location = inMessage.getLocation();
                    String responseMessage = botLogic.handleUserMessage(chatId, null, location);
                    sendMessage(chatId, responseMessage);
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String chatId, String messageText) throws TelegramApiException {
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(chatId);
        outMessage.setText(messageText);
        execute(outMessage);
    }
}
