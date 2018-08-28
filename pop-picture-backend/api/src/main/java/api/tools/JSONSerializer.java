package api.tools;

import basis.brickness.Brick;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import flexjson.JSONDeserializer;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

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

@Brick
public class JSONSerializer implements Serializer {

	private boolean _prettyFormat;
	private Gson _serializer;
	private Gson _prettySerializer;
	private JSONDeserializer<Object> _deserialize;

	public JSONSerializer() {
		this(false);
	}

	public JSONSerializer(boolean prettyFormat) {
		_prettySerializer = new GsonBuilder().setPrettyPrinting().create();
		_serializer = new GsonBuilder().create();
		_prettyFormat = prettyFormat;
		_deserialize = new JSONDeserializer<Object>();
	}

	public String serialize(Object object) {
		return serialize(object, _prettyFormat);
	}

	public String serialize(Object object, boolean pretty) {
		if (object instanceof InvocationTargetException) {
			object = ((InvocationTargetException) object).getTargetException();
			object = ((Throwable) object).getStackTrace();
		}

		if (pretty)
			return _prettySerializer.toJson(object);
		return _serializer.toJson(object);
	}

	public Object deserialize(String txt) {
		return _deserialize.deserialize(txt);
	}

}