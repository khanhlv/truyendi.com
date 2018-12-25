package com.ttruyen.db;

import com.ttruyen.core.ConnectionPool;
import com.ttruyen.model.Chapter;
import com.ttruyen.model.Content;
import com.ttruyen.model.Detail;
import com.ttruyen.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChapterDAO {

    private static final Logger logger = LoggerFactory.getLogger(ChapterDAO.class);

    public void insert(int storyId, Detail detail, List<Chapter> chapterList) throws SQLException {

        String sqlStory = "INSERT INTO CHAPTER(STORY_ID,CHAPTER_INDEX,CHAPTER_NAME," +
                "SOURCE_LINK,META_URL,META_TITLE,META_DESCRIPTION,META_KEYWORD,META_IMAGE) " +
                "VALUES(?,?,?,?,?,?,?,?,?)";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {
            for(Chapter data : chapterList) {
                if (checkExists(storyId, data.getLink())) {
                    System.out.println("EXISTS_CHAPTER[" + data.getName() + "][" + data.getLink() + "]");
                } else {
                    String[] name = data.getName().split(":");
                    pStmt.setInt(1, storyId);
                    pStmt.setString(2, name[0].trim());
                    pStmt.setString(3, name.length > 1 ? name[1] : "");
                    pStmt.setString(4, data.getLink());
                    pStmt.setString(5, StringUtil.stripAccents(name[0].trim(), "-"));
                    pStmt.setString(6, data.getName());
                    pStmt.setString(7, data.getName());
                    pStmt.setString(8, data.getName());
                    pStmt.setString(9, detail.getImage());
                    pStmt.executeUpdate();

                    logger.info("INSERT_CHAPTER[" + data.getName() + "][" + data.getLink() + "]");
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public boolean checkExists(int storyId, String sourceLink) throws SQLException {
        String sqlStory = "SELECT * FROM CHAPTER WHERE STORY_ID = ? AND SOURCE_LINK = ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setInt(1, storyId);
            pStmt.setString(2, sourceLink);

            if (pStmt.executeQuery().next()) {
                return true;
            }
        } catch (Exception ex) {
            throw ex;
        }

        return false;
    }

    public void updateChapterContent(Content content) throws SQLException {
        String sqlStory = "UPDATE CHAPTER SET CHAPTER_CONTENT = ? WHERE CHAPTER_ID = ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setString(1, content.getContent());
            pStmt.setInt(2, content.getId());

            pStmt.executeUpdate();

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void updateStatus(int chapterId, int status) throws SQLException {
        String sqlStory = "UPDATE CHAPTER SET STATUS = ? WHERE CHAPTER_ID = ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setInt(1, status);
            pStmt.setInt(2, chapterId);
            pStmt.executeUpdate();

        } catch (Exception ex) {
            logger.error("updateStatus", ex);
            throw ex;
        }
    }

    public void updateMetaUrl(int chapterId, String metaUrl) throws SQLException {
        String sqlStory = "UPDATE CHAPTER SET META_URL = ? WHERE CHAPTER_ID = ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setString(1, metaUrl);
            pStmt.setInt(2, chapterId);
            pStmt.executeUpdate();

        } catch (Exception ex) {
            logger.error("updateStatus", ex);
            throw ex;
        }
    }

    public List<Content> selectTop(int top) throws SQLException {
        List<Content> listLink = new ArrayList<>();
        String sqlStory = "SELECT TOP " + top +" * FROM CHAPTER WHERE STATUS IN (0, -1)";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            ResultSet rs = pStmt.executeQuery();
            while(rs.next()) {
                Content content = new Content();
                content.setId(rs.getInt("CHAPTER_ID"));
                content.setLink(rs.getString("SOURCE_LINK"));
                content.setStoryId(rs.getInt("STORY_ID"));
                listLink.add(content);
            }
            return listLink;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Content selectOne(int storyId) throws SQLException {

        String sqlStory = "SELECT * FROM CHAPTER WHERE CHAPTER_ID = ?";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setInt(1, storyId);

            ResultSet rs = pStmt.executeQuery();

            if(rs.next()) {
                Content content = new Content();
                content.setId(rs.getInt("CHAPTER_ID"));
                content.setLink(rs.getString("SOURCE_LINK"));
                content.setStoryId(rs.getInt("STORY_ID"));
                return content;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

    public List<Content> selectTopStatus(int top) throws SQLException {
        List<Content> listLink = new ArrayList<>();
        String sqlStory = "SELECT TOP " + top +" * FROM CHAPTER WHERE STATUS = 1";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            ResultSet rs = pStmt.executeQuery();
            while(rs.next()) {
                Content content = new Content();
                content.setId(rs.getInt("CHAPTER_ID"));
                content.setLink(rs.getString("SOURCE_LINK"));
                content.setContent(rs.getString("CHAPTER_CONTENT"));
                content.setChapterIndex(rs.getString("CHAPTER_INDEX"));
                listLink.add(content);
            }
            return listLink;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
