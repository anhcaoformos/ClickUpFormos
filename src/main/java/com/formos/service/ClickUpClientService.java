package com.formos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formos.domain.Profile;
import com.formos.service.dto.clickup.CommentDTO;
import com.formos.service.dto.clickup.HistoryData;
import com.formos.service.dto.clickup.TaskComments;
import com.formos.service.dto.clickup.TaskData;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

public interface ClickUpClientService {
    TaskComments getChildrenComments(String taskId, CommentDTO comment, String childrenCommentEndpoint, Header token)
        throws URISyntaxException;
    <T> T getRequest(String endpoint, List<NameValuePair> parameters, Header header, Class<T> valueType) throws URISyntaxException;

    <T> T getResponse(Class<T> valueType, HttpClient httpClient, ObjectMapper objectMapper, HttpRequestBase request);
    <T, V> T postRequest(String endpoint, V body, Header header, Class<T> valueType);

    HistoryData getHistories(String historyEndpoint, Header token, String startId) throws URISyntaxException;

    HistoryData getCollapsedHistories(String historyEndpoint, Header header, String startId, String endId) throws URISyntaxException;

    TaskData getTask(String taskEnpoint, Header token) throws URISyntaxException;

    String getTaskTitle(Profile profile, String taskId);
}
