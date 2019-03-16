package ru.test.services.api;

import ru.test.entities.api.IUser;
import ru.test.exceptions.DBException;

/**
 * Контракт сервиса взаимодействия с БД
 */
public interface IDBService {

    /**
     * Возвращает пользователя по индентификатору
     *
     * @param id идентификатор
     * @return сущность пользователя
     * @throws DBException в случае ошибки
     */
    IUser getUser(long id) throws DBException;

    /**
     * Возвращает идентификатор пользователя по его логину
     *
     * @param login логин пользователя
     * @return идентификатор пользователя
     * @throws DBException в случае ошибки
     */
    long getUserId(String login) throws DBException;

    /**
     * Возвращает пользователя по его логину
     *
     * @param login логин порльзователя
     * @return сущность пользователя
     * @throws DBException в случае ошибки
     */
    IUser getUserByLogin(String login) throws DBException;

    /**
     * Добавляет пользователя в систему
     *
     * @param login логин пользвателя
     * @param password пароль пользователя
     * @return возвращает идентифкатор добавленного пользователя
     * @throws DBException в случае ошибки
     */
    Long addUser(String login, String password) throws DBException;

}
