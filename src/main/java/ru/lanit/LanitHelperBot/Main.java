package ru.lanit.LanitHelperBot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class Main {
    final static private Logger log = LogManager.getLogger("Bot");

    public static void main(String[] args) {
        try {
            System.getProperties().load(Main.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Отсутствует файл с токеном");
        }

        ApiContextInitializer.init();

        DefaultBotOptions options = ApiContext.getInstance(DefaultBotOptions.class);
        options.setBaseUrl(Config.PROXY);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new LanitHelperBot(options));
            log.info("Bot started.");
        } catch (TelegramApiException e) {
            log.error("Не удалось запустить бота", e);
        }
    }
}
