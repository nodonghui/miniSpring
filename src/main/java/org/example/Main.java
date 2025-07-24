package org.example;

import java.util.List;

import org.example.ioccontainer.*;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

	public static void main(String[] args) {
		AnnotationContainer container = new AnnotationContainer("org.example");

		A a = container.getBean(A.class);
		B b = container.getBean(B.class);
		C c = container.getBean(C.class);

		System.out.println("🟢 모든 빈 주입 완료");

		// 검증용 출력
		assert a != null;
		assert b != null;
		assert c != null;
	}
}
