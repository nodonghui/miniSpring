package org.example.ioccontainer;

import java.awt.*;
import java.util.Scanner;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

public class ComponentScanner {

	public static Set<Class<?>> scan(String basePackage) {
		// 직접 구현시 자바 프로젝트 내부를 파일시스템으로 탐색해 .class파일 목록을 받아와 class<T> 파일로 추출해 사용해야함
		Reflections reflections =new Reflections(basePackage, Scanners.TypesAnnotated);
		return reflections.getTypesAnnotatedWith(Component.class);
	}
}
