package ru.kromvel.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kromvel.exception.ProcessingCommandException;
import ru.kromvel.service.TelegramBot;

@Slf4j
@RequiredArgsConstructor
@Component
public class InfoCommand implements BotCommand {

    private final TelegramBot bot;

    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        // todo Переделай, чтобы ссылку какую угодно можно было сделать.
        String responseText = "Отправь мне ссылку на видео, в формате https://www.youtube.com/watch?v=УНИКАЛЬНЫЙ_ID";

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(responseText);

        try {
            bot.execute(message);
        } catch (Exception e) {
            log.error("Error in info command: {}", e.getMessage());
            throw new ProcessingCommandException(e.getMessage());
        }
    }
}
