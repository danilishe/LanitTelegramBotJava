package repo;


import model.User;

public class UserRepo {
    private static final User anonymous = new User()
            .setId(-1)
            .setName("Новый (аутентификация не пройдена)")
            .addTag("Мурманский")
            .setLogged(false);

    public User getUser(long id) {
        return new User()
                .setId(id)
                .setName("user")
                .addTag("Мурманский")
                .setLogged(true);
    }
}
