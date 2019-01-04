package com.ttruyen.execute;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.drive.Drive;
import com.ttruyen.core.ShareQueue;
import com.ttruyen.db.ChapterDAO;
import com.ttruyen.db.StoryDAO;
import com.ttruyen.model.Content;
import com.ttruyen.parse.ParseTruyenFull;
import com.ttruyen.utils.GZipUtil;
import com.ttruyen.utils.GoogleDriverUtil;
import com.ttruyen.utils.StringUtil;

public class ThreadChapter implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ThreadChapter.class);
    private ChapterDAO chapterDAO = new ChapterDAO();
    private StoryDAO storyDAO = new StoryDAO();
    private ParseTruyenFull parseTruyenFull = new ParseTruyenFull();
    private Drive.Files driveFiles;
    private String threadName = "THREAD_";

    public ThreadChapter(Drive.Files driveFiles, int threadCount) {
        this.driveFiles = driveFiles;
        this.threadName = this.threadName + threadCount;
        System.out.println("START_THREAD_" + threadCount);
    }

    @Override
    public void run() {
        try {
            while (true) {
                List<String> listContent = ShareQueue.getItem();

                System.out.println(this.threadName + " ## LINK_SIZE [" + listContent.size() + "]");

                for(String data : listContent){
                    String[] str = StringUtils.split(data,"\\|");
                    int id = NumberUtils.toInt(str[0]);
                    String link = str[1];
                    try {

                        logger.info(this.threadName + " ## GET_START [URL=" + link + "]");

//                        long start = System.currentTimeMillis();
//                        Content content = parseTruyenFull.readContent(link);
//                        content.setId(id);
//                        long end = System.currentTimeMillis() - start;
//
//                        logger.info(this.threadName + " ## GET_END [URL=" + link + "][TIME=" + end  + "]");
//
//                        if (StringUtils.isBlank(content.getContent())) {
//                            chapterDAO.updateStatus(id, 2);
//                        } else {
//                            InputStream inputStream = GZipUtil.compress(content.getContent());
//                            int fileSize = inputStream.available();
//
//                            String fileId = GoogleDriverUtil.uploadFile(driveFiles, inputStream, id + ".txt.gz", "1-WvE-4xcD81drSN_5bnOFpAj73rX_RHN");
//
//                            System.out.println("[SIZE=" + fileSize + "][FILE_ID=" + fileId + "]");
//
//                            content.setFileName(fileId);
//                            content.setContent(fileId);
//
//                            chapterDAO.updateChapterContent(content);
//
//                            chapterDAO.updateStatus(id, 1);
//                        }

                    } catch (Exception ex) {
                        logger.error(this.threadName + " ## ERROR[" + link + "]", ex);
//                        chapterDAO.updateStatus(id, -1);
                    }
                }

                Thread.sleep( 5000);
            }
        } catch (Exception ex) {
            logger.error("ERROR[ThreadChapter]", ex);
        }
    }
}
