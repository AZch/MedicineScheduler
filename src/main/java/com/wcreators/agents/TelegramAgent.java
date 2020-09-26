package com.wcreators.agents;

import com.wcreators.common.annotations.InjectProperty;
import com.wcreators.common.annotations.PostConstruct;
import com.wcreators.common.annotations.Singleton;
import com.wcreators.task.Task;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Singleton
public class TelegramAgent extends TelegramLongPollingBot implements Agent {
    @InjectProperty("TELEGRAM_API_TOKEN")
    private String token;

    @InjectProperty("TELEGRAM_BOT_USERNAME")
    private String username;

    @PostConstruct
    public void init() {
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendEvent(Task task) {

    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(update.getMessage().getText());
            System.out.println(message);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
