package com.ttruyen.db;

import com.ttruyen.core.ConnectionPool;
import com.ttruyen.model.Category;
import com.ttruyen.model.Detail;
import com.ttruyen.utils.DateUtil;
import com.ttruyen.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StoryDAO {

    private static final Logger logger = LoggerFactory.getLogger(StoryDAO.class);

    public void insertStory(List<Category> listStory, Date date) throws SQLException {

        String sqlStory = "INSERT INTO STORY(STORY_NAME,SOURCE_LINK,META_URL,META_TITLE,CREATED_DATE) VALUES(?,?,?,?,?)";

        Date d = date;

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {
            for(Category category: listStory) {
				if (!checkExists(category.getLink())) {
					d = DateUtils.addMinutes(d, -1);
					pStmt.setString(1, category.getName());
					pStmt.setString(2, category.getLink());
					pStmt.setString(3, StringUtil.stripAccents(category.getName(), "-"));
					pStmt.setString(4, category.getName());
					pStmt.setTimestamp(5, DateUtil.createDateTimestamp(d));

					pStmt.executeUpdate();

					logger.info("INSERT_STORY[" + category.getName() + "][" + category.getLink() + "]");
				}
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public boolean checkExists(String sourceLink) throws SQLException {
        String sqlStory = "SELECT * FROM STORY WHERE SOURCE_LINK = ?";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setString(1, sourceLink);
            ResultSet rs = pStmt.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (Exception ex) {
            throw ex;
        }

        return false;
    }

    public void updateStatus(int storyId, int status) throws SQLException {
        String sqlStory = "UPDATE STORY SET STATUS = ? WHERE STORY_ID = ?";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setInt(1, status);
            pStmt.setInt(2, storyId);
            pStmt.executeUpdate();

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void updateImage(int storyId, String images) throws SQLException {
        String sqlStory = "UPDATE STORY SET STORY_IMAGE = ?, META_IMAGE = ? WHERE STORY_ID = ?";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setString(1, images);
            pStmt.setString(2, images);
            pStmt.setInt(3, storyId);
            pStmt.executeUpdate();

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void updateStory(Detail detail) throws SQLException {
        String sqlStory = "UPDATE STORY SET STORY_DESCRIPTION = ?, STORY_STATUS = ?, STORY_SOURCE = ?, STORY_IMAGE = ?, META_DESCRIPTION = ?, META_KEYWORD = ?, META_IMAGE = ? WHERE STORY_ID = ?";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setString(1, detail.getDescription());
            pStmt.setString(2, detail.getStatus());
            pStmt.setString(3, detail.getSource());
            pStmt.setString(4, detail.getImage());
            pStmt.setString(5, StringUtils.substring(detail.getDescription(), 0, 500));
            pStmt.setString(6, detail.getMetaKeyword());
            pStmt.setString(7, detail.getImage());
            pStmt.setInt(8, detail.getId());

            pStmt.executeUpdate();

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void updateStoryStatus(Detail detail) throws SQLException {
        String sqlStory = "UPDATE STORY SET STORY_STATUS = ? WHERE STORY_ID = ?";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setString(1, detail.getStatus());
            pStmt.setInt(2, detail.getId());

            pStmt.executeUpdate();

        } catch (Exception ex) {
            throw ex;
        }
    }


    public List<Detail> select(int top) throws SQLException {
        List<Detail> listLink = new ArrayList<>();
        String sqlStory = "SELECT TOP " + top +" * FROM STORY WHERE STATUS IN (0, -1)";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            ResultSet rs = pStmt.executeQuery();

            while(rs.next()) {
                Detail detail = new Detail();
                detail.setId(rs.getInt("STORY_ID"));
                detail.setLink(rs.getString("SOURCE_LINK"));
                detail.setMetaUrl(rs.getString("META_URL"));
                listLink.add(detail);
            }
            return listLink;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<Detail> selectStatus(int top, int status) throws SQLException {
        List<Detail> listLink = new ArrayList<>();
        String sqlStory = "SELECT TOP " + top +" * FROM STORY WHERE STATUS = ?";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setInt(1, status);

            ResultSet rs = pStmt.executeQuery();

            while(rs.next()) {
                Detail detail = new Detail();
                detail.setId(rs.getInt("STORY_ID"));
                detail.setLink(rs.getString("SOURCE_LINK"));
                detail.setImage(rs.getString("STORY_IMAGE"));
                detail.setMetaUrl(rs.getString("META_URL"));
                listLink.add(detail);
            }
            return listLink;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<Detail> selectStatusLike() throws SQLException {
        List<Detail> listLink = new ArrayList<>();
        String sqlStory = "SELECT * FROM STORY WHERE STORY_IMAGE LIKE ?";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setString(1, "%truyenfull.vn%");
            ResultSet rs = pStmt.executeQuery();

            while(rs.next()) {
                Detail detail = new Detail();
                detail.setId(rs.getInt("STORY_ID"));
                detail.setLink(rs.getString("SOURCE_LINK"));
                detail.setImage(rs.getString("STORY_IMAGE"));
                detail.setMetaUrl(rs.getString("META_URL"));
                listLink.add(detail);
            }
            return listLink;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<Detail> selectStoryStatusNotFull() throws SQLException {
        List<Detail> listLink = new ArrayList<>();
        String sqlStory = "SELECT * FROM STORY WHERE STORY_STATUS = 0 AND STATUS = 1 ORDER BY CREATED_DATE DESC";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            ResultSet rs = pStmt.executeQuery();

            while(rs.next()) {
                Detail detail = new Detail();
                detail.setId(rs.getInt("STORY_ID"));
                detail.setLink(rs.getString("SOURCE_LINK"));
                listLink.add(detail);
            }
            return listLink;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void updatCREATED_DATE(int storyId, Timestamp timestamp) throws SQLException {
        String sqlStory = "UPDATE STORY SET CREATED_DATE = ? WHERE STORY_ID = ?";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setTimestamp(1, timestamp);
            pStmt.setInt(2, storyId);
            pStmt.executeUpdate();

        } catch (Exception ex) {
            logger.error("updatCREATED_DATE", ex);
            throw ex;
        }
    }

    public void updateDate() throws Exception {
        String sqlStory = "SELECT * FROM STORY ORDER BY STORY_ID ASC";

        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory);
             ResultSet rs = pStmt.executeQuery()) {

            java.util.Date d = new Date();

            while(rs.next()) {
                int storyId = rs.getInt("STORY_ID");

                d = DateUtils.addMinutes(d, -1);

                updatCREATED_DATE(storyId, DateUtil.createDateTimestamp(d));

                System.out.println(storyId);

            }
        } catch (Exception ex) {
            throw ex;
        }
    }

}
