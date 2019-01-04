package com.ttruyen.execute;

import com.google.api.services.drive.Drive;
import com.ttruyen.utils.GoogleDriverUtil;

public class StartThreadChapter {
    public static void main(String[] args) throws Exception {

        Drive.Files driveFiles = GoogleDriverUtil.driveFiles();

        new Thread(new ThreadShareQueue()).start();

        new Thread(new ThreadChapter(driveFiles, 1)).start();
        new Thread(new ThreadChapter(driveFiles, 2)).start();
        new Thread(new ThreadChapter(driveFiles, 3)).start();
        new Thread(new ThreadChapter(driveFiles, 4)).start();
        new Thread(new ThreadChapter(driveFiles, 5)).start();
    }
}
