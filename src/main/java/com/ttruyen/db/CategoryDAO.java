package com.ttruyen.db;

import com.ttruyen.core.ConnectionPool;
import com.ttruyen.model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    private static final Logger logger = LoggerFactory.getLogger(CategoryDAO.class);

    public List<Category> selectAll() throws SQLException {
        List<Category> listLink = new ArrayList<>();
        String sqlStory = "SELECT * FROM CATEGORY";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory);
             ResultSet rs = pStmt.executeQuery()) {

            while(rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("CATEGORY_ID"));
                category.setLink(rs.getString("SOURCE_LINK"));
                listLink.add(category);
            }
            return listLink;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void updateStatus(int categoryId, int status) throws SQLException {
        String sqlStory = "UPDATE CATEGORY SET STATUS = ? WHERE CATEGORY_ID = ?";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setInt(1, status);
            pStmt.setInt(2, categoryId);

            pStmt.executeUpdate();
        } catch (Exception ex) {
            logger.error("updateStatus", ex);
            throw ex;
        }
    }

    public void insertStoryCategory(int storyId, List<String> categoryList) throws  SQLException {
        String sqlStory = "INSERT INTO STORY_CATEGORY(CATEGORY_ID,STORY_ID) VALUES(?,?)";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {
            for(String data : categoryList) {
                if (!checkExistsCategory(Integer.parseInt(data), storyId)) {

                    pStmt.setInt(1, Integer.parseInt(data));
                    pStmt.setInt(2, storyId);

                    int affectedRows = pStmt.executeUpdate();

                    if (affectedRows == 0) {
                        throw new SQLException("Creating user failed, no rows affected.");
                    }
                }
            }
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public boolean checkExistsCategory(int categoryId, int storyId) throws SQLException {
        String sqlStory = "SELECT * FROM STORY_CATEGORY WHERE CATEGORY_ID = ? AND STORY_ID = ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setInt(1, categoryId);
            pStmt.setInt(2, storyId);

            ResultSet rs = pStmt.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return false;
    }
}
