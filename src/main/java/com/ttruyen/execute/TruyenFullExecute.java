package com.ttruyen.execute;

import com.ttruyen.db.ChapterDAO;
import com.ttruyen.db.StoryDAO;
import com.ttruyen.model.Content;
import com.ttruyen.model.Detail;
import com.ttruyen.parse.ParseTruyenFull;
import com.ttruyen.utils.FTPUtil;
import com.ttruyen.utils.FileUtil;
import com.ttruyen.utils.GZipUtil;
import com.ttruyen.utils.StringUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class TruyenFullExecute {
    private static final Logger logger = LoggerFactory.getLogger(TruyenFullExecute.class);

    private ChapterDAO chapterDAO = new ChapterDAO();
    private StoryDAO storyDAO = new StoryDAO();
    private ParseTruyenFull parseTruyenFull = new ParseTruyenFull();

    public void updateImage() {

        try {
            FTPUtil ftpUploader = new FTPUtil("ttruyen.com", "ttruyen.com", "@");
            List<Detail> detailList = storyDAO.selectStatusLike();

            for(Detail data : detailList) {

                System.out.println(data.getImage());

                if (data.getImage().contains("truyenfull.vn")) {
                    URL urlFile = null;

                    if (data.getImage().contains("https://")) {
                        urlFile = new URL(data.getImage().replaceAll("https://cdnaz", "https://cdnvn"));
                    } else {
                        urlFile = new URL("https:" + data.getImage());
                    }

                    InputStream inputStream = urlFile.openStream();

                    if (inputStream != null) {
                        String image =  data.getMetaUrl() + "-" + data.getId() + ".jpg";

                        ftpUploader.uploadFile(inputStream, image, "/httpdocs/images/");

                        if (ftpUploader.getFtpClient().getReplyCode() == 226) {
                            storyDAO.updateImage(data.getId(), image);
                        }

                        Thread.sleep(1000);
                    }
                }
            }
            ftpUploader.disconnect();
        } catch (Exception ex) {
            logger.error("ERROR[updateCREATED_DATEWidthStory]", ex);
        }
    }

    public void updateFileEmpty() {

        List<File> listFile = FileUtil.listFileEmpy("/Users/khanhlv/ttruyen/data_empty/");

        for (File file : listFile) {
            try {
                String fileName = file.getName();
                fileName = fileName.substring(0, fileName.indexOf("."));
                Content contentDb = chapterDAO.selectOne(Integer.parseInt(fileName));
                System.out.println(contentDb.getLink());
                Content content = parseTruyenFull.readContent(contentDb.getLink());
                GZipUtil.compressGZIP(content.getContent(), new File("/Users/khanhlv/ttruyen/data_empty/"+ file.getName()));

                System.out.println(file.getName());
            } catch (Exception ex) {
                try {
                    FileUtils.writeStringToFile(new File("/Users/khanhlv/ttruyen/data_error/"+ file.getName()), "eror");
                } catch (IOException e) {
                    logger.error("ERROR[writeStringToFile]", ex);
                }
                logger.error("ERROR[updateFileEmpty]", ex);
            }
        }
    }

    public void updateMeta() throws SQLException {
        List<Content> detailList = chapterDAO.selectTopStatus(Integer.MAX_VALUE);

        for(Content data : detailList) {
            System.out.println("UPDATE: " + data.getId());
            chapterDAO.updateMetaUrl(data.getId(), StringUtil.stripAccents(data.getChapterIndex().trim(), "-"));
        }
    }

    public static void main(String[] args) throws SQLException {
        TruyenFullExecute truyenFullExecute = new TruyenFullExecute();

//        truyenFullExecute.updateMeta();
//        if (args.length > 0) {
//            String pathFolder = args[0];
//
//            new Thread(new ThreadCategory()).run();
////            new Thread(new ThreadStoryStatus(pathFolder)).run();
//            new Thread(new ThreadChapterContent()).run();
//        }
//        else {
//            System.out.println("Param - Path: C:/");
//        }

//        new Thread(new ThreadCategory()).start();
        new Thread(new ThreadStoryStatus("C://ttruyen")).start();
        new Thread(new ThreadChapterContent()).start();
//        truyenFullExecute.updateImage();
    }
}
