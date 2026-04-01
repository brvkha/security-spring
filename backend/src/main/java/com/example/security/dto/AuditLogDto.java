package com.example.security.dto;
import java.time.Instant;
public class AuditLogDto {
    private Long id;
    private String eventType;
    private String actor;
    private String target;
    private Instant timestamp;
    private String result;
    private String details;
    public AuditLogDto(Long id, String eventType, String actor, String target, Instant timestamp, String result, String details) {
        this.id = id; this.eventType = eventType; this.actor = actor; this.target = target;
        this.timestamp = timestamp; this.result = result; this.details = details;
    }
    public Long getId() { return id; }
    public String getEventType() { return eventType; }
    public String getActor() { return actor; }
    public String getTarget() { return target; }
    public Instant getTimestamp() { return timestamp; }
    public String getResult() { return result; }
    public String getDetails() { return details; }
}
