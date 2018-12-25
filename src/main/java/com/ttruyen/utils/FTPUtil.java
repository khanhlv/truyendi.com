package com.ttruyen.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FTPUtil {

    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private FTPClient ftpClient;

    public FTPUtil(String host, String user, String pwd) throws Exception {
        ftpClient = new FTPClient();
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        int reply;
        ftpClient.connect(host);
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            throw new Exception("Exception in connecting to FTP Server");
        }
        ftpClient.login(user, pwd);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();
    }

    public void uploadFile(String localFileFullName, String fileName, String hostDir) throws Exception {
        try (InputStream input = new FileInputStream(new File(localFileFullName))) {
            this.ftpClient.storeFile(hostDir + fileName, input);
        }
    }

    public void uploadFile(InputStream input, String fileName, String hostDir) throws Exception {
        if (null == this.ftpClient || !this.ftpClient.isConnected()) {
            return;
        }

        this.ftpClient.storeFile(hostDir + fileName, input);

    }

    public boolean downloadFile(File fileOutput, String fileName, String hostDir) throws Exception {

        if (null == this.ftpClient || !this.ftpClient.isConnected()) {
            return false;
        }

        FileOutputStream fileOutputStream = new FileOutputStream(fileOutput);

        return this.ftpClient.retrieveFile(hostDir + fileName, fileOutputStream);
    }

    public void existsFile(String hostDir, String fileName) throws Exception {
        FTPFile[] files = this.ftpClient.listFiles(hostDir);

        System.out.println(Arrays.stream(files).filter((file) -> {
            return file.getName().equals(fileName);
        }).count());
    }

    public boolean makeDirectories(String hostDir, String folderPath) throws IOException {
        if (null == this.ftpClient || !this.ftpClient.isConnected()) {
            return false;
        }

        String[] pathElements = folderPath.split("/");

        StringBuilder path = new StringBuilder();
        path.append(hostDir);

        if (pathElements != null && pathElements.length > 0) {
            for (String dir : pathElements) {
                if (StringUtils.isNotBlank(dir)) {
                    path.append("/" + dir);
                    boolean existed = this.ftpClient.changeWorkingDirectory(path.toString());
                    if (!existed) {
                        boolean created = this.ftpClient.makeDirectory(path.toString());
                        if (!created) {
                            System.out.println("COULD NOT create directory: " + path.toString());
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean checkDirectoryExists(String dirPath) throws IOException {
        if (null == this.ftpClient || !this.ftpClient.isConnected()) {
            return false;
        }

        boolean hasExists = ftpClient.changeWorkingDirectory(dirPath);

        int returnCode = ftpClient.getReplyCode();

        if (!hasExists && returnCode == 550) {
            return false;
        }

        return true;
    }

    public FTPClient getFtpClient() {
        return this.ftpClient;
    }

    public boolean checkFileExists(String filePath) throws IOException {
        if (null == this.ftpClient || !this.ftpClient.isConnected()) {
            return false;
        }

        InputStream inputStream = this.ftpClient.retrieveFileStream(filePath);

        int returnCode = this.ftpClient.getReplyCode();

        if (inputStream == null || returnCode == 550) {
            return false;
        }

        return true;
    }

    public void disconnect() {
        if (this.ftpClient.isConnected()) {
            try {
                this.ftpClient.logout();
                this.ftpClient.disconnect();
            } catch (IOException ex) {
                logger.error("Disconnect FTP Error", ex);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Start");

        FTPUtil ftpUploader = new FTPUtil("ttruyen.com", "ttruyen.com", "@");
        //ftpUploader.uploadFile("/Users/khanhlv/Downloads/demo.zip", "demo.zip", "\\httpdocs\\");
//        ftpUploader.makeDirectories("/httpdocs", "/data2/data3");
        boolean exists = ftpUploader.checkFileExists("/httpdocs/data/801272.txt.gz");
        System.out.println(exists);
//        System.out.println(ftpUploader.checkDirectoryExists("/httpdocs/11"));
//        ftpUploader.existsFile("/httpdocs/data", "801272.txt.gz");
        ftpUploader.disconnect();
        System.out.println("Done");
    }
}
