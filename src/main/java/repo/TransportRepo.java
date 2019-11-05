package repo;

import controller.Config;
import util.CalendarHelper;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class TransportRepo {
    final static Map<String, List<String>> routes = new HashMap<String, List<String>>() {{
        put("Рижская - Мурманский", Arrays.asList("07:00", "07:15", "07:35", "07:55", "08:15", "08:20", "08:24", "08:28", "08:32", "08:36", "08:40", "08:44", "08:48", "08:52", "08:56", "09:00", "09:04", "09:08", "09:12", "09:16", "09:20", "09:24", "09:28", "09:32", "09:36", "09:40", "09:44", "09:48", "09:52", "09:56", "10:00", "10:04", "10:08", "10:12", "10:16", "10:20", "10:24", "10:28", "10:32", "10:36", "10:40", "10:44", "10:48", "10:52", "10:56", "11:00", "11:04", "11:08", "11:12", "11:16", "11:20", "11:24", "11:28", "11:32", "11:36", "11:56", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "16:55"));
        put("Мурманский - Рижская", Arrays.asList("10:26", "10:46", "11:06", "11:26", "11:46", "12:50", "13:15", "13:45", "14:15", "14:45", "15:15", "15:45", "16:15", "16:45", "17:30", "17:35", "17:40", "17:45", "17:50", "17:55", "18:00", "18:05", "18:10", "18:15", "18:20", "18:25", "18:30", "18:35", "18:40", "18:45", "18:50", "18:55", "19:05", "19:10", "19:20", "19:25", "19:30", "19:35", "19:40", "19:45", "20:05", "20:25", "20:45", "21:05", "21:25", "21:45"));
        put("Марьина роща - Мурманский", Arrays.asList("07:40", "08:00", "08:25", "08:50", "09:20", "09:50", "10:20", "10:50"));
        put("Мурманский - Марьина роща", Arrays.asList("18:05", "18:35", "19:05", "19:35", "20:00"));
        put("Площадь Ильича - Мурманский", Arrays.asList("07:30", "07:45", "08:00", "08:10", "08:20", "08:30", "10:00", "10:10", "10:20", "10:30", "10:40", "10:50", "11:00", "11:10", "11:20"));
        put("Мурманский - Площадь Ильича", Arrays.asList("17:00", "17:10", "17:20", "17:30", "17:40", "17:50", "18:45", "18:55", "19:05", "19:15", "19:25", "19:35", "19:45", "20:00"));
    }};

    //    final static Map<String, List<String>> routesHolidays = new HashMap<String, List<String>>() {{
//        put("Рижская - Мурманский", Arrays.asList(
//                 "16:00", "16:30", "16:55"));
//        put("Мурманский - Рижская", Arrays.asList(
//                "11:06","21:25", "21:45"));
//    }};

    public static List<String> getSchedule(String direction, LocalDate date) {
        if (direction == null || direction.isEmpty() || !routes.containsKey(direction))
            throw new RuntimeException("Направление не может быть пустым, либо не существует расписания для направления \"" + direction + "\"");
        if (CalendarHelper.isHoliday(date)) return Collections.emptyList();
        return routes.get(direction);
    }

    public static List<String> getSchedule(String direction, LocalDateTime date) {
        return getSchedule(direction, date.toLocalDate()).stream()
                .filter(t -> {
                    LocalDateTime time = LocalDateTime.of(LocalDate.now(), LocalTime.parse(t));
                    return time.isAfter(date);
                })
                .collect(Collectors.toList());
    }

    public static List<String> getSchedule(String direction, LocalDateTime dateTime, Duration duration) {
        final LocalDateTime max = LocalDateTime.now().plus(duration);
        return getSchedule(direction, dateTime).stream().filter(t -> {
            LocalDateTime time = LocalDateTime.of(LocalDate.now(), LocalTime.parse(t));
            return time.isAfter(dateTime) && time.isBefore(max);
        }).collect(Collectors.toList());
    }

    public static Set<String> getDirections(Set<String> tags) {
        Set<String> directions = new HashSet<>();
        for (String tag : tags)
            for (String route : routes.keySet())
                if (route.contains(tag))
                    directions.add(route);
        return directions;
    }

    public static String getNext(String direction, LocalDateTime time) {
        List<String> availableRoutes = getSchedule(direction, time);
        int daysAfter = 0;
        while (availableRoutes.isEmpty()) {
            if (++daysAfter > Config.MAX_DAYS)
                return String.format(Config.NO_ROUTES_FOR_DAYS, daysAfter, direction);
            availableRoutes = getSchedule(direction, time.plusDays(daysAfter).toLocalDate());
        }
        return time.plusDays(daysAfter).format(Config.DATE_FORMATTER) + " в " + availableRoutes.get(0);
    }
}
