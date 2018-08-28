package servlet.quadtree;

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

public class QuadTreeConstants {

    public static final double QUADTREE_LAST_NODE_SIZE_IN_KM = 1;

    public static final double QUADTREE_LAST_NODE_SIZE_IN_DEGREE = kmToDegree(QUADTREE_LAST_NODE_SIZE_IN_KM);

    public static final float ONE_DEGREE_IN_KM = 111.f;

    public static double kmToDegree(double km) {
        return km / ONE_DEGREE_IN_KM;
    }

}
