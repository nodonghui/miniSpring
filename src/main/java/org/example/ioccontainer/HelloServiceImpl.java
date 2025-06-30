package org.example.ioccontainer;

@Component
public class HelloServiceImpl implements HelloService {

	@Loggable
	@PostConstruct
	public void init() {
		System.out.println("🔧 HelloService 초기화 완료!");
	}

	public void sayHello() {
		System.out.println("Hello, DI Container with @Inject!");
	}
}
