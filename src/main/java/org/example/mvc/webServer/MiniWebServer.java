package org.example.mvc.webServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MiniWebServer {

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(8080);
		System.out.println("Listening on port 8080.....");

		while (true) {
			Socket clientSocket = serverSocket.accept();
			new Thread(new RequestHandler(clientSocket)).start();

		}
	}
}
