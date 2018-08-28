package servlet.action;

import api.exceptions.InvalidParameterException;
import api.exceptions.ServerException;
import api.tools.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

public abstract class BaseAction {

    public static final String KEY_ACTION = "action";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_FIREBASE_ID = "firebase_id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_FILENAME = "filename";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_USER_PUBLIC_ID = "user_public_id";
    public static final String KEY_LIKES_COUNT = "likes_count";
    public static final String KEY_PICTURE_ID = "picture_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PICTURE_NAME = "picture_name";
    public static final String KEY_LIKED = "liked";

    public static final int CODE_OK = 0;
    public static final int CODE_PARAMETER_ERROR = 1;
    public static final int CODE_SERVER_EXCEPTION = 2;

    protected void validateParameters(Map<String, Object> parameters, StringBuilder output, String... persistentData) {
        for (int i = 0; i < persistentData.length; i++) {
            if (!parameters.containsKey(persistentData[i])) {
                output.append("{\"status\":" + CODE_PARAMETER_ERROR + ", \"msg\"=\"requerid parameter not found (")
                        .append(persistentData[i])
                        .append(")\"}");

                throw new InvalidParameterException("Missing param: " + persistentData[i]);
            }
        }
    }

}
