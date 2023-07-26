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
import com.formos.service.mapper.TaskMapper;
import com.formos.service.utils.FileUtils;
import com.formos.web.rest.errors.BadRequestAlertException;
import java.io.*;
import java.net.MalformedURLException;
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
        AttachmentMapper attachmentMapper
    ) {
        this.templateEngine = templateEngine;
        this.clickUpClientService = clickUpClientService;
        this.profileRepository = profileRepository;
        this.fileRepository = fileRepository;
        this.downloadHistoryRepository = downloadHistoryRepository;
        this.taskMapper = taskMapper;
        this.commentMapper = commentMapper;
        this.attachmentMapper = attachmentMapper;
    }

    @Override
    public TaskHistory exportPdfForTask(Profile profile, String taskId) throws Exception {
        Header authorization = new BasicHeader("Authorization", profile.getApiKey());

        String taskApiEndPoint = Constants.TASK_API_ENDPOINT + taskId;
        String taskCommentsEndPoint = taskApiEndPoint + "/comment";

        String taskEndpoint = profile.getBaseUrl() + Constants.TASK_ENDPOINT + taskId;

        TokenHistory tokenHistory = getTokenAndHistory(profile, taskId);

        if (Objects.isNull(tokenHistory)) {
            return null;
        }

        String currentTime = String.valueOf(Instant.now().getEpochSecond());
        TaskData taskData = clickUpClientService.getTask(taskEndpoint, tokenHistory.getToken());
        HistoryData historyData = tokenHistory.getHistoryData();
        List<History> historyComments = historyData
            .getHistory()
            .stream()
            .filter(history -> Constants.COMMENT_TYPES.contains(history.field))
            .collect(Collectors.toList());

        TaskHistory taskHistory = new TaskHistory(baseFolder, taskId, currentTime);

        Map<String, CommentDTO> map = new HashMap<>();
        for (History history : historyComments) {
            TaskComments.Comment comment = history.comment;
            if (Objects.isNull(map.get(comment.id))) {
                map.put(comment.id, commentMapper.toCommentDTO(taskHistory, comment));
            }
        }

        TaskDTO taskDTO = taskMapper.toTaskDTO(profile, taskData, taskHistory);

        TaskComments taskComments = clickUpClientService.getRequest(taskCommentsEndPoint, null, authorization, TaskComments.class);
        List<CommentDTO> commentDTOs = taskComments.comments
            .stream()
            .map(comment -> commentMapper.processAddHistory(taskHistory, map, comment))
            .collect(Collectors.toList());
        Collections.reverse(commentDTOs);

        for (CommentDTO comment : commentDTOs) {
            List<CommentDTO> children = clickUpClientService
                .getChildrenComments(taskId, map, comment, profile.getBaseUrl() + Constants.REPLY_ENDPOINT, tokenHistory.getToken())
                .comments.stream()
                .map(commentChild -> commentMapper.processAddHistory(taskHistory, map, commentChild))
                .collect(Collectors.toList());
            Collections.reverse(children);
            Set<AttachmentDTO> attachmentDTOS = children
                .stream()
                .flatMap(child ->
                    child
                        .getCommentItems()
                        .stream()
                        .filter(childItem -> Objects.nonNull(childItem.getAttachmentId()))
                        .map(item -> attachmentMapper.toAttachmentDTO(comment.getDateTime(), item))
                )
                .collect(Collectors.toSet());
            taskDTO.addAttachments(attachmentDTOS);
            comment.addChildren(children);
        }

        taskDTO.setComments(commentDTOs);
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

        taskHistory.setFolder(new java.io.File(saveDirectory));

        return taskHistory;
    }

    private TokenHistory getTokenAndHistory(Profile profile, String taskId) throws Exception {
        int tryTime = 0;
        HistoryData historyData;
        Header tokenHeader;
        String historyEndpoint = profile.getBaseUrl() + Constants.TASK_ENDPOINT + taskId + "/history";
        do {
            if (StringUtils.isEmpty(profile.getToken()) || tryTime == 1) {
                TokenResponse tokenResponse = getTokenResponse(profile);
                tokenHeader = new BasicHeader("Authorization", "Bearer " + tokenResponse.token);
                historyData = clickUpClientService.getHistories(historyEndpoint, tokenHeader);
                profile.setToken(tokenResponse.token);
                profileRepository.saveAndFlush(profile);
            } else {
                tokenHeader = new BasicHeader("Authorization", "Bearer " + profile.getToken());
                historyData = clickUpClientService.getHistories(historyEndpoint, tokenHeader);
            }
            tryTime++;
        } while (tryTime == 1 && Objects.isNull(historyData));
        if (Objects.isNull(historyData) || tryTime > 1) {
            return null;
        }
        return new TokenHistory(tokenHeader, historyData);
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
