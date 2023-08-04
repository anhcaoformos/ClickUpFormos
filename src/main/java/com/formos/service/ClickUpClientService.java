package com.formos.service;

import com.formos.domain.Profile;
import com.formos.service.dto.clickup.CommentDTO;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

public interface ClickUpClientService {
    Object getChildrenComments(String taskId, CommentDTO comment, String childrenCommentEndpoint, Header token) throws URISyntaxException;
    <T> T getRequest(String endpoint, List<NameValuePair> parameters, Header header, Class<T> valueType) throws URISyntaxException;

    <T> T getResponse(Class<T> valueType, HttpClient httpClient, HttpRequestBase request);
    <T, V> T postRequest(String endpoint, V body, Header header, Class<T> valueType);

    Object getHistories(String historyEndpoint, Header token, String startId) throws URISyntaxException;

    Object getCollapsedHistories(String historyEndpoint, Header header, String startId, String endId) throws URISyntaxException;

    Object getTask(String taskEndpoint, Header token) throws URISyntaxException;

    String getTaskTitle(Profile profile, String taskId);

    Object getTeams(String teamsEndpoint, Header token) throws URISyntaxException;

    Object getTeam(String teamEndpoint, Header token) throws URISyntaxException;

    Object getSubCategory(String subCategoryEndpoint, Header token) throws URISyntaxException;

    Object getView(String viewEndpoint, Header token, String subCategoryId, String viewId) throws URISyntaxException;
}
