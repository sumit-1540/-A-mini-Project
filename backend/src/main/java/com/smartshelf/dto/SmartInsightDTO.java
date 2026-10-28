package com.smartshelf.dto;

import com.smartshelf.models.EntityType;

public class SmartInsightDTO {
    private String id;
    private String title;
    private String description;
    private String category;
    private String impact;
    private String actionLabel;
    private String actionLink;
    private EntityType entityType;
    private Long entityId;

    public SmartInsightDTO() {}

    public SmartInsightDTO(String id, String title, String description, String category, String impact, String actionLabel, String actionLink, EntityType entityType, Long entityId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.impact = impact;
        this.actionLabel = actionLabel;
        this.actionLink = actionLink;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id;
        private String title;
        private String description;
        private String category;
        private String impact;
        private String actionLabel;
        private String actionLink;
        private EntityType entityType;
        private Long entityId;

        public Builder id(String id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder impact(String impact) { this.impact = impact; return this; }
        public Builder actionLabel(String actionLabel) { this.actionLabel = actionLabel; return this; }
        public Builder actionLink(String actionLink) { this.actionLink = actionLink; return this; }
        public Builder entityType(EntityType entityType) { this.entityType = entityType; return this; }
        public Builder entityId(Long entityId) { this.entityId = entityId; return this; }

        public SmartInsightDTO build() {
            return new SmartInsightDTO(id, title, description, category, impact, actionLabel, actionLink, entityType, entityId);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImpact() { return impact; }
    public void setImpact(String impact) { this.impact = impact; }

    public String getActionLabel() { return actionLabel; }
    public void setActionLabel(String actionLabel) { this.actionLabel = actionLabel; }

    public String getActionLink() { return actionLink; }
    public void setActionLink(String actionLink) { this.actionLink = actionLink; }

    public EntityType getEntityType() { return entityType; }
    public void setEntityType(EntityType entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
}
