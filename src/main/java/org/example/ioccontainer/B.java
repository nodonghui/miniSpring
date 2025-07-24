package org.example.ioccontainer;

@Component
public class B {
    @Inject
    private C c;

    public B() {
        System.out.println("✅ B 생성됨");
    }
}