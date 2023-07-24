package com.formos.service.impl;

import com.formos.config.Constants;
import com.formos.domain.Profile;
import com.formos.repository.ProfileRepository;
import com.formos.service.ClickUpClientService;
import com.formos.service.ClickUpService;
import com.formos.service.dto.clickup.*;
import com.formos.service.mapper.CommentMapper;
import com.formos.service.mapper.TaskMapper;
import com.formos.service.utils.FileUtils;
import com.formos.web.rest.errors.BadRequestAlertException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.util.StringUtils;

@Service
public class ClickUpServiceImpl implements ClickUpService {

    private final Logger log = LoggerFactory.getLogger(ClickUpServiceImpl.class);
    private final SpringTemplateEngine templateEngine;
    private final ClickUpClientService clickUpClientService;

    private final ProfileRepository profileRepository;
    private final TaskMapper taskMapper;
    private final CommentMapper commentMapper;

    public ClickUpServiceImpl(
        SpringTemplateEngine templateEngine,
        ClickUpClientService clickUpClientService,
        ProfileRepository profileRepository,
        TaskMapper taskMapper,
        CommentMapper commentMapper
    ) {
        this.templateEngine = templateEngine;
        this.clickUpClientService = clickUpClientService;
        this.profileRepository = profileRepository;
        this.taskMapper = taskMapper;
        this.commentMapper = commentMapper;
    }

    @Override
    public void exportPdfForTask(Long profileId, String taskId) throws Exception {
        Profile profile = profileRepository.findById(profileId).orElseThrow(Exception::new);
        Header authorization = new BasicHeader("Authorization", profile.getApiKey());

        String taskApiEndPoint = Constants.TASK_API_ENDPOINT + taskId;
        String taskCommentsEndPoint = taskApiEndPoint + "/comment";

        //        Task task = ClickUpCall.getRequest(taskApiEndPoint, null, authorization, Task.class);
        String taskEndpoint = profile.getBaseUrl() + Constants.TASK_ENDPOINT + taskId;
        //        TaskData taskData = ClickUpCall.getTask(taskEndpoint, new BasicHeader("Authorization", "Bearer " + token));

        int tryTime = 0;
        TaskData taskData;
        Header tokenHeader;
        do {
            if (StringUtils.isEmpty(profile.getToken()) || tryTime == 1) {
                TokenResponse tokenResponse = getTokenResponse(profile);
                tokenHeader = new BasicHeader("Authorization", "Bearer " + tokenResponse.token);
                taskData = clickUpClientService.getTask(taskEndpoint, tokenHeader);
                profile.setToken(tokenResponse.token);
                profileRepository.saveAndFlush(profile);
            } else {
                tokenHeader = new BasicHeader("Authorization", "Bearer " + profile.getToken());
                taskData = clickUpClientService.getTask(taskEndpoint, tokenHeader);
            }
            tryTime++;
        } while (tryTime == 1 && Objects.isNull(taskData));

        //        TaskDTO taskDTO = new TaskDTO(task);
        TaskDTO taskDTO = taskMapper.toTaskDTO(profile, taskData);

        String historyEndpoint = profile.getBaseUrl() + Constants.TASK_ENDPOINT + taskId + "/history";
        HistoryData historyData = clickUpClientService.getHistories(historyEndpoint, tokenHeader);
        List<History> historyComments = historyData
            .getHistory()
            .stream()
            .filter(history -> Constants.COMMENT_TYPES.contains(history.field))
            .collect(Collectors.toList());
        Map<String, CommentDTO> map = new HashMap<>();
        for (History history : historyComments) {
            TaskComments.Comment comment = history.comment;
            if (Objects.isNull(map.get(comment.id))) {
                map.put(comment.id, commentMapper.toCommentDTO(taskId, comment));
            }
        }

        TaskComments taskComments = clickUpClientService.getRequest(taskCommentsEndPoint, null, authorization, TaskComments.class);
        List<CommentDTO> commentDTOs = taskComments.comments
            .stream()
            .map(comment -> commentMapper.processAddHistory(taskId, map, comment))
            .collect(Collectors.toList());
        Collections.reverse(commentDTOs);

        for (CommentDTO comment : commentDTOs) {
            List<CommentDTO> children = clickUpClientService
                .getChildrenComments(taskId, map, comment, profile.getBaseUrl() + Constants.REPLY_ENDPOINT, tokenHeader)
                .comments.stream()
                .map(commentChild -> commentMapper.processAddHistory(taskId, map, commentChild))
                .collect(Collectors.toList());
            Collections.reverse(children);
            Set<AttachmentDTO> attachmentDTOS = children
                .stream()
                .flatMap(child ->
                    child
                        .getCommentItems()
                        .stream()
                        .filter(childItem -> Objects.nonNull(childItem.getAttachmentId()))
                        .map(item -> new AttachmentDTO(comment.getDateTime(), item))
                )
                .collect(Collectors.toSet());
            taskDTO.addAttachments(attachmentDTOS);
            comment.addChildren(children);
        }

        taskDTO.setComments(commentDTOs);
        taskDTO.setBaseImagePath(FileUtils.getOutputDirectoryForTask(taskId));
        String saveDirectory = FileUtils.getOutputDirectoryForTask(taskDTO.getId());
        FileUtils.createPathIfNotExists(saveDirectory);
        taskDTO
            .getAttachments()
            .forEach(attachmentDTO -> {
                try {
                    FileUtils.downloadFile(attachmentDTO.getUrl(), saveDirectory, attachmentDTO.getId());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            });

        exportPdf(taskDTO, saveDirectory);
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
