package org.example.ioccontainer;

@Component
@Qualifier("toss")
public class TossPayService implements HelloService {
	public void sayHello() {
		System.out.println("Hello, DI Container with toss @Qualifier!");
	}
}
