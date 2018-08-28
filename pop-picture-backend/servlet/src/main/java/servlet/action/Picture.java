package servlet.action;

import api.dao.DAOUser;
import api.dao.DAOUserPicture;
import api.data.User;
import api.data.UserPicture;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import servlet.api.impl.data.UserPictureImpl;
import servlet.quadtree.QuadTree;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static basis.environments.Environments.my;

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

public class Picture extends BaseAction {

    @SecureAction(isSecure = true)
    public void add(HttpServletRequest request, Map<String, Object> params, StringBuilder response) {
        validateParameters(params, response, KEY_USER_ID, KEY_FILENAME, KEY_TITLE, KEY_DESCRIPTION, KEY_LATITUDE, KEY_LONGITUDE);

        int userId = (int) params.get(KEY_USER_ID);
        String filename = (String) params.get(KEY_FILENAME);
        String title = (String) params.get(KEY_TITLE);
        String description = (String) params.get(KEY_DESCRIPTION);
        double latitude = (double) params.get(KEY_LATITUDE);
        double longitude = (double) params.get(KEY_LONGITUDE);

        UserPicture userPicture = new UserPictureImpl(userId, filename, System.currentTimeMillis(), title, description, latitude, longitude, 0, 0);
        my(DAOUserPicture.class).save(userPicture);

        if (userPicture.getId() != -1) {
            my(QuadTree.class).addNeighbour(userPicture.getId(), latitude, longitude);
        }
    }

    @SecureAction(isSecure = true)
    public void remove(HttpServletRequest request, Map<String, Object> params, StringBuilder response) {
        validateParameters(params, response, KEY_USER_ID, KEY_PICTURE_ID);

        int userId = (int) params.get(KEY_USER_ID);
        long pictureId = (long) params.get(KEY_PICTURE_ID);

        int rowsAffected = my(DAOUserPicture.class).deleteById(userId, pictureId);

        if (rowsAffected > 0) {
            my(QuadTree.class).removeNeighbour(pictureId);
        }
    }

    public void findNearby(HttpServletRequest request, Map<String, Object> params, StringBuilder response) {
        validateParameters(params, response, KEY_LATITUDE, KEY_LONGITUDE);

        double latitude = (double) params.get(KEY_LATITUDE);
        double longitude = (double) params.get(KEY_LONGITUDE);
        double rangeInKm = 25;

        Set<Long> picturesIds = my(QuadTree.class).findNeighboursIds(latitude, longitude, rangeInKm);

        List<UserPicture> picturesList = my(DAOUserPicture.class).selectByIds(picturesIds);
        Set<Integer> userIds = new HashSet<>();

        for (UserPicture picture : picturesList)
            userIds.add(picture.getUserId());

        List<User> userList = my(DAOUser.class).selectByIds(userIds);
        Map<Integer, User> users = new HashMap<>();

        for (User user : userList) {
            users.put(user.getId(), user);
        }

        Set<Long> likedByUser;

        if(params.containsKey(KEY_USER_ID)) {
            int userId = (int) params.get(KEY_USER_ID);
            likedByUser = my(DAOUserPicture.class).selectLikesByUser(userId);
        } else {
            likedByUser = new HashSet<>();
        }

        response.append(new FindPicturesResponse(users, picturesList, likedByUser).toString());
    }

    public void findLiked(HttpServletRequest request, Map<String, Object> params, StringBuilder response) {
        validateParameters(params, response, KEY_LATITUDE, KEY_LONGITUDE);

        double latitude = (double) params.get(KEY_LATITUDE);
        double longitude = (double) params.get(KEY_LONGITUDE);
        double rangeInKm = 100;

        Set<Long> picturesIds = my(QuadTree.class).findNeighboursIds(latitude, longitude, rangeInKm);

        List<UserPicture> picturesList = my(DAOUserPicture.class).selectByIds(picturesIds);
        Set<Integer> userIds = new HashSet<>();

        for (UserPicture picture : picturesList)
            userIds.add(picture.getUserId());

        List<User> userList = my(DAOUser.class).selectByIds(userIds);
        Map<Integer, User> users = new HashMap<>();

        for (User user : userList) {
            users.put(user.getId(), user);
        }

        Set<Long> likedByUser;

        if(params.containsKey(KEY_USER_ID)) {
            int userId = (int) params.get(KEY_USER_ID);
            likedByUser = my(DAOUserPicture.class).selectLikesByUser(userId);
        } else {
            likedByUser = new HashSet<>();
        }

        response.append(new FindPicturesResponse(users, picturesList, likedByUser).toString());
    }

    @SecureAction(isSecure = true)
    public void addLike(HttpServletRequest request, Map<String, Object> params, StringBuilder response) {
        validateParameters(params, response, KEY_USER_ID, KEY_PICTURE_ID);

        int userId = (int) params.get(KEY_USER_ID);
        long pictureId = (long) params.get(KEY_PICTURE_ID);

        my(DAOUserPicture.class).addLike(userId, pictureId);
    }

    @SecureAction(isSecure = true)
    public void removeLike(HttpServletRequest request, Map<String, Object> params, StringBuilder response) {
        validateParameters(params, response, KEY_USER_ID, KEY_PICTURE_ID);

        int userId = (int) params.get(KEY_USER_ID);
        long pictureId = (long) params.get(KEY_PICTURE_ID);

        my(DAOUserPicture.class).removeLike(userId, pictureId);
    }

    @SecureAction(isSecure = true)
    public void report(HttpServletRequest request, Map<String, Object> params, StringBuilder response) {
        validateParameters(params, response, KEY_PICTURE_ID);

        int userId = (int) params.get(KEY_USER_ID);
        long pictureId = (long) params.get(KEY_PICTURE_ID);

        my(DAOUserPicture.class).addReport(userId, pictureId);
    }

    private class FindPicturesResponse {

        JsonArray mJsonArray = new JsonArray();

        FindPicturesResponse(Map<Integer, User> users, List<UserPicture> picturesList, Set<Long> likedByUser) {
            JsonObject jsonObject;
            for (UserPicture userPicture : picturesList) {
                //TODO define a better rule
                if(userPicture.getReportsCount() > 2)
                    continue;
                jsonObject = new JsonObject();
                jsonObject.addProperty(KEY_USER_PUBLIC_ID, users.get(userPicture.getUserId()).getPublicId());
                jsonObject.addProperty(KEY_PICTURE_ID, userPicture.getId());
                jsonObject.addProperty(KEY_FILENAME, userPicture.getFilename());
                jsonObject.addProperty(KEY_TITLE, userPicture.getTitle());
                jsonObject.addProperty(KEY_DESCRIPTION, userPicture.getDescription());
                jsonObject.addProperty(KEY_LATITUDE, userPicture.getLatitude());
                jsonObject.addProperty(KEY_LONGITUDE, userPicture.getLongitude());
                jsonObject.addProperty(KEY_LIKES_COUNT, userPicture.getLikesCount());
                jsonObject.addProperty(KEY_LIKED, likedByUser.contains(userPicture.getId()) ? 1 : 0);
                mJsonArray.add(jsonObject);
            }
        }

        @Override
        public String toString() {
            return mJsonArray.toString();
        }
    }
}
