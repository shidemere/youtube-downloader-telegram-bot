package ru.kromvel.service;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.YoutubeCallback;
import com.github.kiulian.downloader.downloader.YoutubeProgressCallback;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoDetails;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.AudioFormat;
import com.github.kiulian.downloader.model.videos.formats.Format;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Slf4j

@Component
@RequiredArgsConstructor
public class VideoDownloader {

    private final YoutubeDownloader youtubeDownloader;

    public VideoInfo getVideoInfoById(String videoId) {
        RequestVideoInfo request = new RequestVideoInfo(videoId)
                .callback(new YoutubeCallback<>() {
                    @Override
                    public void onFinished(VideoInfo videoInfo) {
                        System.out.println("Finished parsing");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("Error: " + throwable.getMessage());
                    }
                })
                .async();
        Response<VideoInfo> response = youtubeDownloader.getVideoInfo(request);

        return response.data();
    }

    public Response<File> downloadVideo(VideoInfo videoInfo) {

        List<VideoFormat> videoFormats = videoInfo.videoFormats();
        VideoFormat videoFormat = videoFormats.get(0);



        RequestVideoFileDownload request = new RequestVideoFileDownload(videoFormat)
                .callback(new YoutubeProgressCallback<>() {
                    @Override
                    public void onDownloading(int progress) {
                        System.out.printf("Downloaded %d%%\n", progress);
                    }

                    @Override
                    public void onFinished(File videoInfo) {
                        System.out.println("Finished file: " + videoInfo);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("Error: " + throwable.getLocalizedMessage());
                    }
                })
                .async();

        return youtubeDownloader.downloadVideoFile(request);
    }


    public Response<File> downloadAudio(VideoInfo videoInfo) {
        AudioFormat audioFormat = videoInfo.bestAudioFormat();
        RequestVideoFileDownload download = new RequestVideoFileDownload(audioFormat)
                .callback(new YoutubeProgressCallback<>() {
                    @Override
                    public void onDownloading(int progress) {
                        System.out.printf("Downloaded %d%%\n", progress);
                    }

                    @Override
                    public void onFinished(File videoInfo) {
                        System.out.println("Finished file: " + videoInfo);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("Error: " + throwable.getLocalizedMessage());
                    }
                })
                .async();

        return youtubeDownloader.downloadVideoFile(download);
    }
}
