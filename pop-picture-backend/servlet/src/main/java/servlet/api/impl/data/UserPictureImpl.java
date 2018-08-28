package servlet.api.impl.data;

import api.data.UserPicture;

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

public class UserPictureImpl implements UserPicture {

    private long mId;
    private int mUserId;
    private String mFilename;
    private long mDate;
    private String mTitle;
    private String mDescription;
    private double mLatitude;
    private double mLongitude;
    private int mLikesCount;
    private int mReportsCount;

    public UserPictureImpl(long id, int userId, String filename, long date, String title, String description,
                           double latitude, double longitude, int likesCount, int reportsCount) {
        mId = id;
        mUserId = userId;
        mFilename = filename;
        mDate = date;
        mTitle = title;
        mDescription = description;
        mLatitude = latitude;
        mLongitude = longitude;
        mLikesCount = likesCount;
        mReportsCount = reportsCount;
    }

    public UserPictureImpl(int userId, String filename, long date, String title, String description, double latitude,
                           double longitude, int likesCount, int reportsCount) {
        this(-1, userId, filename, date, title, description, latitude, longitude, likesCount, reportsCount);
    }

    public UserPictureImpl(int id, double latitude, double longitude) {
        mId = id;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    @Override
    public void setId(long id) {
        mId = id;
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public int getUserId() {
        return mUserId;
    }

    @Override
    public String getFilename() {
        return mFilename;
    }

    @Override
    public long getDate() {
        return mDate;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getDescription() {
        return mDescription;
    }

    @Override
    public double getLatitude() {
        return mLatitude;
    }

    @Override
    public double getLongitude() {
        return mLongitude;
    }

    @Override
    public int getLikesCount() {
        return mLikesCount;
    }

    @Override
    public int getReportsCount() {
        return mReportsCount;
    }
}
