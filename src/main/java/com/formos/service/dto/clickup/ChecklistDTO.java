package com.formos.service.dto.clickup;

import java.util.List;

public class ChecklistDTO {

    private String id;
    private String name;
    private Long orderIndex;
    private long creatorId;
    private long resolved;
    private long unresolved;
    private List<ChecklistItemDTO> items;

    public ChecklistDTO() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Long orderIndex) {
        this.orderIndex = orderIndex;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public long getResolved() {
        return resolved;
    }

    public void setResolved(long resolved) {
        this.resolved = resolved;
    }

    public long getUnresolved() {
        return unresolved;
    }

    public void setUnresolved(long unresolved) {
        this.unresolved = unresolved;
    }

    public List<ChecklistItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ChecklistItemDTO> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return (
            "ChecklistDTO{" +
            "id='" +
            id +
            '\'' +
            ", name='" +
            name +
            '\'' +
            ", orderIndex=" +
            orderIndex +
            ", creatorId=" +
            creatorId +
            ", resolved=" +
            resolved +
            ", unresolved=" +
            unresolved +
            ", items=" +
            items +
            '}'
        );
    }
}
