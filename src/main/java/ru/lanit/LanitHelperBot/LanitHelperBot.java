package ru.lanit.LanitHelperBot;

import ru.lanit.LanitHelperBot.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.lanit.LanitHelperBot.repo.TransportRepo;
import ru.lanit.LanitHelperBot.repo.UserRepo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LanitHelperBot extends TelegramLongPollingBot {
    final private Logger log = LogManager.getLogger("Bot");
    final private UserRepo userRepo = new UserRepo();

    public LanitHelperBot(DefaultBotOptions options) {
        super(options);
    }

    public void onUpdateReceived(Update update) {
        SendMessage message = null;

        if (update.hasCallbackQuery()) {
            message = dispatchQuery(update.getCallbackQuery());
        } else if (update.hasMessage()) {
            message = dispatchMessage(update.getMessage());
        } else if (update.hasEditedMessage()) {
            message = dispatchEditedMessage(update.getEditedMessage());
        }

        if (message != null)
            try {
                message.enableMarkdown(true);
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Не удалось отправить сообщение!\n", e);
            }
    }

    private SendMessage dispatchQuery(CallbackQuery callbackQuery) {
        final Message message = callbackQuery.getMessage();
        final Long chatId = message.getChatId();
        final String text = message.getText();
        if (text.equals(Config.SELECT_DIRECTION)) {
            String direction = callbackQuery.getData();
            log.trace("Запрос расписания: " + direction);
//                LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(callbackQuery.getMessage().getDate()), ZoneId.systemDefault());
            List<String> schedule = TransportRepo.getSchedule(direction, LocalDateTime.now(), Duration.ofMinutes(Config.DEFAULT_SCHEDULE_DURATION));
            //todo переделать на запрос в начале: полное расписание? на ближайшие полчаса? час? дату? -> полное расписание, время
            return new SendMessage()
                    .setChatId(chatId)
                    .setText(
                            schedule.isEmpty() ?
                                    String.format(Config.NO_ROUTES, Config.DEFAULT_SCHEDULE_DURATION, direction, TransportRepo.getNext(direction, LocalDateTime.now()))
                                    : String.format(Config.AVAILABLE_ROUTES, Config.DEFAULT_SCHEDULE_DURATION, direction, String.join("\n", schedule))
                    );
        }
        log.error("Некорректный CallBackQuery:\n" + callbackQuery.toString());
        return null;
    }

    private SendMessage dispatchMessage(Message message) {
        final User user = userRepo.getUser(message.getFrom().getId());
        final Long chatId = message.getChatId();
        final String command = message.getText();

        if (command.matches("/transport\\b.*")) {
            Set<String> directions = TransportRepo.getDirections(user.getTags());
            return new SendMessage()
                    .setChatId(chatId)
                    .setReplyToMessageId(message.getMessageId())
                    .setText(Config.SELECT_DIRECTION)
                    .setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(
                            directions.stream().map(d -> Arrays.asList(new InlineKeyboardButton(d).setCallbackData(d))).collect(Collectors.toList())
                    ));
        } else if (command.matches("/help\\b.*")) {
            return new SendMessage()
                    .setChatId(chatId)
                    .setText("Для доступа к некоторым разделам бота нужна авторизация. Авторизация подтверждает, что вы являетесь в данный момент сотрудником Ланит.\n" +
                            "Для этого выберите /start и введите свою корпоративную почту. На указанный адрес в течение 5 минут придёт токен, который нужно отправить боту. Если токен не приходит длительное время, попробуйте проверить папку \"Спам\". \n" +
                            "После авторизации необходимо указать теги, относящиеся к вашей работе: город, офис, проект и т.п. В зависимости от указанных тегов будут приходить информационные сообщения и фильтроваться справочная информация.\n" +
                            "Команда /transport позволяет получить актуальную информацию по транспорту для вашего офиса.\n" +
                            "\n" +
                            "Мы надеемся, что нашего бота работа будет способствовать вашей эффективности."
                    );
//                    sendMessage.setText(new String(Files.readAllBytes(Paths.get("help.md"))));
        } else if (command.matches("/start\\b.*")) {
            return new SendMessage()
                    .setChatId(chatId)
                    .setText("Привет! Для помощи выбери /help а для получения информации по маршруткам (пока только для офиса на Мурманской в Москве) выбери /transport");
        } else if (command.matches("/settings\\b.*")) {
            return new SendMessage()
                    .setChatId(chatId)
                    .setText("Пока настроек нет \uD83D\uDE14");
        }
        return new SendMessage()
                .setChatId(chatId)
                .setText("Я пока не понимаю \uD83E\uDD16");
    }

    private SendMessage dispatchEditedMessage(Message editedMessage) {
        return null;
    }

    public String getBotUsername() {
        return Config.BOT_NAME;
    }

    public String getBotToken() {
        return System.getProperty("telegram.token");
    }
}
