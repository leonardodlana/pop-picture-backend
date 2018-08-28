import servlet.quadtree.Neighbour;
import org.junit.Before;
import org.junit.Test;
import servlet.quadtree.QuadTree;

import java.util.Random;
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

public class QuadTreeTest {

    private static final double[] AUCKLAND_CENTRAL = {-36.8493527, 174.7615583};
    private static final double[] AUCKLAND_NEW_MARKET = {-36.8682996, 174.7722289};

    private QuadTree mQuadtree;

    @Before
    public void init() {
        mQuadtree = new QuadTree();
    }

    @Test
    public void add() {
        mQuadtree.addNeighbour(1234, AUCKLAND_CENTRAL[0], AUCKLAND_CENTRAL[1]);
        Set<Neighbour> neighbourSet = mQuadtree.findNeighbours(AUCKLAND_CENTRAL[0], AUCKLAND_CENTRAL[1], 1);
        assert neighbourSet.size() == 1;
    }

    @Test
    public void remove() {
        mQuadtree.addNeighbour(1234, AUCKLAND_CENTRAL[0], AUCKLAND_CENTRAL[1]);
        mQuadtree.removeNeighbour(1234);
        Set<Neighbour> neighbourSet = mQuadtree.findNeighbours(AUCKLAND_CENTRAL[0], AUCKLAND_CENTRAL[1], 1);
        assert neighbourSet.size() == 0;
    }

    @Test
    public void search1() {
        mQuadtree.addNeighbour(1, AUCKLAND_CENTRAL[0], AUCKLAND_CENTRAL[1]);
        Set<Neighbour> neighbourSet = mQuadtree.findNeighbours(AUCKLAND_NEW_MARKET[0], AUCKLAND_NEW_MARKET[1], 1);
        // Point A is further than 1 km than Point B
        assert neighbourSet.size() == 0;
    }

    @Test
    public void search2() {
        mQuadtree.addNeighbour(1, AUCKLAND_CENTRAL[0], AUCKLAND_CENTRAL[1]);
        Set<Neighbour> neighbourSet = mQuadtree.findNeighbours(AUCKLAND_NEW_MARKET[0], AUCKLAND_NEW_MARKET[1], 3);
        assert neighbourSet.size() == 1;
    }

    private int generateRandomId() {
        return new Random().nextInt();
    }

}
