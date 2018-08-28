package servlet.api.impl.dao;

import api.dao.AbstractDAO;
import api.dao.DAOUserPicture;
import api.data.UserPicture;
import api.exceptions.DAOException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import servlet.api.impl.data.UserPictureImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 *  Created by Leonardo Lana
 *  Github: https://github.com/leonardodlana
 *
 *  Copyright 2018 Leonardo Lana
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

public class DAOUserPictureImpl extends AbstractDAO<UserPicture> implements DAOUserPicture {

    @Override
    protected String[] getCreateTableSql() {
        return new String[]{
                "CREATE TABLE UserPicture ("
                        + "id bigint NOT NULL AUTO_INCREMENT,"
                        + "user_id int NOT NULL,"
                        + "filename varchar(255) NOT NULL,"
                        + "date bigint,"
                        + "title varchar(255), "
                        + "description text, "
                        + "latitude double, "
                        + "longitude double, "
                        + "likes int, "
                        + "reports int, "
                        + "PRIMARY KEY (id),"
                        + "FOREIGN KEY (user_id) REFERENCES User(id)"
                        + ")",

                "CREATE TABLE UserPictureLike ("
                        + "picture_id bigint NOT NULL,"
                        + "user_id int NOT NULL,"
                        + "FOREIGN KEY (picture_id) REFERENCES UserPicture(id),"
                        + "FOREIGN KEY (user_id) REFERENCES User(id),"
                        + "CONSTRAINT PK_UserPictureLike PRIMARY KEY (picture_id, user_id)"
                        + ")",

                "CREATE TABLE UserPictureReport ("
                        + "picture_id bigint NOT NULL,"
                        + "user_id int NOT NULL,"
                        + "FOREIGN KEY (picture_id) REFERENCES UserPicture(id),"
                        + "FOREIGN KEY (user_id) REFERENCES User(id),"
                        + "CONSTRAINT PK_UserPictureReport PRIMARY KEY (picture_id, user_id)"
                        + ")"
        };
    }


    @Override
    public void save(UserPicture picture) {
        JdbcTemplate update = newJdbcTemplate();
        if (tryUpdateUser(picture, update))
            return;
        insertUser(picture);
    }

    private boolean tryUpdateUser(UserPicture picture, JdbcTemplate update) {
        try {
            String sql = "UPDATE UserPicture SET title=?, description=?, likes=?, reports=? WHERE id=?";
            return update.update(sql, picture.getTitle(), picture.getDescription(), picture.getLikesCount(), picture.getReportsCount(), picture.getId()) != 0;
        } catch (DataAccessException e) {
            return false;
        } catch (Throwable e1) {
            throw new DAOException(e1);
        }
    }

    private void insertUser(UserPicture picture) {
        try {
            String sql = "INSERT INTO UserPicture (user_id, filename, date, title, description, latitude, longitude, likes, reports) VALUES (?,?,?,?,?,?,?,?,?)";
            JdbcTemplate insert = newJdbcTemplate();
            insert.update(sql, picture.getUserId(), picture.getFilename(), picture.getDate(), picture.getTitle(), picture.getDescription()
                    , picture.getLatitude(), picture.getLongitude(), picture.getLikesCount(), picture.getReportsCount());

            int id = insert.queryForObject("SELECT id FROM UserPicture WHERE user_id=? AND filename=?",
                    new Object[]{picture.getUserId(), picture.getFilename()}, Integer.class);

            picture.setId(id);
        } catch (Throwable e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<UserPicture> selectAll() {
        return new ArrayList<>();
    }

    @Override
    public List<UserPicture> selectAllForGeoLocation() {
        try {
            return newJdbcTemplate().query("SELECT id,latitude,longitude FROM UserPicture",
                    new UserPictureForGeoLocationRowMapper());
        } catch (DataAccessException e) {
            return new ArrayList<>();
        } catch (Throwable e) {
            throw new DAOException(e);
        }
    }

    @Override
    public UserPicture selectById(int id) {
        try {
            return newJdbcTemplate().queryForObject("SELECT * FROM UserPicture WHERE id=?", new Object[]{id},
                    new UserPictureRowMapper());
        } catch (DataAccessException e) {
            return null;
        } catch (Throwable e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<UserPicture> selectByIds(Set<Long> picturesIds) {
        try {
            JdbcTemplate db = newJdbcTemplate();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM UserPicture WHERE id IN (");

            for (int i = 0; i < picturesIds.size(); i++)
                sql.append("?,");
            sql.setLength(sql.length() - 1);
            sql.append(")");

            return db.query(sql.toString(), new UserPictureRowMapper(), picturesIds.toArray());
        } catch (DataAccessException e) {
            return new ArrayList<>();
        } catch (Throwable e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int deleteById(int userId, long pictureId) {
        try {
            JdbcTemplate update = newJdbcTemplate();
            String sql = "DELETE FROM UserPictureLike WHERE picture_id=?";
            update.update(sql, pictureId);

            sql = "DELETE FROM UserPicture WHERE user_id=? AND id=?";
            return update.update(sql, userId, pictureId);
        } catch (DataAccessException e) {
            return 0;
        } catch (Throwable e1) {
            throw new DAOException(e1);
        }
    }

    @Override
    public void addLike(int userId, long pictureId) {
        try {
            JdbcTemplate insert = newJdbcTemplate();
            String sql = "INSERT INTO UserPictureLike (picture_id, user_id) VALUES (?,?)";
            int rowsAffected = insert.update(sql, pictureId, userId);
            if (rowsAffected > 0) {
                insert.update("UPDATE UserPicture SET likes = likes + 1 WHERE id=?", pictureId);
            }
        } catch (DataAccessException ignored) {
            ignored.printStackTrace();
        } catch (Throwable e1) {
            throw new DAOException(e1);
        }
    }

    @Override
    public void removeLike(int userId, long pictureId) {
        try {
            JdbcTemplate update = newJdbcTemplate();
            String sql = "DELETE FROM UserPictureLike WHERE user_id=? AND picture_id=?";
            int rowsAffected = update.update(sql, userId, pictureId);
            if (rowsAffected > 0) {
                update.update("UPDATE UserPicture SET likes = likes - 1 WHERE id=?", pictureId);
            }
        } catch (DataAccessException ignored) {
        } catch (Throwable e1) {
            throw new DAOException(e1);
        }
    }

    @Override
    public Set<Long> selectLikesByUser(int userId) {
        try {
            JdbcTemplate db = newJdbcTemplate();

            String sql = "SELECT picture_id FROM UserPictureLike WHERE user_id=?";

            return new HashSet<>(db.queryForList(sql, new Object[] {userId}, Long.class));
        } catch (DataAccessException e) {
            return new HashSet<>();
        } catch (Throwable e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void addReport(int userId, long pictureId) {
        try {
            JdbcTemplate insert = newJdbcTemplate();
            String sql = "INSERT INTO UserPictureReport (picture_id, user_id) VALUES (?,?)";
            int rowsAffected = insert.update(sql, pictureId, userId);
            if (rowsAffected > 0) {
                insert.update("UPDATE UserPicture SET reports = reports + 1 WHERE id=?", pictureId);
            }
        } catch (DataAccessException ignored) {
            ignored.printStackTrace();
        } catch (Throwable e1) {
            throw new DAOException(e1);
        }
    }

    class UserPictureRowMapper implements RowMapper<UserPicture> {

        @Override
        public UserPicture mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new UserPictureImpl(rs.getLong("id"), rs.getInt("user_id"), rs.getString("filename"),
                    rs.getLong("date"), rs.getString("title"), rs.getString("description"), rs.getDouble("latitude"),
                    rs.getDouble("longitude"), rs.getInt("likes"), rs.getInt("reports"));
        }

    }

    class UserPictureForGeoLocationRowMapper implements RowMapper<UserPicture> {

        @Override
        public UserPicture mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new UserPictureImpl(rs.getInt("id"), rs.getDouble("latitude"),
                    rs.getDouble("longitude"));
        }

    }
}
