package org.example.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot extends TelegramLongPollingBot {
    private final BotLogic botLogic;

    public TelegramBot() {
        this.botLogic = new BotLogic();
    }

    @Override
    public String getBotUsername() {
        return "Goyda0911_bot";
    }

    @Override
    public String getBotToken() {
        return System.getenv("Token");
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Message inMessage = update.getMessage();
                String chatId = inMessage.getChatId().toString();
                String userMessage = inMessage.getText();

                String responseMessage = botLogic.handleUserMessage(chatId, userMessage);
                sendMessage(chatId, responseMessage);
                //System.out.println("Получено сообщение от пользователя: " + userMessage + " в чате: " + chatId);
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
