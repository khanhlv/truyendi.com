package com.ttruyen.execute;

import com.ttruyen.core.Const;
import com.ttruyen.db.AuthorDAO;
import com.ttruyen.db.CategoryDAO;
import com.ttruyen.db.ChapterDAO;
import com.ttruyen.db.StoryDAO;
import com.ttruyen.model.Category;
import com.ttruyen.model.Chapter;
import com.ttruyen.model.Detail;
import com.ttruyen.parse.ParseTruyenFull;
import com.ttruyen.utils.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class ThreadCategory implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TruyenFullExecute.class);

    private CategoryDAO categoryDAO = new CategoryDAO();
    private AuthorDAO authorDAO = new AuthorDAO();
    private ChapterDAO chapterDAO = new ChapterDAO();
    private StoryDAO storyDAO = new StoryDAO();
    private ParseTruyenFull parseTruyenFull = new ParseTruyenFull();

    public void crawlerStoryWithCategory(int limit) {
        try {
            for (Category data: categoryDAO.selectAll()) {
                try {
                    logger.info("GET_START [URL=" + data.getLink() + "]");

                    long start = System.currentTimeMillis();
                    List<Category> listStory = parseTruyenFull.readCategory(data.getLink(), 1, limit);
                    long end = System.currentTimeMillis() - start;
                    logger.info("GET_END [URL=" + data.getLink() + "][TIME=" + end  + "][SIZE=" + listStory.size() + "]");

                    storyDAO.insertStory(listStory, new Date());
                    categoryDAO.updateStatus(data.getId(), 1);
                } catch (Exception ex) {
                    logger.error("ERROR[" + data.getLink() + "]", ex);
                    categoryDAO.updateStatus(data.getId(), -1);
                }
            }
        } catch (Exception ex) {
            logger.error("ERROR[crawlerStoryWithCategory]", ex);
        }
    }

    public void crawlerChapterWithStory() {
        try {
            FTPUtil ftpUploader = null;

            while (true) {
                List<Detail> detailList = storyDAO.select(Const.TOP_STORY);

                if (detailList != null && detailList.size() > 0) {
                    ftpUploader = new FTPUtil("ttruyen.com", "ttruyen.com", "@");
                }

                if (ftpUploader != null && ftpUploader.getFtpClient().isConnected()) {
                    for(Detail data : detailList) {
                        try {
                            logger.info("GET_START [URL=" + data.getLink() + "]");

                            long start = System.currentTimeMillis();
                            Detail detail = parseTruyenFull.readDetail(data.getLink());
                            detail.setId(data.getId());

                            List<Chapter> chapterList = parseTruyenFull.readChapter(data.getLink(), detail.getPageTotal());

                            long end = System.currentTimeMillis() - start;

                            logger.info("GET_END [URL=" + data.getLink() + "][TIME=" + end  + "][SIZE=" + chapterList.size() + "]");

                            if (detail.getImage().contains("truyenfull.vn")) {

                                try {
                                    URL urlFile;
                                    if (detail.getImage().contains("https://")) {
                                        urlFile = new URL(detail.getImage().replaceAll("https://cdnaz", "https://cdnvn"));
                                    } else {
                                        urlFile = new URL("https:" + detail.getImage());
                                    }

                                    InputStream inputStream = urlFile.openStream();

                                    String image =  data.getMetaUrl() + "-" + detail.getId() + ".jpg";

                                    ftpUploader.uploadFile(inputStream, image, "/httpdocs/images/");

                                    if (ftpUploader.getFtpClient().getReplyCode() == 226) {
                                        detail.setImage(image);
                                    }
                                } catch (Exception ex) {
                                    logger.error("ERROR_UPLOAD_IMAGE[" + detail.getImage() + "]", ex);
                                }
                            }

                            storyDAO.updateStory(detail);

                            categoryDAO.insertStoryCategory(detail.getId(), detail.getListCategory());

                            authorDAO.insert(detail.getId(), detail.getListAuthor());

                            chapterDAO.insert(detail.getId(), detail, chapterList);

                            storyDAO.updateStatus(data.getId(), 1);
                        } catch (Exception ex) {
                            logger.error("ERROR[" + data.getLink() + "]", ex);
                            storyDAO.updateStatus(data.getId(), -1);
                        }
                    }

                    Thread.sleep(Const.THREAD);

                    ftpUploader.disconnect();
                }

                if (detailList == null || detailList.size() == 0) {
                    // 1h lay du lieu 1 lan
                    Thread.sleep(1 * 60 * 60 * 1000);
                }
            }
        } catch (Exception ex) {
            logger.error("ERROR[crawlerChapterWithStory]", ex);
        }
    }

    @Override
    public void run() {
        logger.info("START THREAD [ThreadCategory]");
        crawlerStoryWithCategory(4);

        crawlerChapterWithStory();
    }
}
