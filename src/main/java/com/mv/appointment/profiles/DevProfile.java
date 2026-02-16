package com.mv.appointment.profiles;

import com.mv.appointment.services.DBService;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevProfile {

    private final DBService dbService;

    public DevProfile(DBService dbService) {
        this.dbService = dbService;
    }

    @PostConstruct
    public void instanceDB() {
    	this.dbService.instantiateDatabase();
    }
}
