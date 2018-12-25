package com.ttruyen.db;

import com.ttruyen.core.ConnectionPool;
import com.ttruyen.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

public class AuthorDAO {

    private static final Logger logger = LoggerFactory.getLogger(AuthorDAO.class);

    public boolean checkExistsAuthor(int authorId, int storyId) throws SQLException {
        String sqlStory = "SELECT * FROM STORY_AUTHOR WHERE AUTHOR_ID = ? AND STORY_ID = ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {
            pStmt.setInt(1, authorId);
            pStmt.setInt(2, storyId);

            if (pStmt.executeQuery().next()) {
                return true;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return false;
    }

    public void insertStoryAuthor(int authorId, int storyId) throws SQLException {
        String sqlStory = "INSERT INTO STORY_AUTHOR(AUTHOR_ID,STORY_ID) VALUES(?,?)";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setInt(1, authorId);
            pStmt.setInt(2, storyId);

            int affectedRows = pStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void insert(int storyId, List<String> authorList) throws SQLException {

        String sqlStory = "INSERT INTO AUTHOR(AUTHOR_NAME,META_URL,META_TITLE,META_DESCRIPTION,META_KEYWORD) " +
                "VALUES(?,?,?,?,?)";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory, Statement.RETURN_GENERATED_KEYS)) {
            for(String data : authorList) {
                    int id = checkExists(data);
                    if (id != -1) {
                        if (!checkExistsAuthor(id, storyId)) {
                            insertStoryAuthor(id, storyId);
                        }
//                        logger.info("EXISTS_AUTHOR[" + data + "][" + id + "]");
                    } else {

                        pStmt.setString(1, data);
                        pStmt.setString(2, StringUtil.stripAccents(data, "-"));
                        pStmt.setString(3, data);
                        pStmt.setString(4, data);
                        pStmt.setString(5, data);

                        pStmt.executeUpdate();

                        ResultSet genKeysRs = pStmt.getGeneratedKeys();
                        if (genKeysRs.next()) {
                            int authorId = genKeysRs.getInt(1);
                            insertStoryAuthor(authorId, storyId);
                        }

                        if (genKeysRs != null) {
                            genKeysRs.close();
                        }

                        logger.info("INSERT_AUTHOR[" + data + "]");
                    }
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public int checkExists(String authorName) throws SQLException {
        String sqlStory = "SELECT * FROM AUTHOR WHERE AUTHOR_NAME = ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setString(1, authorName);

            ResultSet rs = pStmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("AUTHOR_ID");
            }
        } catch (Exception ex) {
            throw ex;
        }

        return -1;
    }
}
