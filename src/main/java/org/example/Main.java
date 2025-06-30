package org.example;

import java.util.List;

import org.example.ioccontainer.AnnotationContainer;
import org.example.ioccontainer.HelloServiceImpl;
import org.example.ioccontainer.UserController;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

	public static void main(String[] args) {
		AnnotationContainer container = new AnnotationContainer("org.example");

		UserController controller = container.getBean(UserController.class);
		controller.process();  // HelloService가 자동으로 주입되어 동작함!
	}
}
