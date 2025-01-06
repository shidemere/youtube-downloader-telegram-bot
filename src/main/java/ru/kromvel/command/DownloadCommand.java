package ru.kromvel.command;

import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kromvel.exception.ProcessingCommandException;
import ru.kromvel.service.TelegramBot;
import ru.kromvel.service.VideoDownloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DownloadCommand implements BotCommand {

    private final TelegramBot bot;
    private final VideoDownloader downloader;
    @Value("${default.image}")
    private String defaultImageUrl;

    @Override
    public void execute(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            log.info("Received message from {} with payload: {}", update.getMessage().getChat().getFirstName(), update.getMessage().getText());

            String videoURL = getVideoURL(update);
            if (videoURL == null) {
                return;
            }

            InputStream videoPreview = getVideoPreview(videoURL);
            SendPhoto photo = SendPhoto.builder()
                    .chatId(update.getMessage().getChatId())
                    .photo(new InputFile(videoPreview, "Preview"))
                    .caption("Выберите формат скачивания")
                    .replyMarkup(InlineKeyboardMarkup
                            .builder()
                            .keyboardRow(
                                    List.of(InlineKeyboardButton
                                                    .builder()
                                                    .text("Video")
                                                    .callbackData("video_format:" + videoURL)
                                                    .build(),
                                            InlineKeyboardButton.builder()
                                                    .text("Audio")
                                                    .callbackData("audio_format:" + videoURL)
                                                    .build()
                                    )
                            )
                            .build())
                    .build();

                bot.executeAsync(photo);
        } else if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            if (data.startsWith("video_format")) {
                String link = data.substring("video_format:".length());
                VideoInfo videoInfoById = downloader.getVideoInfoById(link);
                Response<File> fileResponse = downloader.downloadVideo(videoInfoById);
                SendVideo video = SendVideo.builder()
                        .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                        .video(new InputFile(fileResponse.data()))
                        .caption(videoInfoById.details().title())
                        .build();
                bot.executeAsync(video);
            } else if (data.startsWith("audio_format")) {
                String link = data.substring("audio_format:".length());
                VideoInfo videoInfoById = downloader.getVideoInfoById(link);
                String title = videoInfoById.details().title();
                Response<File> fileResponse = downloader.downloadAudio(videoInfoById);
                SendAudio audio = SendAudio.builder()
                        .title(title)
                        .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                        .audio(new InputFile(fileResponse.data()))
                        .build();
                bot.executeAsync(audio);
            }
        } else {
            log.info("Received message from {} with unknown payload: {}", update.getMessage().getChat().getFirstName(), update.getMessage().getText());
            SendMessage incorrect = SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text("Некорректный ввод, попробуй еще раз.")
                    .build();
            try {
                bot.executeAsync(incorrect);
            } catch (TelegramApiException e) {
                throw new ProcessingCommandException(e.getMessage());
            }
        }
    }

    @SneakyThrows
    private String getVideoURL(Update update) {
        // Валидация
        String[] tokens = update.getMessage().getText().split(" ");
        log.info("After splitting input line was subtracted next tokens: {}", Arrays.toString(tokens));
        Optional<String> link = Arrays.stream(tokens).filter(it -> it.startsWith("http")).findFirst();
        if (link.isEmpty()) {
            SendMessage message = SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text("По данному URL не найдено видео")
                    .build();
            bot.execute(message);
            return null;
        }
        // Получение превью, для дальнейшего выбора формата скачивания
        String[] split = link.get().split("=");
        return split[1];
    }

    @SneakyThrows
    private InputStream getVideoPreview(String videoURL) {
        VideoInfo videoInfoById = downloader.getVideoInfoById(videoURL);
        String imageUrl = videoInfoById.details().thumbnails().stream().findFirst().orElse(defaultImageUrl);
        return new BufferedInputStream(new URL(imageUrl).openStream());
    }
}
