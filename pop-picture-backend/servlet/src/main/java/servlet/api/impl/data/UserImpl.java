package servlet.api.impl.data;

import api.data.User;

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

public class UserImpl implements User {

    private final int mId;
    private final String mFirebaseId;
    private final String mPublicId;
    private final String mEmail;
    private final String mName;
    private final String mPictureName;

    public UserImpl(int id, String firebaseId, String publicId, String email, String name, String pictureName) {
        mId = id;
        mFirebaseId = firebaseId;
        mPublicId = publicId;
        mEmail = email;
        mName = name;
        mPictureName = pictureName;
    }

    public UserImpl(String firebaseId, String publicId, String email, String name, String pictureName) {
        this(-1, firebaseId, publicId, email, name, pictureName);
    }

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public String getFirebaseId() {
        return mFirebaseId;
    }

    @Override
    public String getPublicId() {
        return mPublicId;
    }

    @Override
    public String getEmail() {
        return mEmail;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getPictureName() {
        return mPictureName;
    }
}
