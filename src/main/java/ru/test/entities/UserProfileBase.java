package ru.test.entities;

import ru.test.entities.api.IUser;

/**
 * Базовая реализация пользовательского профиля
 */
public class UserProfileBase implements IUser {

    private Long id;

    private String login;

    private String password;

    public UserProfileBase() {
    }

    public UserProfileBase(Long id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public UserProfileBase(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
