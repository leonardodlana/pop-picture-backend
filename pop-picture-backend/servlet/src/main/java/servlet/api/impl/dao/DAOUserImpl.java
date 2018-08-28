package servlet.api.impl.dao;

import api.dao.AbstractDAO;
import api.dao.DAOUser;
import api.data.User;
import api.exceptions.DAOException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import servlet.api.impl.data.UserImpl;

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

public class DAOUserImpl extends AbstractDAO<User> implements DAOUser {

    public DAOUserImpl() {
        super();
    }

    @Override
    protected String[] getCreateTableSql() {
        return new String[]{
                "CREATE TABLE User ("
                        + "id int NOT NULL AUTO_INCREMENT,"
                        + "firebase_id varchar(100) NOT NULL,"
                        + "public_id varchar(64) NOT NULL,"
                        + "email varchar(255), "
                        + "name varchar(255), "
                        + "picture_name varchar(255), "
                        + "PRIMARY KEY (id),"
                        + "UNIQUE (firebase_id)" + ")"};
    }

    @Override
    public void save(User person) {
        JdbcTemplate update = newJdbcTemplate();
        if (tryUpdateUser(person, update))
            return;
        insertUser(person);
    }

    private boolean tryUpdateUser(User user, JdbcTemplate update) {
        try {
            String sql = "UPDATE User SET email=?, name=? WHERE firebase_id=?";
            return update.update(sql, user.getEmail(), user.getName(), user.getFirebaseId()) != 0;
        } catch (DataAccessException e) {
            return false;
        } catch (Throwable e1) {
            throw new DAOException(e1);
        }
    }

    private void insertUser(User user) {
        try {
            String sql = "INSERT INTO User (firebase_id,public_id,email,name,picture_name) VALUES (?,?,?,?,?)";
            JdbcTemplate insert = newJdbcTemplate();
            insert.update(sql, user.getFirebaseId(), user.getPublicId(), user.getEmail(), user.getName(), user.getPictureName());
        } catch (Throwable e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<User> selectAll() {
        return new ArrayList<>();
    }

    @Override
    public User selectById(int id) {
        try {
            return newJdbcTemplate().queryForObject("SELECT * FROM User WHERE id=?", new Object[]{id},
                    new UserRowMapper());
        } catch (DataAccessException e) {
            return null;
        } catch (Throwable e) {
            throw new DAOException(e);
        }
    }

    @Override
    public User selectByFirebaseId(String firebaseId) {
        try {
            return newJdbcTemplate().queryForObject("SELECT * FROM User WHERE firebase_id=?", new Object[]{firebaseId},
                    new UserRowMapper());
        } catch (DataAccessException e) {
            return null;
        } catch (Throwable e) {
            throw new DAOException(e);
        }
    }

    @Override
    public User selectByPublicId(String publicId) {
        try {
            return newJdbcTemplate().queryForObject("SELECT * FROM User WHERE public_id=?", new Object[]{publicId},
                    new UserRowMapper());
        } catch (DataAccessException e) {
            return null;
        } catch (Throwable e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<User> selectByIds(Set<Integer> userIds) {
        try {
            JdbcTemplate db = newJdbcTemplate();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM User WHERE id IN (");

            for (int i = 0; i < userIds.size(); i++)
                sql.append("?,");
            sql.setLength(sql.length() - 1);
            sql.append(")");

            return db.query(sql.toString(), new UserRowMapper(), userIds.toArray());
        } catch (DataAccessException e) {
            return new ArrayList<>();
        } catch (Throwable e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean updatePictureNameForUser(int userId, String pictureName) {
        try {
            JdbcTemplate update = newJdbcTemplate();
            String sql = "UPDATE User SET picture_name=? WHERE user_id=?";
            return update.update(sql, pictureName, userId) > 0;
        } catch (DataAccessException e) {
            return false;
        } catch (Throwable e1) {
            throw new DAOException(e1);
        }
    }

    @Override
    public void deleteAll() {

    }

    class UserRowMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new UserImpl(rs.getInt("id"), rs.getString("firebase_id"),
                    rs.getString("public_id"), rs.getString("email"),
                    rs.getString("name"), rs.getString("picture_name"));
        }

    }
}
