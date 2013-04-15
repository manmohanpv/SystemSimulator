package com.mveettil.tool.simulator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

    public static void main(String args[]) {
	try {
	    Socket socket = new Socket("localhost", 8000);
	    try {
		DataOutputStream outToServer = new DataOutputStream(
			socket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(
			new InputStreamReader(socket.getInputStream()));
		outToServer.writeBytes("text" + '\n');
		String response = inFromServer.readLine();
		System.out.println("FROM SERVER: " + response);
	    } finally {
		if (socket != null)
		    socket.close();
	    }
	} catch (Exception e) {
	}
    }

}
