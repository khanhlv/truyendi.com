package com.ttruyen.execute;

import com.google.api.services.drive.Drive;
import com.ttruyen.utils.GoogleDriverUtil;

public class StartThreadChapter {

    public void execute(int threadCount) throws Exception {
        Drive.Files driveFiles = GoogleDriverUtil.driveFiles();

        new Thread(new ThreadShareQueue()).start();
        Thread.sleep(5000);

        for (int i = 1; i <= threadCount; i++) {
            new Thread(new ThreadChapter(driveFiles, i)).start();
            Thread.sleep(5000);
        }
    }

    public static void main(String[] args) throws Exception {
        new StartThreadChapter().execute(4);
    }
}
