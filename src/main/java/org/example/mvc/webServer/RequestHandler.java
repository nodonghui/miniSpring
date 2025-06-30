package org.example.mvc.webServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.example.mvc.DispatcherServlet;
import org.example.mvc.HtmlRenderer;
import org.example.mvc.ModelAndView;

public class RequestHandler  implements Runnable{

	private final Socket clientSocket;

	public RequestHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream())) {

			String line = in.readLine();
			if(line == null || line.isEmpty()) return;

			String[] tokens = line.split(" ");
			if(tokens.length < 2) return;

			String httpMethod = tokens[0];
			String path = tokens[1];

			// 2. 헤더 읽기
			String headerLine;
			int contentLength = 0;
			while (!(headerLine = in.readLine()).isEmpty()) {
				if (headerLine.startsWith("Content-Length:")) {
					contentLength = Integer.parseInt(headerLine.split(":")[1].trim());
				}
			}

			// 3. Request Body 읽기 (POST 전용)
			BufferedReader bodyReader = in;
			// DispatcherServlet이 직접 읽게 넘겨도 됨

			// 4. DispatcherServlet 호출
			ModelAndView mv = DispatcherServlet.handle(httpMethod, path, bodyReader);

			// 5. HTML 렌더링
			String html = HtmlRenderer.render(mv);

			// 6. 응답 보내기
			out.println("HTTP/1.1 200 OK");
			out.println("Content-Type: text/html; charset=utf-8");
			out.println("Content-Length: " + html.getBytes().length);
			out.println();
			out.println(html);
			out.flush();



		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
