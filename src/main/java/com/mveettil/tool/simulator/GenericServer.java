/**
 * 
 * @author ManMohan Veettil
 * 
 * This is a generic simulator application which you can configure to run on any port
 * 
 */

package com.mveettil.tool.simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.mveettil.tool.simulator.utils.ServerUtils;

public class GenericServer implements Runnable {

	private static Logger logger = Logger.getLogger(GenericServer.class.getName());

	public static String port;
	public static String delay;
	public static String response;
	public static String respXml;
	private static Properties props;

	private static void init() {

		props = getProperties();
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

	private static Properties getProperties() {

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

	private static String getProperty(String name, boolean required) {

		String value = props.getProperty(name);
		if (required && value == null) {
			logger.error("property " + name + " not set");
			System.exit(-1);
		}
		return value;
	}

	public static void main(String args[]) {

		init();
		final Thread service = new Thread(new GenericServer());
		service.start();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.warn("Caught <ctrl-c>, shutting the service down!");
				service.interrupt();
			}
		});
	}

	public void run() {

		try {
			ServerSocket serverSocket = new ServerSocket(Integer.parseInt(GenericServer.port));
			try {
				while (!Thread.currentThread().isInterrupted()) {
					Socket socket = serverSocket.accept();
					logger.info("request from host:" + socket.getInetAddress().getHostName());
					new Thread(new Worker(socket)).start();
				}
			} finally {
				if (serverSocket != null)
					serverSocket.close();
			}
		} catch (Exception x) {
			logger.error("Network exception:", x);
		}

	}

}

class Worker implements Runnable {

	private static Logger logger = Logger.getLogger(Worker.class.getName());

	private Socket socket;

	public Worker(Socket s) {
		this.socket = s;

	}

	public void run() {
		try {
			try {
				PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);

				if (GenericServer.delay != null && !GenericServer.delay.equalsIgnoreCase("0")) {
					logger.debug("delaying response for " + GenericServer.delay + " milliseconds");
					Thread.sleep(Long.parseLong(GenericServer.delay));

				}

				out.write(GenericServer.respXml);
				out.flush();

			} finally {
				if (this.socket != null)
					this.socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
