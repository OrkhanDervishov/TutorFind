package com.team13.TutorFind.User;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum UserRoles {
    @Enumerated(EnumType.STRING)
    LEARNER,
    @Enumerated(EnumType.STRING)
    TUTOR,
    @Enumerated(EnumType.STRING)
    ADMIN
}
