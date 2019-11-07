package ru.lanit.LanitHelperBot;

import java.time.format.DateTimeFormatter;

public class Config {
    public static final String PROXY = "https://telegg.ru/orig/bot";
    public static final String SELECT_DIRECTION = "В какую сторону?";
    public static final String NO_ROUTES = "В ближайшие %s минут по направлению _%s_ не будет. Ближайший: *%s*";
    public static final String AVAILABLE_ROUTES =  "В ближайшие %s минут по направлению _%s_ доступны рейсы:\n\n*%s*";
    public static final String NO_ROUTES_FOR_DAYS = "В ближайшие *%d дней* маршруток по направлению _%s_ не будет";

    public static final int DEFAULT_SCHEDULE_DURATION = 60;
    public static final int MAX_DAYS = 15;

    public static final String BOT_NAME = LanitHelperBot.class.getSimpleName();
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

}
