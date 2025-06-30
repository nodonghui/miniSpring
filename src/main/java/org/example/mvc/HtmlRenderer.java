package org.example.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HtmlRenderer {

	public static String render(ModelAndView mv) {
		String content = readHtml(mv.getViewName());
		for(Map.Entry<String ,Object> entry : mv.getModel().entrySet()) {
			content = content.replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
		}
		return content;
	}

	private static String readHtml(String filename) {
		try (InputStream is = HtmlRenderer.class.getClassLoader().getResourceAsStream(filename)) {
			if(is == null) return "<h1>View not found</h1>";
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			return "<h1>Template read error</h1>";
		}
	}
}
