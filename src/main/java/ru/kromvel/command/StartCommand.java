package ru.kromvel.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kromvel.exception.ProcessingCommandException;
import ru.kromvel.service.TelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartCommand implements BotCommand {
    private final TelegramBot bot;


    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        String responseText = "Привет! Это бот, для скачивания видео из YouTube. Пришли мне видео и я пришлю тебе видео.";

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(responseText);

        try {
            bot.execute(message);
        } catch (Exception e) {
            log.error("Error in greeting command: {}", e.getMessage());
            throw new ProcessingCommandException(e.getMessage());
        }
    }
}
