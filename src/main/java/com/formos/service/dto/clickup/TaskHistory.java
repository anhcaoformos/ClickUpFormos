package com.formos.service.dto.clickup;

import com.formos.service.utils.FileUtils;
import java.io.File;
import net.lingala.zip4j.ZipFile;

public class TaskHistory {

    private String taskId;
    private String timestamp;
    private String relativePath;
    private String fullPath;
    private File folder;

    public TaskHistory(String baseFolder, String taskId, String timestamp) {
        this.taskId = taskId;
        this.timestamp = timestamp;
        this.relativePath = FileUtils.getRelativePath(baseFolder, null);
        this.fullPath = FileUtils.getOutputDirectoryForTargetAndTimestamp(baseFolder, taskId, timestamp);
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public File getFolder() {
        return folder;
    }

    public void setFolder(File folder) {
        this.folder = folder;
    }
}
