package servlet.action;

import api.dao.DAOUser;
import api.data.User;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
public class UserData extends BaseAction {

    @SecureAction(isSecure = true)
    public void changePicture(HttpServletRequest request, Map<String, Object> params, StringBuilder response) {
        validateParameters(params, response, KEY_PICTURE_NAME);

        int userId = (int) params.get(KEY_USER_ID);
        String pictureName = (String) params.get(KEY_PICTURE_NAME);

        my(DAOUser.class).updateUserPicture(userId, pictureName);
    }

    @SecureAction(isSecure = true)
    public void update(HttpServletRequest request, Map<String, Object> params, StringBuilder response) {
        validateParameters(params, response, KEY_NAME);

        int userId = (int) params.get(KEY_USER_ID);
        String name = (String) params.get(KEY_NAME);

        my(DAOUser.class).updateUserName(userId, name);
    }

    public void get(HttpServletRequest request, Map<String, Object> params, StringBuilder response) {
        validateParameters(params, response, KEY_USER_PUBLIC_ID);

        String publicId = (String) params.get(KEY_USER_PUBLIC_ID);

        User user = my(DAOUser.class).selectByPublicId(publicId);

        if(user != null) {
            response.append(new UserDataResponse(user).toString());
        }
    }

    private class UserDataResponse {

        JsonObject mData = new JsonObject();

        public UserDataResponse(User user) {
            mData.addProperty(KEY_USER_PUBLIC_ID, user.getPublicId());
            mData.addProperty(KEY_NAME, user.getName());
            mData.addProperty(KEY_PICTURE_NAME, user.getPictureName());
        }

        @Override
        public String toString() {
            return mData.toString();
        }
    }
}
