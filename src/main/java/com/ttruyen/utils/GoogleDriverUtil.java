package com.ttruyen.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.ttruyen.model.Content;
import com.ttruyen.parse.ParseTruyenFull;

public final class GoogleDriverUtil {
    public static final String APPLICATION_NAME = "Google Drive TruyenDi";
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    public static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    // private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);
    private static final List<String> SCOPES = new ArrayList<>();
    static {
        SCOPES.add(DriveScopes.DRIVE);
        SCOPES.add(DriveScopes.DRIVE_FILE);
        SCOPES.add(DriveScopes.DRIVE_APPDATA);
    }
//    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String CREDENTIALS_FILE_PATH = "/credentials_truyendi.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleDriverUtil.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        java.io.File filePath = new java.io.File(GoogleDriverUtil.class.getResource(CREDENTIALS_FILE_PATH).getFile());

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(filePath.getParent() + "/" +  TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     *
     * @param files
     * @param filePath
     * @param contentType
     * @param folderId
     * @return
     * @throws Exception
     */
    public static String uploadFile(Drive.Files files, java.io.File filePath, String contentType, String folderId) throws Exception {
        File fileMetadata = new File();
        fileMetadata.setName(filePath.getName());

        if (StringUtils.isNotBlank(folderId)) {
            fileMetadata.setParents(Collections.singletonList(folderId));
        }

        FileContent mediaContent = new FileContent(contentType, filePath);

        File file = files.create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
        System.out.println("FileID: " + file.getId());

        return file.getId();
    }

    /**
     *
     * @param drive
     * @param data
     * @param fileName
     * @param folderId
     * @return
     * @throws Exception
     */
    public static String uploadFile(Drive.Files files, byte[] data, String fileName, String folderId) throws Exception {
        File fileMetadata = new File();
        fileMetadata.setName(fileName);

        if (StringUtils.isNotBlank(folderId)) {
            fileMetadata.setParents(Collections.singletonList(folderId));
        }

        ByteArrayContent byteArrayContent = new ByteArrayContent(null, data);

        File file = files.create(fileMetadata, byteArrayContent)
                .setFields("id")
                .execute();

        System.out.println("FILE_ID: " + file.getId());

        return file.getId();
    }

    /**
     *
     * @param files
     * @param content
     * @param fileName
     * @param folderId
     * @return
     * @throws Exception
     */
    public static String uploadFile(Drive.Files files, String content, String fileName, String folderId) throws Exception {
        return uploadFile(files, content.getBytes("UTF-8"), fileName, folderId);
    }

    /**
     *
     * @param drive
     * @param inputStream
     * @param fileName
     * @param folderId
     * @return
     * @throws Exception
     */
    public static String uploadFile(Drive.Files files, InputStream inputStream, String fileName, String folderId) throws Exception {
        return uploadFile(files, IOUtils.toByteArray(inputStream), fileName, folderId);
    }

    /**
     *
     * @param files
     * @param fileId
     * @return
     * @throws Exception
     */
    public static InputStream downloadFileInputStream(Drive.Files files, String fileId) throws Exception {
        return files.get(fileId).executeMediaAsInputStream();
    }

    /**
     *
     * @param files
     * @param fileId
     * @return
     * @throws Exception
     */
    public static ByteArrayOutputStream downloadFileOutputStream(Drive.Files files, String fileId) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        files.get(fileId).executeMediaAndDownloadTo(outputStream);
        return outputStream;
    }

    /**
     *
     * @param files
     * @param fileId
     * @return
     * @throws Exception
     */
    public static String readFileGZip(Drive.Files files, String fileId) throws Exception {
        InputStream inputStream = downloadFileInputStream(files, fileId);
        System.out.println("Bytes: " + inputStream.available());
        if (inputStream.available() > 0) {
            return GZipUtil.decompressGZIP(inputStream);
        }

        return StringUtils.EMPTY;
    }

    /**
     *
     * @param files
     * @param folder
     * @throws Exception
     */
    public static void createFolder(Drive.Files files, String folder) throws Exception {
        File fileMetadata = new File();
        fileMetadata.setName(folder);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        File file = files.create(fileMetadata)
                .setFields("id")
                .execute();

        System.out.println("Folder ID: " + file.getId());
    }

    /**
     *
     * @param files
     * @param fileId
     * @throws Exception
     */
    public static void deleteFile(Drive.Files files, String fileId) throws Exception {

        files.delete(fileId).execute();

        System.out.println("File ID: " + fileId);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public static Drive driveService() throws Exception {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public static Drive.Files driveFiles() throws Exception {
        return driveService().files();
    }

    /**
     *
     * @param files
     * @throws Exception
     */
    public static void listFiles(Drive.Files files) throws Exception {
        FileList result = files.list()
                .setPageSize(100)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> listFile = result.getFiles();

        if (listFile == null || listFile.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : listFile) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }
    }

    public static void main(String... args) throws Exception {

        Drive drive = GoogleDriverUtil.driveService();

        ParseTruyenFull parseTruyenFull = new ParseTruyenFull();
        Content content = parseTruyenFull.readContent("https://truyenfull.vn/thuong-thien/chuong-797/");;

//        GoogleDriverUtil.uploadFile(drive,  new java.io.File("D:/text.txt.gz"), "application/gzip", "1-WvE-4xcD81drSN_5bnOFpAj73rX_RHN");

        InputStream inputStream = GZipUtil.compress(content.getContent());
//
        System.out.println(inputStream.available());
//        GoogleDriverUtil.uploadFile(drive, inputStream, "khanh.txt.gz","1-WvE-4xcD81drSN_5bnOFpAj73rX_RHN");

//        GoogleDriverUtil.listFiles(drive);

        System.out.println(GoogleDriverUtil.readFileGZip(drive.files(), "1Ca1IJtQARpUztqcTTIRGlVSTbw7AXdgZ"));

    }
}
