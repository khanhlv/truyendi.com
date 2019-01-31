package com.ttruyen.execute;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ttruyen.core.Const;
import com.ttruyen.core.ShareQueue;
import com.ttruyen.db.ChapterDAO;
import com.ttruyen.model.Content;

public class ThreadShareQueue implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ThreadShareQueue.class);
    private ChapterDAO chapterDAO = new ChapterDAO();

    public ThreadShareQueue(){
        System.out.println("START_THREAD_QUEUE");
    }

    @Override
    public void run() {
        try {
            while (true) {

                if (ShareQueue.shareQueue.size() < Const.TOP_CHAPTER) {
                    List<Content> contentList = chapterDAO.selectTop(Const.TOP_CHAPTER);

                    if (contentList != null && contentList.size() > 0) {
                        ShareQueue.addItem(contentList);
                    }
                }

                System.out.println("SHARE_QUEUE=" + ShareQueue.shareQueue.size());

                Thread.sleep(1 * 60 * 1000);
            }
        } catch (Exception ex) {
            logger.error("ERROR[ThreadShareQueue]", ex);
        }
    }
}
