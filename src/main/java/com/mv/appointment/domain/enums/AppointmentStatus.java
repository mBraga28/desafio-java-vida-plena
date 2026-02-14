package com.mv.appointment.domain.enums;

public enum AppointmentStatus {

    SCHEDULED(0),
    IN_PROGRESS(1),
    COMPLETED(2),
    CANCELED(3);

    private final int code;

    private AppointmentStatus(int cod) {
        this.code = cod;
    }
}
