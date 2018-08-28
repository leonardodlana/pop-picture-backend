package api.tools;

import basis.brickness.Brick;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

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
public class SysoutLogger implements Logger {

	private static final String dtFormat = "yyyy/MM/dd hh:mm:ss:SS";

	protected void print(String msg) {System.out.println(msg);}
	protected void print(Throwable t) {t.printStackTrace();}
	protected void print(String msg, Throwable t) {
		print(msg);
		t.printStackTrace();
	}

	public void warn(String msg) {print("WARN: " + msg);}
	public void warn(Throwable t) {	print("WARN: " + Arrays.toString(t.getStackTrace()));}
	public void warn(String msg, Throwable t) {	print("WARN: " + msg + "\n" + Arrays.toString(t.getStackTrace()));}

	public void error(String msg) {print("ERROR: " + msg);}
	public void error(Throwable t) {	print("ERROR: " + Arrays.toString(t.getStackTrace()));}
	public void error(String msg, Throwable t) { print("ERROR: " + msg + "\n" + Arrays.toString(t.getStackTrace()));}

	public void log(String msg) {print("LOG: " + msg);}
	
	public void log(Date dt0, Date dt1, String request, String response) {
		
		long t0 = System.currentTimeMillis();
		int duration = (int)(dt1.getTime() - dt0.getTime()  + System.currentTimeMillis() - t0);

		SimpleDateFormat formater = new SimpleDateFormat(dtFormat);
		String xml = 
			"'transaction':{" +
			"'thread':'" + Thread.currentThread().getId() + "', " +
			"'startime':'" + formater.format(dt0) + "', " +
			"'duration':'" + duration + "', ";
		xml += "'request':" +  request;		
		xml += ", 'response':" + response;		
		xml += "}";
		
		print(xml);
	}
}