package servlet.action;

import api.dao.DAOUser;
import api.data.User;
import api.tools.HashFunctions;
import com.google.gson.JsonObject;
import servlet.api.impl.data.UserImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

public class Login extends BaseAction {

    public static final String ACTION_AUTHORIZE = "Login.authorize";

    public void register(HttpServletRequest request, Map<String, Object> params, StringBuilder response) {
        validateParameters(params, response, KEY_EMAIL, KEY_FIREBASE_ID, KEY_NAME);

        String email = (String) params.get(KEY_EMAIL);
        String firebaseId = (String) params.get(KEY_FIREBASE_ID);
        String name = (String) params.get(KEY_NAME);
        String publicId = my(HashFunctions.class).generatePublicId(firebaseId);

        User user = new UserImpl(firebaseId, publicId, email, name, "");
        my(DAOUser.class).save(user);
        authorize(request, params, response);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(KEY_USER_PUBLIC_ID, publicId);
        response.append(jsonObject.toString());
    }

    public void authorize(HttpServletRequest request, Map<String, Object> params, StringBuilder response) {
        validateParameters(params, response, KEY_FIREBASE_ID);

        String firebaseId = (String) params.get(KEY_FIREBASE_ID);

        User user = my(DAOUser.class).selectByFirebaseId(firebaseId);

        if(user != null) {
            HttpSession session = request.getSession();
            session.setAttribute(KEY_USER_ID, user.getId());
        }
    }

}
