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

public class FileUtils {

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
        Files.write(path, token.getBytes(StandardCharsets.UTF_8));
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

    public static String getOutputDirectoryForTask(String taskId) {
        return FileUtils.getCurrentSourcePath() + "\\src\\clickup\\output\\" + taskId + "\\";
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
