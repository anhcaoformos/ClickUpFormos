package com.formos.service.dto.clickup;

import com.formos.config.Constants;

public enum HistoryField {
    COMMENT,
    COMMENT_ASSIGNED,
    COMMENT_RESOLVED,
    STATUS,
    NEW_SUBTASK,
    TASK_CREATION,
    COLLAPSED_ITEMS,
    SECTION_MOVED,
    ATTACHMENTS;

    public static boolean isCommentType(HistoryField historyField) {
        return Constants.COMMENT_TYPES.contains(historyField);
    }

    public static HistoryField getValueFrom(String historyField) {
        return valueOf(historyField.toUpperCase());
    }
}
