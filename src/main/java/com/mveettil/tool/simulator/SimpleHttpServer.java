package com.mveettil.tool.simulator;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.mveettil.tool.simulator.utils.ServerUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class SimpleHttpServer {
	private static Logger logger = Logger.getLogger(SimpleHttpServer.class.getName());

	public static String port;
	public static String delay;
	public static String response;
	public static String respXml;
	private static Properties props;

	private static void init() {

		props = ServerUtils.getProperties();
		port = getProperty("port", true);
		delay = getProperty("delay", false);
		response = getProperty("response", true);

		try {
			respXml = ServerUtils.readXmlFile(response);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("response xml is null", e);
			System.exit(-1);

		}

	}

	private static String getProperty(String name, boolean required) {

		String value = props.getProperty(name);
		if (required && value == null) {
			logger.error("property " + name + " not set");
			System.exit(-1);
		}
		return value;
	}

	public static void main(String[] args) throws Exception {
		init();
		HttpServer server = HttpServer.create(new InetSocketAddress(Integer.parseInt(port)), 0);
		server.createContext("/test", new MyHandler());
		server.setExecutor(null); // creates a default executor
		server.start();

	}

	static class MyHandler implements HttpHandler {
		public void handle(HttpExchange t) {
			try {

				if (delay != null && !delay.equalsIgnoreCase("0")) {
					logger.debug("delaying response for " + delay + " milliseconds");
					try {
						Thread.sleep(Long.parseLong(delay));
					} catch (InterruptedException e) {
						logger.error("sleep call interrupted:", e);
					}
				}

				t.sendResponseHeaders(200, respXml.length());
				t.getResponseHeaders().set("Content-Type", "text/xml");
				OutputStream os = t.getResponseBody();
				os.write(respXml.getBytes());
				os.close();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
