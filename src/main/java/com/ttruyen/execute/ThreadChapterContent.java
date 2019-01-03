package com.ttruyen.execute;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;
import com.ttruyen.core.Const;
import com.ttruyen.db.ChapterDAO;
import com.ttruyen.db.StoryDAO;
import com.ttruyen.model.Content;
import com.ttruyen.parse.ParseTruyenFull;
import com.ttruyen.utils.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ThreadChapterContent implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ThreadChapterContent.class);

    private ChapterDAO chapterDAO = new ChapterDAO();
    private StoryDAO storyDAO = new StoryDAO();
    private ParseTruyenFull parseTruyenFull = new ParseTruyenFull();

    private void crawlerContentWithChapter() {
        try {

            while (true) {
                FTPUtil ftpUploader = null;

                ArrayList<Integer> listId = new ArrayList<>();

                List<Content> contentList = chapterDAO.selectTop(Const.TOP_CHAPTER);

                if (contentList != null && contentList.size() > 0) {
                    ftpUploader = new FTPUtil("ttruyen.com", "ttruyen.com", "@");
                }

                if (ftpUploader != null && ftpUploader.getFtpClient().isConnected()) {
                    Date date =  new Date();
                    for(Content data : contentList){
                        try {

                            logger.info("GET_START [URL=" + data.getLink() + "]");

                            long start = System.currentTimeMillis();
                            Content content = parseTruyenFull.readContent(data.getLink());
                            content.setId(data.getId());

                            long end = System.currentTimeMillis() - start;

                            logger.info("GET_END [URL=" + data.getLink() + "][TIME=" + end  + "]");

                            if (StringUtils.isBlank(content.getContent())) {
                                chapterDAO.updateStatus(data.getId(), 2);
                            } else {
                                InputStream inputStream = GZipUtil.compress(content.getContent());

                                int fileSize = inputStream.available();
                                System.out.println("[SIZE=" + fileSize + "]");
                                content.setFileName(data.getId() + ".txt.gz");
                                content.setContent(data.getId() + ".txt.gz");
                                chapterDAO.updateChapterContent(content);

                                if (!listId.contains(data.getId())) {
                                    date = DateUtils.addSeconds(date, -10);
                                    listId.add(data.getId());
                                }

                                storyDAO.updatCREATED_DATE(data.getStoryId(), DateUtil.createDateTimestamp(date));

                                ftpUploader.uploadFile(inputStream, content.getFileName(), "/httpdocs/data/");

                                logger.info("REPLY CODE [" + ftpUploader.getFtpClient().getReplyCode() + "]");

                                if (ftpUploader.getFtpClient().getReplyCode() == 226 && FileUtil.checkFileGZIP("http://ttruyen.com/data/" + content.getFileName(), fileSize)) {
                                    logger.info("COMPLETE UPLOAD FILE [" + content.getFileName() + "]");
                                    chapterDAO.updateStatus(data.getId(), 1);
                                } else {
                                    logger.info("NOT EXISTS FILE [" + content.getFileName() + "]");
                                    chapterDAO.updateStatus(data.getId(), 0);
                                }
                            }

                        } catch (Exception ex) {
                            logger.error("ERROR[" + data.getLink() + "]", ex);
                            chapterDAO.updateStatus(data.getId(), -1);
                        }
                    }

                    ftpUploader.disconnect();
                }
                System.out.println("CHAPTER_SIZE [" + contentList.size() + "]");

                Thread.sleep(1 * 30 * 1000);
            }
        } catch (Exception ex) {
            logger.error("ERROR[crawlerChapterWithStory]", ex);
        }
    }

    private void crawlerContentWithChapterGoogle() {
        try {

            Drive drive = GoogleDriverUtil.driveService();

            while (true) {

                ArrayList<Integer> listId = new ArrayList<>();

                List<Content> contentList = chapterDAO.selectTop(Const.TOP_CHAPTER);

                if (contentList == null || contentList.size() == 0) {
                    return;
                }

                if (drive != null) {
                    Date date =  new Date();
                    for(Content data : contentList){
                        try {

                            logger.info("GET_START [URL=" + data.getLink() + "]");

                            long start = System.currentTimeMillis();
                            Content content = parseTruyenFull.readContent(data.getLink());
                            content.setId(data.getId());

                            long end = System.currentTimeMillis() - start;

                            logger.info("GET_END [URL=" + data.getLink() + "][TIME=" + end  + "]");

                            if (StringUtils.isBlank(content.getContent())) {
                                chapterDAO.updateStatus(data.getId(), 2);
                            } else {
                                InputStream inputStream = GZipUtil.compress(content.getContent());

                                String fileId = GoogleDriverUtil.uploadFile(drive, inputStream, data.getId() + ".txt.gz", "1-WvE-4xcD81drSN_5bnOFpAj73rX_RHN");

                                int fileSize = inputStream.available();
                                System.out.println("[SIZE=" + fileSize + "][FILE_ID=" + fileId + "]");

                                content.setFileName(fileId);
                                content.setContent(fileId);

                                chapterDAO.updateChapterContent(content);

                                if (!listId.contains(data.getId())) {
                                    date = DateUtils.addSeconds(date, -10);
                                    listId.add(data.getId());
                                }

                                storyDAO.updatCREATED_DATE(data.getStoryId(), DateUtil.createDateTimestamp(date));

                                chapterDAO.updateStatus(data.getId(), 1);
                            }

                        } catch (Exception ex) {
                            logger.error("ERROR[" + data.getLink() + "]", ex);
                            chapterDAO.updateStatus(data.getId(), -1);
                        }
                    }
                }
                System.out.println("CHAPTER_SIZE [" + contentList.size() + "]");

                Thread.sleep(1 * 30 * 1000);
            }
        } catch (Exception ex) {
            logger.error("ERROR[crawlerContentWithChapterGoogle]", ex);
        }
    }

    @Override
    public void run() {
        logger.info("START THREAD [ThreadChapterContent]");

        crawlerContentWithChapterGoogle();
    }
}
