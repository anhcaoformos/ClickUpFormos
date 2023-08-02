package com.formos.service.dto.clickup;

import com.formos.service.utils.FileUtils;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TaskHistory {

    private String taskId;
    private String timestamp;
    private String relativePath;
    private String fullPath;
    private File folder;
    private TaskData taskData;
    private List<History> historiesData;
    private String originTaskData;
    private String originHistoriesData;
    private Map<String, TaskComments> childrenComments = new HashMap<>();

    public TaskHistory(
        String baseFolder,
        String taskId,
        String timestamp,
        String originTaskData,
        String originHistoriesData,
        String childrenCommentData
    ) {
        this.taskId = taskId;
        this.timestamp = timestamp;
        this.relativePath = FileUtils.getRelativePath(baseFolder, null);
        this.fullPath = FileUtils.getOutputDirectoryForTargetAndTimestamp(baseFolder, taskId, timestamp);
        this.folder = new java.io.File(fullPath);
        this.originTaskData = originTaskData;
        this.originHistoriesData = originHistoriesData;
        Gson gson = new Gson();
        this.taskData = gson.fromJson(originTaskData, TaskData.class);
        this.historiesData = gson.fromJson(originHistoriesData, new TypeToken<List<History>>() {}.getType());
        if (Objects.nonNull(childrenCommentData)) {
            this.childrenComments = gson.fromJson(childrenCommentData, new TypeToken<Map<String, TaskComments>>() {}.getType());
        }
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

    public TaskData getTaskData() {
        return taskData;
    }

    public void setTaskData(TaskData taskData) {
        this.taskData = taskData;
    }

    public List<History> getHistoriesData() {
        return historiesData;
    }

    public void setHistoriesData(List<History> historiesData) {
        this.historiesData = historiesData;
    }

    public Map<String, TaskComments> getChildrenComments() {
        return childrenComments;
    }

    public void setChildrenComments(Map<String, TaskComments> childrenComments) {
        this.childrenComments = childrenComments;
    }

    public String getOriginTaskData() {
        return originTaskData;
    }

    public void setOriginTaskData(String originTaskData) {
        this.originTaskData = originTaskData;
    }

    public String getOriginHistoriesData() {
        return originHistoriesData;
    }

    public void setOriginHistoriesData(String originHistoriesData) {
        this.originHistoriesData = originHistoriesData;
    }
}
