package org.example.ioccontainer;

@Component
@Qualifier("kakao")
public class KakaoPayService implements HelloService {


	public void sayHello() {
		System.out.println("Hello, DI Container with kakao @Qualifier!");
	}
}