package com.formos.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formos.config.Constants;
import com.formos.domain.Profile;
import com.formos.service.ClickUpClientService;
import com.formos.service.dto.clickup.*;
import com.formos.service.mapper.CommentMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Service
public class ClickUpClientServiceImpl implements ClickUpClientService {

    public TaskComments getChildrenComments(String taskId, CommentDTO comment, String childrenCommentEndpoint, Header token)
        throws URISyntaxException {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("key", "2_" + comment.getId()));
        parameters.add(new BasicNameValuePair("parent", comment.getId()));
        parameters.add(new BasicNameValuePair("type", "2"));
        parameters.add(new BasicNameValuePair("loadAllPages", "false"));
        return getRequest(childrenCommentEndpoint, parameters, token, TaskComments.class);
    }

    public <T> T getRequest(String endpoint, List<NameValuePair> parameters, Header header, Class<T> valueType) throws URISyntaxException {
        HttpClient httpClient = HttpClientBuilder.create().build();

        ObjectMapper objectMapper = new ObjectMapper();
        HttpGet request = new HttpGet(endpoint);
        request.addHeader(header);
        if (Objects.nonNull(parameters) && !parameters.isEmpty()) {
            URI uri = new URIBuilder(request.getURI()).addParameters(parameters).build();
            request.setURI(uri);
        }
        return getResponse(valueType, httpClient, objectMapper, request);
    }

    public <T> T getResponse(Class<T> valueType, HttpClient httpClient, ObjectMapper objectMapper, HttpRequestBase request) {
        try {
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                return objectMapper.readValue(responseBody, valueType);
            } else {
                System.out.println("Request failed with status code: " + statusCode);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T, V> T postRequest(String endpoint, V body, Header header, Class<T> valueType) {
        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpPost request = new HttpPost(endpoint);
        request.addHeader(header);
        ObjectMapper objectMapper = new ObjectMapper();

        if (Objects.nonNull(body)) {
            Gson gson = new Gson();
            String requestBody = gson.toJson(body);

            StringEntity entity = new StringEntity(requestBody, "UTF-8");
            entity.setContentType("application/json");
            request.setEntity(entity);
        }

        return getResponse(valueType, httpClient, objectMapper, request);
    }

    public HistoryData getHistories(String historyEndpoint, Header token) throws URISyntaxException {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("contracted_view", Boolean.TRUE.toString()));
        parameters.add(new BasicNameValuePair("reverse", Boolean.FALSE.toString()));
        parameters.add(new BasicNameValuePair("hist_fields[]", "tag"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "tag_removed"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "time_spent"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "dependency_of"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "resolved_items"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "depends_on"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "checklist_items_added"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "checklist_item_assignee"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "checklist_item_resolved"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "priority"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "status"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "assignee"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "follower"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "time_estimate"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "content"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "name"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "task_creation"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "section_moved"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "new_subtask"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "duplicate"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "template_merged"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "due_date"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "start_date"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "comment_resolved"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "comment_assigned"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "new_activity"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "comment"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "email_comment"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "collapsed_items"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "gh_commit"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "attachments"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "gl_commit"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "gl_issue"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "gl_merge_request"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "recurrence_set"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "recurrence_set_2.0"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "recur"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "recur_2.0"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "recurrence_removed"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "copy_task_recur"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "recurrence_missed"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "github_pull_request"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "bb_commit"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "bb_pr_created"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "bb_pr_updated"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "bb_pr_approved"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "bb_pr_unapproved"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "bb_pr_fulfilled"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "bb_pr_rejected"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "bb_issue"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "bb_issue_updated"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "gl_task_branch"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "gh_task_branch"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "linked_task"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "custom_fields"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "zoom_meeting"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "attachment_comment"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "group_assignee"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "custom_type"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "bb_task_branch"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "bb_pull_request"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "points"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "added_to_subcategory"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "removed_from_subcategory"));
        parameters.add(new BasicNameValuePair("hist_fields[]", "hubspot"));
        parameters.add(new BasicNameValuePair("email_filter_flag", Boolean.TRUE.toString()));
        return getRequest(historyEndpoint, parameters, token, HistoryData.class);
    }

    public TaskData getTask(String taskEnpoint, Header token) throws URISyntaxException {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("include_groups", Boolean.TRUE.toString()));
        parameters.add(new BasicNameValuePair("field_backlinks", Boolean.TRUE.toString()));
        parameters.add(new BasicNameValuePair("isSharingApp", Boolean.FALSE.toString()));
        parameters.add(new BasicNameValuePair("fields[]", "content"));
        parameters.add(new BasicNameValuePair("fields[]", "assignees"));
        parameters.add(new BasicNameValuePair("fields[]", "dependencies"));
        parameters.add(new BasicNameValuePair("fields[]", "parent_task"));
        parameters.add(new BasicNameValuePair("fields[]", "subtask_parent_task"));
        parameters.add(new BasicNameValuePair("fields[]", "attachments"));
        parameters.add(new BasicNameValuePair("fields[]", "hidden_attachments"));
        parameters.add(new BasicNameValuePair("fields[]", "followers"));
        parameters.add(new BasicNameValuePair("fields[]", "totalTimeSpent"));
        parameters.add(new BasicNameValuePair("fields[]", "subtasks"));
        parameters.add(new BasicNameValuePair("fields[]", "todoComments"));
        parameters.add(new BasicNameValuePair("fields[]", "mentions"));
        parameters.add(new BasicNameValuePair("fields[]", "tags"));
        parameters.add(new BasicNameValuePair("fields[]", "position"));
        parameters.add(new BasicNameValuePair("fields[]", "simple_statuses"));
        parameters.add(new BasicNameValuePair("fields[]", "viewing"));
        parameters.add(new BasicNameValuePair("fields[]", "commenting"));
        parameters.add(new BasicNameValuePair("fields[]", "customFields"));
        parameters.add(new BasicNameValuePair("fields[]", "statuses"));
        parameters.add(new BasicNameValuePair("fields[]", "members"));
        parameters.add(new BasicNameValuePair("fields[]", "features"));
        parameters.add(new BasicNameValuePair("fields[]", "rolledUpTimeSpent"));
        parameters.add(new BasicNameValuePair("fields[]", "rolledUpTimeEstimate"));
        parameters.add(new BasicNameValuePair("fields[]", "rolledUpPointsEstimate"));
        parameters.add(new BasicNameValuePair("fields[]", "views"));
        parameters.add(new BasicNameValuePair("fields[]", "linkedTasks"));
        parameters.add(new BasicNameValuePair("fields[]", "last_viewed"));
        parameters.add(new BasicNameValuePair("fields[]", "new_thread_count"));
        parameters.add(new BasicNameValuePair("fields[]", "commit_counts"));
        parameters.add(new BasicNameValuePair("fields[]", "relationships"));
        parameters.add(new BasicNameValuePair("markItemViewed", Boolean.TRUE.toString()));
        parameters.add(new BasicNameValuePair("include_archived_subtasks", Boolean.TRUE.toString()));
        parameters.add(new BasicNameValuePair("clear_nested_subtasks", Boolean.TRUE.toString()));
        return getRequest(taskEnpoint, parameters, token, TaskData.class);
    }

    public String getTaskTitle(Profile profile, String taskId) {
        if (Objects.isNull(profile)) {
            return "";
        }
        String taskApiEndPoint = Constants.TASK_API_ENDPOINT + taskId;
        Header authorization = new BasicHeader("Authorization", profile.getApiKey());
        try {
            Task task = getRequest(taskApiEndPoint, null, authorization, Task.class);
            return task.name + " | " + task.status.color;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "";
        }
    }
}
