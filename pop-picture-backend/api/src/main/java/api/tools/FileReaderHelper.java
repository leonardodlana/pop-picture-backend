package api.tools;

import org.apache.commons.io.IOUtils;

import java.io.*;

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

public class FileReaderHelper {

    public String readFile(String filename) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(Thread.currentThread().getContextClassLoader().getResource(filename).getPath());
            return IOUtils.toString(fileReader);
        } catch (Exception e) {
            InputStream inputStream = null;
            try {
                inputStream = getClass().getResourceAsStream("/" + filename);
                return IOUtils.toString(new InputStreamReader(inputStream));
            } catch (Exception e2) {
                e2.printStackTrace();
            } finally {
                if (fileReader != null) {
                    try {
                        fileReader.close();
                    } catch (IOException ignored) {
                        e.printStackTrace();
                    }
                }
            }

            e.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public InputStream readFileAsStream(String filename) {

        try {
            return new FileInputStream(Thread.currentThread().getContextClassLoader().getResource(filename).getPath());
        } catch (Exception e) {
            try {
                return getClass().getResourceAsStream("/" + filename);
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            e.printStackTrace();
        }

        return null;
    }
}
