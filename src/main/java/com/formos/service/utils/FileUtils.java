package com.formos.service.utils;

import com.formos.config.Constants;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;

public class FileUtils {

    public static final String SERVER_PATH = "/";

    public static final int DIR_MAX_DEEP = 4;

    public static final int DIR_MAX_SIZE = 1000;

    /**
     * Check if directory contained maximum number of files/sub-dir<br>
     * If directory is not existed, create it.
     *
     * @param dir
     * @return
     */
    private static boolean dirIsNotFullSize(File dir) {
        if (dir == null) {
            return false;
        } else if (!dir.exists()) {
            dir.mkdirs();
            return true;
        } else if (dir.isDirectory() && Objects.requireNonNull(dir.list()).length < DIR_MAX_SIZE) {
            return true;
        } else {
            return false;
        }
    }

    private static String getRelativePathAtStarting() {
        String path = "";
        int counter = 0;

        do {
            path += String.valueOf(1) + SERVER_PATH;
            counter++;
        } while (counter < DIR_MAX_DEEP);

        return path;
    }

    public static String getRelativePath(String rootPath, String path) {
        String relativePath = "";
        if (path == null || "".equals(path)) {
            relativePath = getRelativePathAtStarting();
        } else {
            relativePath = path;
        }

        File rootDir = new File(rootPath);
        File currentDir = new File(rootDir, relativePath);

        // If the latest dir is full, create the next one. Else the latest dir is available.
        if (!dirIsNotFullSize(currentDir)) {
            // Create next dir
            String arr[] = relativePath.split(SERVER_PATH);
            if (arr.length > 2) { // Relative path in DB is valid.
                int level1 = 1;
                int level2 = 1;
                int level3 = 1;
                int level4 = 1;
                try {
                    level1 = NumberUtils.parseInteger(arr[0]);
                    level2 = NumberUtils.parseInteger(arr[1]);
                    level3 = NumberUtils.parseInteger(arr[2]);
                    level4 = NumberUtils.parseInteger(arr[3]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                File level1Dir = new File(rootDir, String.valueOf(level1));
                File level2Dir = new File(level1Dir, String.valueOf(level2));
                File level3Dir = new File(level2Dir, String.valueOf(level3));

                boolean isRootFull = !dirIsNotFullSize(rootDir);
                boolean isLevel1Full = !dirIsNotFullSize(level1Dir);
                boolean isLevel2Full = !dirIsNotFullSize(level2Dir);
                boolean isLevel3Full = !dirIsNotFullSize(level3Dir);

                if (isLevel3Full) { // Level 3 is full, check level 2
                    if (isLevel2Full) { // Level 2 is full, check level 1
                        if (isLevel1Full) {
                            if (isRootFull) {
                                // TODO: pending issue
                            } else {
                                level1++;
                                level2 = 1;
                                level3 = 1;
                                level4 = 1;
                            }
                        } else { // level 1 is not full, create new dir at level 2
                            level2++;
                            level3 = 1;
                            level4 = 1;
                        }
                    } else { // Level 2 is not full, create new dir at level 3, reset level 4
                        level3++;
                        level4 = 1;
                    }
                } else { // Level 3 is not full, create new dir at level 4
                    level4++;
                }

                // Re-build relative path
                relativePath = level1 + SERVER_PATH + level2 + SERVER_PATH + level3 + SERVER_PATH + level4 + SERVER_PATH;
                File relativeDir = new File(rootDir, relativePath);
                if (!relativeDir.exists()) {
                    relativeDir.mkdirs();
                }
            }
        }

        return relativePath;
    }

    public static String getFullPath(String rootpath, String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append(rootpath);
        sb.append(File.separator);
        sb.append(fileName);
        return sb.toString();
    }

    public static String getFullPath(String rootpath, String fileName, Boolean isAddSeparator) {
        StringBuilder sb = new StringBuilder();
        sb.append(rootpath);
        if (Boolean.TRUE.equals(isAddSeparator)) {
            sb.append(File.separator);
        }
        sb.append(fileName);
        return sb.toString();
    }

    public static void downloadFile(String fileUrl, String saveDirectory, String fileName) throws MalformedURLException {
        URL url = new URL(fileUrl);
        String filePath = saveDirectory + "/" + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            System.out.println("File already exists. Skipping download.");
        } else {
            try (
                InputStream in = new BufferedInputStream(url.openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(filePath)
            ) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer, 0, buffer.length)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> readListFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            return new ArrayList<>(Files.readAllLines(path));
        } else {
            Files.createFile(path);
            return new ArrayList<>();
        }
    }

    public static void writeTokenToFile(String filePath, String token) throws IOException {
        Path path = Paths.get(filePath);
        Files.writeString(path, token);
    }

    public static String getCurrentPackage(Class<?> inputClass) {
        Package currentPackage = inputClass.getPackage();
        return currentPackage.getName();
    }

    public static String getCurrentSourcePath() {
        File currentDirectory = new File("");
        return currentDirectory.getAbsolutePath();
    }

    public static String getInputFilePath() {
        return getCurrentSourcePath() + "\\src\\clickup\\input\\" + Constants.INPUT_TASK_IDS_FILE_NAME;
    }

    public static String getTokenFilePath() {
        return getCurrentSourcePath() + "\\src\\clickup\\input\\" + Constants.INPUT_TOKEN_FILE_NAME;
    }

    //    public static String getOutputDirectoryForTask(String taskId) {
    //        return FileUtils.getCurrentSourcePath() + "\\src\\clickup\\output\\" + taskId + "\\";
    //    }

    public static String getOutputDirectoryForTaskHistory(String baseFolder, String taskId, String timestamp) {
        return (
            FileUtils.getCurrentSourcePath() +
            "\\/" +
            baseFolder +
            "\\/" +
            FileUtils.getRelativePath(baseFolder, null) +
            taskId +
            "_" +
            timestamp
        );
    }

    public static void createPathIfNotExists(String savePath) {
        try {
            Path path = Paths.get(savePath);
            if (Files.exists(path)) {
                System.out.println("Existing directory");
            } else {
                Files.createDirectories(path);
                System.out.println("Directory created successfully.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
