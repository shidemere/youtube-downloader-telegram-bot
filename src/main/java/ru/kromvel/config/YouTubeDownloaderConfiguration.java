package ru.kromvel.config;

import com.github.kiulian.downloader.Config;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.proxy.ProxyCredentials;
import com.github.kiulian.downloader.downloader.proxy.ProxyCredentialsImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

@Configuration
public class YouTubeDownloaderConfiguration {

    @Bean
    public YoutubeDownloader youtubeDownloader(Config config) {
        return new YoutubeDownloader(config);
    }

    @Bean Config config() {

//        Authenticator.setDefault(new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                if (getRequestingHost().equals("45.151.106.205")) {
//                    return new PasswordAuthentication("5n2RKD", "ubrDY7".toCharArray());
//                }
//                return null;
//            }
//        });
//
//        System.setProperty("http.proxyHost", "45.151.106.205");
//        System.setProperty("http.proxyPort", "8000");
//        System.setProperty("https.proxyHost", "45.151.106.205");
//        System.setProperty("https.proxyPort", "8000");

        return new Config.Builder()
//                .executorService(executorService) // for async requests, default Executors.newCachedThreadPool()
                .maxRetries(3) // retry on failure, default 0
                .header("Accept-language", "en-US,en;") // extra request header
//                .proxy("192.168.0.1", 2005)

//                .proxyCredentialsManager(proxyCredentials) // default ProxyCredentialsImpl
//                .proxy("45.151.106.205", 8000, "5n2RKD", "ubrDY7")
                .build();
    }

//    @Bean
//    public ProxyCredentials proxyCredentials() {
//        ProxyCredentials proxyCredentials = new ProxyCredentialsImpl();
//        proxyCredentials.addAuthentication();
//    }

}
