package servlet.main;

import api.dao.DAOUserPicture;
import api.main.AppEnvironment;
import api.tools.JSONSerializer;
import api.tools.Logger;
import api.tools.SysoutLogger;
import basis.brickness.Brickness;
import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.Closure;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import servlet.api.impl.dao.DAOUserImpl;
import servlet.api.impl.dao.DAOUserPictureImpl;
import servlet.api.impl.tools.HashFunctionsImpl;
import servlet.quadtree.QuadTree;

import java.io.IOException;

import static basis.environments.Environments.my;
import static servlet.main.ServerConstants.MAX_THREADS;
import static servlet.main.ServerConstants.PORT;

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

public class Launcher {

    private static Launcher INSTANCE;
    private static Logger LOGGER;

    private Environment mEnvironment;
    private Server mServer;

    public Launcher() throws ClassNotFoundException, IOException {
        mEnvironment = Brickness.newBrickContainer(getBricks());
        Environments.runWith(mEnvironment, (Closure) () -> my(QuadTree.class).load(my(DAOUserPicture.class).selectAllForGeoLocation()));
    }

    private Object[] getBricks() throws ClassNotFoundException, IOException {
        return new Object[]{
                LOGGER,
                new JSONSerializer(),
                new DAOUserImpl(),
                new DAOUserPictureImpl(),
                new QuadTree(),
                new HashFunctionsImpl()
        };
    }

    protected void initializeJetty() {
        mServer = new Server(new QueuedThreadPool(MAX_THREADS));

        ServerConnector connector = new ServerConnector(mServer);
        connector.setIdleTimeout(7200000);
        connector.setPort(PORT);
        mServer.setConnectors(new Connector[]{connector});
        ServletContextHandler context = new ServletContextHandler(mServer, "/pop_picture");
        context.addServlet(new ServletHolder(new CmdServlet()), "/cmd");
        SessionHandler sh = new SessionHandler();
        context.setSessionHandler(sh);
    }

    // Static methods

    public synchronized static Environment getEnvironment() {
        return getEnvironment(true);
    }

    public synchronized static Environment getEnvironment(boolean force) {
        Launcher instance = singleton(force);

        if (instance == null)
            return null;

        return instance.mEnvironment;
    }

    public static Launcher singleton() {
        return singleton(true);
    }

    public static Launcher singleton(boolean force) {
        if (INSTANCE == null && force) {
            try {
                INSTANCE = new Launcher();
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        return INSTANCE;
    }

    public static boolean isEnvironmentReady() {
        return INSTANCE != null;
    }

    public static void startJetty() throws Exception {
        Environments.runWith(singleton().mEnvironment, (Closure) () -> singleton().initializeJetty());
        singleton().mServer.start();
    }

    static {
        LOGGER = new SysoutLogger();
        AppEnvironment.setCurrent(AppEnvironment.PROD);
    }

    /**
     * Used on local machine to simulate a servlet using Jetty.
     * No need for tomcat or anything.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Launcher.startJetty();
    }

}
