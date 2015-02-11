package com.mveettil.tool.simulator.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ServerUtils {
	private static Logger logger = Logger.getLogger(ServerUtils.class.getName());

	public static Properties getProperties() {

		Properties properties = new Properties();
		try {

			InputStream props = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
			Properties p = new Properties();
			p.load(props);
			for (Object k : p.keySet()) {
				properties.put(k, p.get(k));
			}
		} catch (Exception e) {
			logger.error("error loading config.properites", e);
			System.exit(-1);

		}

		if (properties != null)
			logger.info("config.properties loaded");

		return properties;

	}

	public static String readXmlFile(String fname) throws IOException {

		InputStream fxml = Thread.currentThread().getContextClassLoader().getResourceAsStream(fname);

		String everything = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(fxml));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			everything = sb.toString();
		} finally {
			br.close();
		}
		return everything;
	}

}
