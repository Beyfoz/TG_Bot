package org.example.bot;

import org.example.bot.commands.PogodaCommand;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "Goyda0911_bot"; // Укажите ваше имя бота
    }

    @Override
    public String getBotToken() {
        return "7841264677:AAFgkiHZSlfqViBMhB5mVzn_YWuFNGmEJ1I"; // Укажите ваш токен
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Message inMessage = update.getMessage();
                String chatId = inMessage.getChatId().toString();

                String userMessage = inMessage.getText();
                if (userMessage.startsWith("/pogoda")) {
                    // Проверяем, что длина userMessage больше 8 символов
                    if (userMessage.length() > 8) {
                        String city = userMessage.substring(8).trim(); // Извлекаем название города из сообщения
                        PogodaCommand pogodaCommand = new PogodaCommand(); // Создаем экземпляр PogodaCommand
                        API api = new API();
                        String weatherInfo = api.fetchWeatherForecast(city); // Получаем погоду для указанного города
                        sendMessage(chatId, weatherInfo);
                    } else {
                        // Если длина меньше или равна 8, отправляем сообщение с инструкцией
                        PogodaCommand pogodaCommand = new PogodaCommand(); // Создаем экземпляр PogodaCommand
                        sendMessage(chatId, pogodaCommand.getMessage()); // Вызываем getMessage() для получения сообщения
                    }
                } else {
                    ListOfCommands commandsList = new ListOfCommands();
                    String message = commandsList.findCommand(userMessage);
                    sendMessage(chatId, message);
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
