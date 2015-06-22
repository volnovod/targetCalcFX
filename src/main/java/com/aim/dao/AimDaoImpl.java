package com.aim.dao;

import com.aim.model.Aim;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by victor on 17.02.15.
 * клас, який реалізує DAO
 */
@Repository("aimDao")
@Transactional
public class AimDaoImpl implements AimDao {

    @Autowired
    SessionFactory factory;

    public AimDaoImpl() {

    }

    /**
     * метод створює ціль в базі даних
     * @param aim
     * @return порядковий номер цілі в базі даних
     */
    public Integer create(Aim aim) {
        return (Integer) factory.getCurrentSession().save(aim);
    }

    /**
     * метод дозволяє отримати ціль із бази даних по порядковому номеру
     * @param id
     * @return ціль по id
     */
    @Transactional(readOnly = true)
    public Aim read(Integer id) {
        return (Aim) factory.getCurrentSession().get(Aim.class, id);
    }

    /**
     * метод оновлює параметри цілі
     * @param aim
     * @return true - все вдало оновлено, false - помилка
     */
    public boolean update(Aim aim) {
        factory.getCurrentSession().update("AIM", aim);
        return true;
    }

    /**
     * метод видаляє ціль
     * @param aim
     * @return true - видалено вдало, false - помилка
     */
    public boolean delete(Aim aim) {
        factory.getCurrentSession().delete("AIM", aim);
        return false;
    }

    /**
     * метод дозволяє отримати список всіх цілей із бази даних
     * @return список цілей
     */
    @Transactional(readOnly = true)
        public List<Aim> findAll() {
        return factory.getCurrentSession().createCriteria(Aim.class).list();
    }
}
