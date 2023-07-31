package com.formos.config;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "en";

    public static final Set<String> SINGLE_BLOCK_TYPES = Stream
        .of("attachment", "emoticon", "userMention", "divider", "taskImage", "taskMention", "tag")
        .collect(Collectors.toSet());

    public static final List<String> IMAGE_EXTENSION = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp");
    public static final Set<String> CSS_ATTRIBUTES = Stream
        .of(
            "bold",
            "italic",
            "underline",
            "header",
            "strike",
            "color_class",
            "align",
            "indent",
            "code",
            "blockQuote",
            "advancedBannerColor"
        )
        .collect(Collectors.toSet());

    public static final Set<String> COMMENT_TYPES = Stream.of("comment", "attachment_comment").collect(Collectors.toSet());
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String MMM_DD_AT_H_MM_A = "MMM dd 'at' h:mm a";

    public static final String TASK_API_ENDPOINT = "https://api.clickup.com/api/v2/task/";
    public static final String LOGIN_ENDPOINT = "https://app.clickup.com/auth/v1/login";
    public static final String TASK_URL_PREFIX = "https://app.clickup.com/t/";

    /**
     * Constants endpoint for Production
     */
    public static final String TASK_ENDPOINT = "/tasks/v1/task/";
    public static final String REPLY_ENDPOINT = "/comments/v2/comment";
    //        public static final String TASK_ENDPOINT = "https://app.clickup.com/tasks/v1/task/";
    //        public static final String REPLY_ENDPOINT = "https://app.clickup.com/comments/v2/comment";

    public static final String ATTACHMENT_COMMENT = "attachment_comment";

    public static final Set<String> HIGHLIGHT_COMMENT_TYPES = Stream.of("comment_assigned", "comment_resolved").collect(Collectors.toSet());

    public static final String INPUT_TOKEN_FILE_NAME = "token.txt";

    // The input task ids
    public static final String INPUT_TASK_IDS_FILE_NAME = "taskIds.txt";

    /**
     * Constants for Configure
     */
    public static final String WKHTMLTOPDF_EXECUTE_FILE_PATH = "C:\\Program Files\\wkhtmltopdf\\bin\\wkhtmltopdf.exe";

    private Constants() {}
}
