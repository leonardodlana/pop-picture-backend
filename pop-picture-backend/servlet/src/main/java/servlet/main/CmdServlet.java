package servlet.main;

import api.exceptions.InvalidParameterException;
import api.tools.Logger;
import api.tools.Serializer;
import api.tools.Utils;
import basis.environments.Environments;
import basis.lang.Closure;
import com.google.common.io.CharStreams;
import servlet.action.BaseAction;
import servlet.action.Login;
import servlet.action.SecureAction;
import servlet.action.Status;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static basis.environments.Environments.my;
import static servlet.action.BaseAction.*;
import static servlet.main.ServerConstants.*;

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

public class CmdServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
        super.init();
        setupEnvironment();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        setupEnvironment();
    }

    private void setupEnvironment() {
        Launcher.getEnvironment();
    }

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException, ServletException {
        if (!Launcher.isEnvironmentReady()) {
            resp.setStatus(HTTP_CODE_SERVER_NOT_READY);
            return;
        }

        final boolean wait[] = new boolean[]{true};
        final IOException ioEx[] = new IOException[1];
        try {
            Environments.runWith(Launcher.getEnvironment(), (Closure) () -> {
                try {
                    run(req, resp);
                } catch (IOException e) {
                    ioEx[0] = e;
                }
                wait[0] = false;
            });

        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            while (true) {
                if (!wait[0])
                    return;
                if (ioEx[0] != null)
                    throw ioEx[0];
                Thread.sleep(50);
            }

        } catch (InterruptedException ex) {
            throw new ServletException(ex);
        }
    }

    private static void run(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        long start = System.currentTimeMillis();
        try {
            StringBuilder responseBuilder = new StringBuilder();
            Map<String, Object> parameters = getParameters(req);

            my(Logger.class).log("Raw request: " + my(Serializer.class).serialize(parameters));

            String actionString;

            if (parameters.containsKey(KEY_ACTION))
                actionString = (String) parameters.get(KEY_ACTION);
            else {
                actionString = Status.ACTION_CHECK;
            }

            String tmp[] = (actionString).split("\\.");
            String clazz = tmp[0];
            String method = tmp[1];

            String classPath = BaseAction.class.getPackage().getName() + "." + clazz;
            BaseAction action = (BaseAction) Class.forName(classPath).newInstance();

            Method target = action.getClass().getMethod(method, HttpServletRequest.class, Map.class, StringBuilder.class);
            SecureAction secureAction = target.getAnnotation(SecureAction.class);

            HttpSession session = req.getSession(false);

            if (session == null && secureAction != null && secureAction.isSecure()) {
                respond(HTTP_CODE_UNAUTHORIZED, resp, responseBuilder.toString());
                my(Logger.class).error("Unauthorized user, requesting authorization");
                return;
            } else if(session != null){
                getParametersFromSession(session, parameters);
            }

            target.invoke(action, req, parameters, responseBuilder);

            String response = responseBuilder.toString();

            my(Logger.class).log("Response after: " + ((System.currentTimeMillis() - start) / 1000) + "   " + responseBuilder.toString());

            respond(resp, response);
        } catch (Throwable t) {
            if(t instanceof InvalidParameterException) {
                respond(HTTP_CODE_BAD_REQUEST, resp, t.getMessage());
                return;
            }

            my(Logger.class).error(t.getMessage());
            my(Logger.class).error(t);
            respond(HTTP_CODE_ERROR, resp, t.toString());
        }
    }

    private static void respond(HttpServletResponse resp, String string) {
        respond(HTTP_CODE_OK, resp, string);
    }

    private static void respond(int code, HttpServletResponse resp, String string) {
        try {
            resp.setStatus(code);
            resp.getWriter().write(string);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getParameters(HttpServletRequest req) {
        Map<String, Object> parameters = new HashMap<String, Object>();

        if (req.getMethod().equals("POST")) {
            String rawPost = readPostContent(req);


            if (!Utils.isTextEmpty(rawPost))
                parameters.putAll((Map<String, Object>) my(Serializer.class).deserialize(rawPost));
        }

        return parameters;
    }

    private static void getParametersFromSession(HttpSession session, Map<String, Object> parameters) {
        Enumeration<String> paramsName = session.getAttributeNames();
        String name;
        while(paramsName.hasMoreElements()) {
            name = paramsName.nextElement();
            parameters.put(name, session.getAttribute(name));
        }
    }

    private static String readPostContent(HttpServletRequest req) {
        try {
            return CharStreams.toString(req.getReader());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

}