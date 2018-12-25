package com.ttruyen.execute;

import com.ttruyen.core.Const;
import com.ttruyen.db.AuthorDAO;
import com.ttruyen.db.CategoryDAO;
import com.ttruyen.db.ChapterDAO;
import com.ttruyen.db.StoryDAO;
import com.ttruyen.model.Chapter;
import com.ttruyen.model.Detail;
import com.ttruyen.parse.ParseTruyenFull;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class ThreadStoryStatus implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ThreadStoryStatus.class);

    private ChapterDAO chapterDAO = new ChapterDAO();
    private StoryDAO storyDAO = new StoryDAO();
    private ParseTruyenFull parseTruyenFull = new ParseTruyenFull();

    private String pathFolder;

    public ThreadStoryStatus(String pathFolder) {
        this.pathFolder = pathFolder;
    }

    public void crawlerChapterWithStoryNotFull(String path) {
        try {
            while (true) {
                List<Detail> detailList = storyDAO.selectStoryStatusNotFull();

                for(Detail data : detailList) {

                    try {
                        String pathFile = path +"/data_complete/" + data.getId() + ".txt.gz";
                        File file = new File(pathFile);

                        if(file.exists()) {
                            logger.info("EXISTS_GET [URL=" + data.getLink() + "]");
                            continue;
                        }

                        logger.info("GET_START [URL=" + data.getLink() + "]");

                        long start = System.currentTimeMillis();
                        Detail detail = parseTruyenFull.readDetail(data.getLink());
                        detail.setId(data.getId());

                        List<Chapter> chapterList = parseTruyenFull.readChapter(data.getLink(), detail.getPageTotal());

                        long end = System.currentTimeMillis() - start;

                        logger.info("GET_END [URL=" + data.getLink() + "][TIME=" + end  + "][SIZE=" + chapterList.size() + "]");

                        storyDAO.updateStoryStatus(detail);

                        chapterDAO.insert(detail.getId(), detail, chapterList);

                        FileUtils.writeStringToFile(new File(pathFile), "DONE");

                    } catch (Exception ex) {
                        logger.error("ERROR[" + data.getLink() + "]", ex);
                        String pathFile = path +"/data_error/" + data.getId() + ".txt.gz";
                        FileUtils.writeStringToFile(new File(pathFile), "ERROR");
                    }
                }

                if (detailList == null || detailList.size() == 0) {
                    // 1h lay du lieu 1 lan
                    Thread.sleep(1 * 60 * 60 * 1000);
                }

                Thread.sleep(Const.THREAD);
            }
        } catch (Exception ex) {
            logger.error("ERROR[crawlerChapterWithStory]", ex);
        }
    }
    @Override
    public void run() {
        logger.info("START THREAD [ThreadStoryStatus]");

        crawlerChapterWithStoryNotFull(pathFolder);
    }
}
