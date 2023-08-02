package com.formos.service;

import com.formos.domain.Profile;
import com.formos.service.dto.clickup.TaskHistory;
import java.util.List;
import net.lingala.zip4j.ZipFile;

public interface ClickUpService {
    TaskHistory exportPdfForTask(Profile profile, String taskId) throws Exception;
    ZipFile exportPdfForTasks(Long profileId, List<String> taskIds) throws Exception;
    ZipFile exportPdfForHistory(Long historyId) throws Exception;
}
