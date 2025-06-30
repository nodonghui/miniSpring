package org.example.mvc;

import java.io.BufferedReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DispatcherServlet {

	private static final Map<String, Method> getMapping = new HashMap<>();
	private static final Map<String, Method> postMapping = new HashMap<>();
	// 실제 스프링에선 ioc 컨테이너에서 컨트롤러 어노테이션 객체 꺼내와 사용함.
	private static final Map<String, Object> controllerInstances = new HashMap<>();

	static {
		try {
			Class<?> controllerClass = HelloController.class;

			if (controllerClass.isAnnotationPresent(Controller.class)) {
				Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
				controllerInstances.put(controllerClass.getName(), controllerInstance);

				for (Method method : controllerClass.getDeclaredMethods()) {
					if (method.isAnnotationPresent(GetMapping.class)) {
						String path = method.getAnnotation(GetMapping.class).value();
						getMapping.put(path, method);
					}
					if (method.isAnnotationPresent(PostMapping.class)) {
						String path = method.getAnnotation(PostMapping.class).value();
						postMapping.put(path, method);
					}
				}

			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize DispatcherServlet", e);
		}
	}

	public static ModelAndView handle(String httpMethod, String fullPath, BufferedReader bodyReader) {
		try {
			String path;
			Map<String,String> queryParams = new HashMap<>();

			if(fullPath.contains("?")) {
				String[] parts = fullPath.split("\\?");
				path = parts[0];
				String queryString =parts[1];

				for(String param : queryString.split("&")) {
					String[] keyValue = param.split("=");
					if(keyValue.length == 2) {
						queryParams.put(keyValue[0],keyValue[1]);
					}
				}
			} else {
				path =fullPath;
			}



			Method method;
			if ("GET".equalsIgnoreCase(httpMethod)) {
				method = getMapping.get(path);
				if (method == null) throw new RuntimeException("No GET mapping found for " + path);
				Object controller = controllerInstances.get(method.getDeclaringClass().getName());

				if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == Map.class) {
					return (ModelAndView) method.invoke(controller, queryParams);
				} else {
					return (ModelAndView) method.invoke(controller);
				}
			} else if ("POST".equalsIgnoreCase(httpMethod)) {
				method = postMapping.get(path);
				if (method == null) throw new RuntimeException("No POST mapping found for " + path);

				// 예시로 간단히 Request Body를 읽어 문자열로 넘기자
				StringBuilder requestBody = new StringBuilder();
				String line;
				while ((line = bodyReader.readLine()) != null && !line.isEmpty()) {
					requestBody.append(line).append("\n");
				}
				//DTO 형식으로 받고 넣고 싶음 1. 메서드 매개변수 조사 -> @requestmapping 같은 어노테이션 있는지 확인 -> DTO 정보 확인
				//DTO 생성후 주입
				Object controller = controllerInstances.get(method.getDeclaringClass().getName());
				return (ModelAndView) method.invoke(controller, requestBody.toString());
			} else {
				throw new RuntimeException("Unsupported HTTP method: " + httpMethod);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to handle request for " + fullPath, e);
		}
	}

}

