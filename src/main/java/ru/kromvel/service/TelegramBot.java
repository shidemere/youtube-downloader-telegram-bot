package ru.kromvel.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kromvel.handler.CommandHandler;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;
    private final CommandHandler commandHandler;

    public TelegramBot(
            DefaultBotOptions options,
            @Value("${bot.name}") String botUsername,
            @Value("${bot.token}") String botToken,
            CommandHandler commandHandler
    ) {
        super(options);
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.commandHandler = commandHandler;
    }


    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        commandHandler.registerCommand(this);
        commandHandler.handle(update);
    }
}
