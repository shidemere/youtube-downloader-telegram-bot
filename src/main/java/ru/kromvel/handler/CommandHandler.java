package ru.kromvel.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kromvel.command.BotCommand;
import ru.kromvel.command.DownloadCommand;
import ru.kromvel.command.InfoCommand;
import ru.kromvel.command.StartCommand;
import ru.kromvel.service.TelegramBot;
import ru.kromvel.service.VideoDownloader;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CommandHandler {


    private final Map<String, BotCommand> commands = new HashMap<>();
    private final VideoDownloader videoDownloader;


    public CommandHandler(VideoDownloader downloader) {
        this.videoDownloader = downloader;
    }

    public void registerCommand(TelegramBot bot) {
        commands.put("/start", new StartCommand(bot));
        commands.put("/info", new InfoCommand(bot));
        commands.put("/default", new DownloadCommand(bot, videoDownloader));
    }

    public void handle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String command = update.getMessage().getText();
            BotCommand commandToExecute = commands.get(command);
            if (commandToExecute != null) {
                commandToExecute.execute(update);
            } else {
                commands.get("/default").execute(update);
            }
        } else if (update.hasCallbackQuery()) {
            commands.get("/default").execute(update);
        }
    }
}
