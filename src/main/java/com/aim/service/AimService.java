package com.aim.service;


import com.aim.model.Aim;

import java.util.List;

/**
 * Created by victor on 17.02.15.
 */
public interface AimService {

    void createAim(Aim aim);
    Aim getAim(Integer id);
    boolean deleteAim(Aim aim);
    boolean updateAim(Aim aim);
    List<Aim> getAllAim();
}
