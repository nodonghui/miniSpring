package org.example.ioccontainer;

@Component
public class UserController {

	// 필드 주입 -> 불변성 보장 x
	private final HelloService helloService;

	@Inject
	public UserController(@Qualifier("kakao") HelloService helloService) {
		this.helloService = helloService;
	}

	@Loggable
	public void process() {
		helloService.sayHello();
	}
}