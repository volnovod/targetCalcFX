package com.aim.dao;


import com.aim.model.Aim;

import java.util.List;

/**
 * Created by victor on 17.02.15.
 * інтерфейс для DAO цілі
 */

public interface AimDao {

    /**
     * метод створює ціль в базі даних
     * @param aim
     * @return порядковий номер цілі в базі даних
     */
    Integer create(Aim aim);

    /**
     * метод дозволяє отримати ціль із бази даних по порядковому номеру
     * @param id
     * @return ціль по id
     */
    Aim read(Integer id);

    /**
     * метод оновлює параметри цілі
     * @param aim
     * @return true - все вдало оновлено, false - помилка
     */
    boolean update(Aim aim);

    /**
     * метод видаляє ціль
     * @param aim
     * @return true - видалено вдало, false - помилка
     */
    boolean delete(Aim aim);

    /**
     * метод дозволяє отримати список всіх цілей із бази даних
     * @return список цілей
     */
    List<Aim> findAll();
}
