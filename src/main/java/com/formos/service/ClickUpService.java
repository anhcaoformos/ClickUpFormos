package com.formos.service;

import com.formos.domain.File;
import com.formos.domain.Profile;
import com.formos.service.dto.clickup.TaskHistory;
import java.util.List;

public interface ClickUpService {
    TaskHistory exportPdfForTask(Profile profile, String taskId) throws Exception;
    void exportPdfForTasks(Long profileId, List<String> taskIds) throws Exception;
}
