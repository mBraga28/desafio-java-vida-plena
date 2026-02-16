package com.mv.appointment.profiles;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.mv.appointment.services.DBService;

import jakarta.annotation.PostConstruct;

@Configuration
@Profile("test")
public class TestProfile {

    private final DBService dbService;

    public TestProfile(DBService dbService) {
        this.dbService = dbService;
    }

    @PostConstruct
    public void instanceDB() {
    	this.dbService.instantiateDatabase();
    }
}
