package com.aim.SpringLoader;

import com.aim.service.AimService;
import com.aim.service.AimServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by victor on 03.06.15.
 */
public class SpringFXMLLoader {

    private static Logger logger = Logger.getLogger(SpringFXMLLoader.class);
    private AimService service;

    public SpringFXMLLoader() {
        service = new ClassPathXmlApplicationContext("transactionalContext.xml").getBean("service", AimServiceImpl.class);
    }

    public AimService getService() {
        return service;
    }

    public void setService(AimService service) {
        this.service = service;
    }
}
