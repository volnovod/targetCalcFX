package com.aim.service;

import com.aim.dao.AimDao;
import com.aim.model.Aim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by victor on 17.02.15.
 * сервіс для взаємодії користувача і бази даних
 */
@Service("service")
public class AimServiceImpl implements AimService {

    @Autowired
    private AimDao aimDao;

    public AimServiceImpl() {
    }

    /**
     * створення цілі в базі
     * @param aim
     */
    public void createAim(Aim aim) {
        aimDao.create(aim);
    }

    /**
     * отримати ціль із бази по id
     * @param id
     * @return
     */
    public Aim getAim(Integer id) {
        return aimDao.read(id);
    }

    /**
     * видаляє ціль із бази
     * @param aim
     * @return
     */
    public boolean deleteAim(Aim aim) {
        return aimDao.delete(aim);
    }

    /**
     * оновлює в базі параметри цілі
     * @param aim
     * @return
     */
    public boolean updateAim(Aim aim) {
        return aimDao.update(aim);
    }

    /**
     * отримує список всіх цілей із бази
     * @return
     */
    public List<Aim> getAllAim() {
        return aimDao.findAll();
    }
}
