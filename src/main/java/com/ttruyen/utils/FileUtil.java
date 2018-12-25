package com.ttruyen.utils;

import com.ttruyen.core.UserAgent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static List<File> listFileEmpy(String path) {
        List<File> listFileEmpy = new ArrayList<>();
        File file = new File(path);
        if (file.isDirectory()) {
            File[] listFile = file.listFiles();

            for (File itemFile : listFile) {
                if (itemFile.length() <= 20) {
                    listFileEmpy.add(itemFile);
                }
            }
        }

        return listFileEmpy;
    }

    public static void downloadImage(String url) throws Exception {
        URL urlFile = new URL("http://cdnvn.truyenfull.vn/cover/eJzLyTDWd_Mprcxwc0n1CXB0yg6PMKoKcSz3yLf0MfBILEszy8wz9Td2do33SYz0DsxxzE-uKkh2NgrLcPTNdM0OqcpOSg4oKcrQjSgJt8h2ineq8nYM8He0LTcyNNXNMDYyAgAtlx-z/trong-sinh-chi-nhat-pham-phu-nhan.jpg");

        InputStream inputStream = urlFile.openStream();

        FTPUtil ftpUploader = new FTPUtil("ttruyen.com", "ttruyen.com", "123qwe@");

        ftpUploader.uploadFile(inputStream, "trong-sinh-chi-nhat-pham-phu-nhan.jpg", "/httpdocs/images/");

        ftpUploader.disconnect();
    }

    public static boolean checkFileGZIP(String url, int contentLength) throws IOException {
        Connection.Response response = Jsoup.connect(url).ignoreContentType(true).userAgent(UserAgent.getUserAgent()).execute();

        boolean flagContentLength = StringUtils.equals(response.header("Content-Length"), String.valueOf(contentLength));
        boolean flagContentType = StringUtils.equals(response.header("Content-Type"), "application/x-gzip");
        boolean flagAcceptRanges = StringUtils.equals(response.header("Accept-Ranges"), "bytes");

        return (flagContentLength && flagContentType && flagAcceptRanges);
    }

    public static void main(String[] args) throws Exception {
//        File f = new File("/Users/khanhlv/ttruyen/data/78875.txt.gz");
//        System.out.println(f .length());
//        List<File> listFile = listFileEmpy("/Users/khanhlv/ttruyen/data/");
//        System.out.println(checkFile("http://ttruyen.com/data/801328.txt.gz", 5609));
//        System.out.println(listFileEmpy("/Users/khanhlv/ttruyen/data/").size());

        downloadImage("");
    }
}
