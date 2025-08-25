package com.therapy.nest.audit;

import com.therapy.nest.uaa.service.UserAccountService;
import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionListener;
import org.hibernate.envers.RevisionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuditRevisionListener implements RevisionListener, EntityTrackingRevisionListener {
    static private UserAccountService userAccountService;

    @Autowired
    public void init(UserAccountService userAccountService) {
        AuditRevisionListener.userAccountService = userAccountService;
    }

    @Override
    public void newRevision(Object revisionEntity) {
        String username = "SYSTEM";
        String ipAddress = userAccountService.getClientIp();
        String userAgent = userAccountService.getUserAgent();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            username = authentication.getName();
        }

        AuditRevision auditRevision = (AuditRevision) revisionEntity;
        auditRevision.setUsername(username);
        auditRevision.setIpAddress(ipAddress);
        auditRevision.setUserAgent(userAgent);
    }

    @Override
    public void entityChanged(Class aClass, String s, Object o, RevisionType revisionType, Object o1) {

    }
}
