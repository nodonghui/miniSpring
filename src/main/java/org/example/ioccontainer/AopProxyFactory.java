package org.example.ioccontainer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AopProxyFactory {

	public static Object createProxy(Object target) {
		Class<?> targetClass =target.getClass();
		Class<?>[] interfaces = targetClass.getInterfaces();

		if(interfaces.length == 0) {
			return target;
		}

		return Proxy.newProxyInstance(
			targetClass.getClassLoader(),
			interfaces,
			new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					Method targetMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());

					if (targetMethod.isAnnotationPresent(Loggable.class)) {
						System.out.println("🔍 [AOP] 시작: " + method.getName());
						Object result = method.invoke(target, args);
						System.out.println("✅ [AOP] 종료: " + method.getName());
						return result;
					}

					return method.invoke(target, args);
				}
			}
		);
	}
}
