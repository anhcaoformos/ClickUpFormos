package com.formos.service.impl;

import com.formos.config.Constants;
import com.formos.domain.DownloadHistory;
import com.formos.domain.File;
import com.formos.domain.Profile;
import com.formos.repository.DownloadHistoryRepository;
import com.formos.repository.FileRepository;
import com.formos.repository.ProfileRepository;
import com.formos.service.ClickUpClientService;
import com.formos.service.ClickUpService;
import com.formos.service.dto.clickup.*;
import com.formos.service.mapper.AttachmentMapper;
import com.formos.service.mapper.CommentMapper;
import com.formos.service.mapper.HistoryMapper;
import com.formos.service.mapper.TaskMapper;
import com.formos.service.utils.FileUtils;
import com.formos.web.rest.errors.BadRequestAlertException;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import net.lingala.zip4j.ZipFile;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.util.StringUtils;

@Service
@Transactional
public class ClickUpServiceImpl implements ClickUpService {

    private final Logger log = LoggerFactory.getLogger(ClickUpServiceImpl.class);
    private final SpringTemplateEngine templateEngine;
    private final ClickUpClientService clickUpClientService;

    private final ProfileRepository profileRepository;
    private final FileRepository fileRepository;
    private final DownloadHistoryRepository downloadHistoryRepository;
    private final TaskMapper taskMapper;
    private final CommentMapper commentMapper;
    private final AttachmentMapper attachmentMapper;
    private final HistoryMapper historyMapper;

    @Value("${clickup.base-folder}")
    private String baseFolder;

    public ClickUpServiceImpl(
        SpringTemplateEngine templateEngine,
        ClickUpClientService clickUpClientService,
        FileRepository fileRepository,
        ProfileRepository profileRepository,
        DownloadHistoryRepository downloadHistoryRepository,
        TaskMapper taskMapper,
        CommentMapper commentMapper,
        AttachmentMapper attachmentMapper,
        HistoryMapper historyMapper
    ) {
        this.templateEngine = templateEngine;
        this.clickUpClientService = clickUpClientService;
        this.profileRepository = profileRepository;
        this.fileRepository = fileRepository;
        this.downloadHistoryRepository = downloadHistoryRepository;
        this.taskMapper = taskMapper;
        this.commentMapper = commentMapper;
        this.attachmentMapper = attachmentMapper;
        this.historyMapper = historyMapper;
    }

    @Override
    public TaskHistory exportPdfForTask(Profile profile, String taskId) throws Exception {
        String taskEndpoint = profile.getBaseUrl() + Constants.TASK_ENDPOINT + taskId;

        TokenHistory tokenHistory = getTokenAndHistory(profile, taskId);

        if (Objects.isNull(tokenHistory)) {
            return null;
        }

        String currentTime = String.valueOf(Instant.now().getEpochSecond());
        Gson gson = new Gson();
        String originTaskData = gson.toJson(clickUpClientService.getTask(taskEndpoint, tokenHistory.getToken()));

        TaskHistory taskHistory = new TaskHistory(baseFolder, taskId, currentTime, originTaskData, tokenHistory.getOriginHistories(), null);

        generateFromTaskDataAndTaskHistory(profile, tokenHistory, taskHistory);

        return taskHistory;
    }

    private void generateFromTaskDataAndTaskHistory(Profile profile, TokenHistory tokenHistory, TaskHistory taskHistory) throws Exception {
        TaskData taskData = taskHistory.getTaskData();
        List<History> histories = taskHistory.getHistoriesData();
        TaskDTO taskDTO = taskMapper.toTaskDTO(profile, taskData, taskHistory);
        Map<String, CommentDTO> map = new LinkedHashMap<>();
        List<CommentDTO> allComments = new ArrayList<>();
        Map<String, TaskComments> childrenComments = new HashMap<>();
        Gson gson = new Gson();
        List<HistoryDTO> historyDTOs = histories
            .stream()
            .map(history -> {
                HistoryDTO historyDTO = historyMapper.toHistoryDTO(taskHistory, history);
                if (Constants.COMMENT_TYPES.contains(historyDTO.getField())) {
                    try {
                        CommentDTO historyDTOComment = historyDTO.getComment();
                        String commentId = historyDTOComment.getId();
                        TaskComments taskComments;
                        if (Objects.nonNull(tokenHistory) && Objects.nonNull(tokenHistory.getToken())) {
                            taskComments =
                                gson.fromJson(
                                    gson.toJson(
                                        clickUpClientService.getChildrenComments(
                                            taskHistory.getTaskId(),
                                            historyDTOComment,
                                            profile.getBaseUrl() + Constants.REPLY_ENDPOINT,
                                            tokenHistory.getToken()
                                        )
                                    ),
                                    TaskComments.class
                                );
                            childrenComments.put(commentId, taskComments);
                        } else {
                            taskComments = taskHistory.getChildrenComments().get(commentId);
                        }

                        if (Objects.nonNull(taskComments)) {
                            List<CommentDTO> children = taskComments.comments
                                .stream()
                                .map(comment -> {
                                    CommentDTO commentDTO = commentMapper.toCommentDTO(taskHistory, comment);
                                    if (Objects.nonNull(commentDTO.getResolved())) {
                                        if (Objects.isNull(map.get(commentDTO.getId()))) {
                                            map.put(commentDTO.getId(), commentDTO);
                                        }
                                    }
                                    taskDTO.addAttachments(commentDTO.getAttachments());
                                    return commentDTO;
                                })
                                .collect(Collectors.toList());
                            historyDTOComment.addChildren(children);
                        }

                        if (Objects.nonNull(historyDTOComment.getResolved())) {
                            if (Objects.isNull(map.get(commentId))) {
                                map.put(commentId, historyDTOComment);
                            }
                        }
                        allComments.add(historyDTOComment);
                        allComments.addAll(historyDTOComment.getChildren());
                        if ("attachment_comment".equals(historyDTO.getField())) {
                            taskDTO.addAttachments(historyDTOComment.getAttachments());
                            if (Objects.isNull(map.get(commentId))) {
                                map.put(commentId, historyDTOComment);
                            }
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else if (Constants.HIGHLIGHT_COMMENT_TYPES.contains(historyDTO.getField())) {
                    String commentId = history.comment.id;
                    if (Objects.isNull(map.get(commentId))) {
                        map.put(commentId, commentMapper.toCommentDTO(taskHistory, history));
                    }
                }

                return historyDTO;
            })
            .collect(Collectors.toList());

        taskHistory.setChildrenComments(childrenComments);
        Collections.reverse(historyDTOs);
        taskDTO.setHistories(historyDTOs);
        List<CommentDTO> highlightComments = allComments
            .stream()
            .filter(commentDTO -> map.containsKey(commentDTO.getId()))
            .sorted(Comparator.comparing(CommentDTO::getTimeStamp))
            .collect(Collectors.toList());
        taskDTO.setHighlightComments(highlightComments);

        String saveDirectory = taskHistory.getFullPath();
        String saveDirectoryPath = saveDirectory + "\\/";
        taskDTO.setBaseImagePath(saveDirectoryPath);
        FileUtils.createPathIfNotExists(saveDirectoryPath);
        taskDTO
            .getAttachments()
            .forEach(attachmentDTO -> {
                try {
                    FileUtils.downloadFile(attachmentDTO.getUrl(), saveDirectoryPath, attachmentDTO.getId());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            });

        exportPdf(taskDTO, saveDirectoryPath);
    }

    private TokenHistory getTokenAndHistory(Profile profile, String taskId) throws Exception {
        int tryTime = 0;
        JsonObject historyData;
        Header tokenHeader;
        Gson gson = new Gson();

        String historyEndpoint = profile.getBaseUrl() + Constants.TASK_ENDPOINT + taskId + "/history";
        do {
            if (StringUtils.isEmpty(profile.getToken()) || tryTime == 1) {
                TokenResponse tokenResponse = getTokenResponse(profile);
                tokenHeader = new BasicHeader("Authorization", "Bearer " + tokenResponse.token);
                historyData = gson.toJsonTree(clickUpClientService.getHistories(historyEndpoint, tokenHeader, null)).getAsJsonObject();
                profile.setToken(tokenResponse.token);
                profileRepository.saveAndFlush(profile);
            } else {
                tokenHeader = new BasicHeader("Authorization", "Bearer " + profile.getToken());
                historyData = gson.toJsonTree(clickUpClientService.getHistories(historyEndpoint, tokenHeader, null)).getAsJsonObject();
            }
            tryTime++;
        } while (tryTime == 1 && Objects.isNull(historyData));
        if (Objects.isNull(historyData) || tryTime > 1) {
            return null;
        }
        JsonArray histories = historyData.get("history").getAsJsonArray();
        String startId = histories.get(histories.size() - 1).getAsJsonObject().get("id").getAsString();
        JsonArray allHistories = new JsonArray();
        if (!historyData.get("last_page").getAsBoolean()) {
            while (!historyData.get("last_page").getAsBoolean()) {
                historyData = gson.toJsonTree(clickUpClientService.getHistories(historyEndpoint, tokenHeader, startId)).getAsJsonObject();
                JsonArray nextHistories = historyData.get("history").getAsJsonArray();
                histories.addAll(nextHistories);
                startId = nextHistories.get(nextHistories.size() - 1).getAsJsonObject().get("id").getAsString();
            }
        }

        explore(historyEndpoint, tokenHeader, allHistories, histories);

        return new TokenHistory(tokenHeader, allHistories);
    }

    private void explore(String historyEndpoint, Header tokenHeader, JsonArray allHistories, JsonArray histories)
        throws URISyntaxException {
        int index = 0;
        Gson gson = new Gson();
        while (index < histories.size()) {
            if ("collapsed_items".equals(histories.get(index).getAsJsonObject().get("field").getAsString())) {
                int count = histories.get(index).getAsJsonObject().get("count").getAsInt();
                String startId = null;
                String endId = null;
                if (index > 0) {
                    startId = histories.get(index - 1).getAsJsonObject().get("id").getAsString();
                }
                if (index < histories.size() - 1) {
                    endId = histories.get(index + 1).getAsJsonObject().get("id").getAsString();
                }
                JsonObject collapsedHistoryData = gson
                    .toJsonTree(clickUpClientService.getCollapsedHistories(historyEndpoint, tokenHeader, startId, endId))
                    .getAsJsonObject();
                if (Objects.nonNull(collapsedHistoryData) && !collapsedHistoryData.get("history").getAsJsonArray().isEmpty()) {
                    JsonArray collapsedHistories = collapsedHistoryData.get("history").getAsJsonArray();
                    explore(historyEndpoint, tokenHeader, allHistories, collapsedHistoryData.get("history").getAsJsonArray());
                    if (count > 10) {
                        startId = collapsedHistories.get(collapsedHistories.size() - 1).getAsJsonObject().get("id").getAsString();
                        JsonObject collapsedHistoryNestedData = gson
                            .toJsonTree(clickUpClientService.getCollapsedHistories(historyEndpoint, tokenHeader, startId, endId))
                            .getAsJsonObject();
                        if (
                            Objects.nonNull(collapsedHistoryNestedData) &&
                            !collapsedHistoryNestedData.get("history").getAsJsonArray().isEmpty()
                        ) {
                            explore(historyEndpoint, tokenHeader, allHistories, collapsedHistoryNestedData.get("history").getAsJsonArray());
                        }
                    }
                }
            } else {
                allHistories.add(histories.get(index));
            }
            index++;
        }
    }

    @Override
    public ZipFile exportPdfForTasks(Long profileId, List<String> taskIds) throws Exception {
        Profile profile = profileRepository
            .findById(profileId)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", "profile", "idnotfound"));
        ZipFile zipFile = new ZipFile(
            FileUtils.getOutputDirectoryForTargetAndTimestamp(
                baseFolder,
                "profile_" + profileId,
                String.valueOf(Instant.now().getEpochSecond())
            ) +
            ".zip"
        );
        for (String taskId : taskIds) {
            TaskHistory taskHistory = exportPdfForTask(profile, taskId);
            if (Objects.nonNull(taskHistory)) {
                DownloadHistory downloadHistory = new DownloadHistory();
                downloadHistory.setTaskId(taskId);
                downloadHistory.setProfile(profile);
                downloadHistory.setTimestamp(taskHistory.getTimestamp());
                downloadHistory.setTaskData(taskHistory.getOriginTaskData());
                downloadHistory.setHistoriesData(taskHistory.getOriginHistoriesData());
                if (!taskHistory.getChildrenComments().isEmpty()) {
                    downloadHistory.setChildrenCommentData(new Gson().toJson(taskHistory.getChildrenComments()));
                }
                downloadHistoryRepository.save(downloadHistory);
                File file = new File();
                file.setName(taskId + "_" + taskHistory.getTimestamp());
                file.setFileOnServer(taskId);
                file.setRelativePath(taskHistory.getRelativePath());
                file.setDownloadHistory(downloadHistory);
                fileRepository.save(file);
                zipFile.addFolder(taskHistory.getFolder());
                org.apache.commons.io.FileUtils.deleteQuietly(taskHistory.getFolder());
            }
        }
        return zipFile;
    }

    @Override
    public ZipFile exportPdfForHistory(Long historyId) throws Exception {
        DownloadHistory downloadHistory = downloadHistoryRepository
            .findById(historyId)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", "downloadHistory", "idnotfound"));
        String taskId = downloadHistory.getTaskId();
        Profile profile = downloadHistory.getProfile();
        ZipFile zipFile = new ZipFile(
            FileUtils.getOutputDirectoryForTargetAndTimestamp(
                baseFolder,
                "profile_" + profile.getId(),
                String.valueOf(Instant.now().getEpochSecond())
            ) +
            ".zip"
        );

        TaskHistory taskHistory = new TaskHistory(
            baseFolder,
            taskId,
            String.valueOf(Instant.now().getEpochSecond()),
            downloadHistory.getTaskData(),
            downloadHistory.getHistoriesData(),
            downloadHistory.getChildrenCommentData()
        );
        generateFromTaskDataAndTaskHistory(profile, null, taskHistory);
        zipFile.addFolder(taskHistory.getFolder());
        org.apache.commons.io.FileUtils.deleteQuietly(taskHistory.getFolder());
        return zipFile;
    }

    private TokenResponse getTokenResponse(Profile profile) {
        String encodedAuthString = getEncodedAuthString(profile.getUsername(), profile.getPassword());
        Header auth = new BasicHeader("Authorization", encodedAuthString);

        return clickUpClientService.postRequest(Constants.LOGIN_ENDPOINT, null, auth, TokenResponse.class);
    }

    private String getEncodedAuthString(String username, String password) {
        String authString = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));
    }

    private void exportPdf(TaskDTO taskDTO, String saveDirectory) throws IOException {
        byte[] results;
        ProcessBuilder builder = new ProcessBuilder(
            Constants.WKHTMLTOPDF_EXECUTE_FILE_PATH,
            "--keep-relative-links",
            "--encoding",
            "utf-8",
            "--log-level",
            "error",
            "--image-quality",
            "100",
            "--page-size",
            "Letter",
            "--margin-bottom",
            "7mm",
            "--margin-top",
            "7mm",
            "-",
            "-"
        );
        try {
            Process process = builder.start();
            try (BufferedOutputStream stdin = new BufferedOutputStream(process.getOutputStream())) {
                Locale locale = Locale.ENGLISH;
                Context context = new Context(locale);
                context.setVariable("task", taskDTO);

                String content = templateEngine.process("clickup/index.html", context);
                System.out.println(content);
                stdin.write(content.getBytes(StandardCharsets.UTF_8));
            }
            try (
                BufferedInputStream stdout = new BufferedInputStream(process.getInputStream());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ) {
                while (true) {
                    int x = stdout.read();
                    if (x == -1) {
                        break;
                    }
                    outputStream.write(x);
                }
                results = outputStream.toByteArray();
                process.waitFor();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        OutputStream os = Files.newOutputStream(Paths.get(saveDirectory + "\\" + taskDTO.getId() + ".pdf"));
        try {
            os.write(results);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
