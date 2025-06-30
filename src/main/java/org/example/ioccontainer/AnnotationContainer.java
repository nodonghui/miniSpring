package org.example.ioccontainer;

import static java.beans.Introspector.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnnotationContainer {
	private Map<Class<?>, Object> beanMap = new HashMap<>();
	private Map<String,Object> nameToBeanMap = new HashMap<>();


	private final Map<Class<?>, Class<?>> interfaceToImplMap = Map.of(
		HelloService.class, HelloServiceImpl.class
	);

	public AnnotationContainer(String basePackage) {
		try {
			Set<Class<?>> componentClasses = ComponentScanner.scan(basePackage);
			// 1. @Component 클래스 인스턴스 생성
			for (Class<?> clazz : componentClasses) {
				if (clazz.isAnnotationPresent(Component.class)) {
					Object instance = createInstance(clazz);
					beanMap.put(clazz, instance);

					String name= resolveQualifierName(clazz);
					nameToBeanMap.put(name,instance);
				}
			}

			// 2. @Inject 필드에 의존성 주입
			for (Object bean : beanMap.values()) {
				Field[] fields = bean.getClass().getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(Inject.class)) {
						Object dependency;
						if(field.isAnnotationPresent(Qualifier.class)) {
							String name = field.getAnnotation(Qualifier.class).value();
							dependency = nameToBeanMap.get(name);
						} else {
							Class<?> fieldType = field.getType();
							dependency = getBeanByType(fieldType);
						}
						field.setAccessible(true);
						field.set(bean, dependency);
					}
				}
			}

			//3 PostConstruct 호출
			for(Object bean : beanMap.values()) {
				for(Method method : bean.getClass().getDeclaredMethods()) {
					if(method.isAnnotationPresent(PostConstruct.class)) {
						method.setAccessible(true);
						method.invoke(bean);
					}
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("DI 초기화 실패", e);
		}
	}

	public <T> T getBean(Class<T> clazz) {
		return clazz.cast(beanMap.get(clazz));
	}

	public Object getBeanByType(Class<?> type) throws Exception {
		Object bean = beanMap.get(type);
		if (bean != null) return bean;

		// 없으면 즉시 생성
		return registerBean(type);
	}

	public Object registerBean(Class<?> clazz) throws Exception {

		// 1. 인터페이스면 구현체로 교체
		if (clazz.isInterface() && interfaceToImplMap.containsKey(clazz)) {
			clazz = interfaceToImplMap.get(clazz); // 구현체로 변경
		}

		// 중복 생성 방지
		if (beanMap.containsKey(clazz)) return beanMap.get(clazz);

		// 우선 null로 넣어 두고 (선등록)
		beanMap.put(clazz, null);

		Object instance = createInstance(clazz);
		Object proxied = AopProxyFactory.createProxy(instance);
		beanMap.put(clazz, instance);
		nameToBeanMap.put(resolveQualifierName(clazz), instance);

		return instance;
	}

	private Object createInstance(Class<?> clazz) throws Exception {
		for(Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			if(constructor.isAnnotationPresent(Inject.class)) {
				Parameter[] params = constructor.getParameters();
				Object[] args = new Object[params.length];
				for(int i=0;i<params.length;i++) {
					Qualifier qualifier = params[i].getAnnotation(Qualifier.class);
					if (qualifier != null) {
						args[i] = nameToBeanMap.get(qualifier.value());
					} else {
						args[i] = getBeanByType(params[i].getType());
					}
				}
				constructor.setAccessible(true);
				return  constructor.newInstance(args);
			}
		}


		return clazz.getDeclaredConstructor().newInstance();
	}

	private String resolveQualifierName(Class<?> clazz) {
		if(clazz.isAnnotationPresent(Qualifier.class)) {
			return clazz.getAnnotation(Qualifier.class).value();
		}

		return decapitalize(clazz.getSimpleName());
	}

	private String decapitalize(String name) {
		if (name == null || name.isEmpty()) return name;
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}
}