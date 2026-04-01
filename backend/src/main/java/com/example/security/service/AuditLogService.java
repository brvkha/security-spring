package com.example.security.service;

import com.example.security.entity.AuditLog;
import com.example.security.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String eventType, String actor, String target, String result, String details) {
        AuditLog entry = new AuditLog();
        entry.setEventType(eventType);
        entry.setActor(actor);
        entry.setTarget(target);
        entry.setResult(result);
        entry.setDetails(details);
        auditLogRepository.save(entry);
    }

    public Page<AuditLog> getLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable);
    }
}
