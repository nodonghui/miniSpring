package org.example.ioccontainer;



@Component
public class A {
    @Inject
    private B b;

    public A() {
        System.out.println("✅ A 생성됨");
    }
}
