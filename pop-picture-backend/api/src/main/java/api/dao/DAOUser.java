package api.dao;

import api.data.User;

import java.util.List;
import java.util.Set;

/**
 * Created by Leonardo Lana
 * Github: https://github.com/leonardodlana
 * <p>
 * Copyright 2018 Leonardo Lana
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public interface DAOUser extends DAO<User> {

    User selectByFirebaseId(String firebaseId);

    List<User> selectByIds(Set<Integer> userIds);

    void updateUserPicture(int userId, String pictureName);

    User selectByPublicId(String publicId);

    void updateUserName(int userId, String name);
}
