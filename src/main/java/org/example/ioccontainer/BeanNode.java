package org.example.ioccontainer;

import java.util.HashSet;
import java.util.Set;

public class BeanNode {
    Class<?> clazz;
    Set<Class<?>> dependencies = new HashSet<>();

    public BeanNode(Class<?> clazz) {
        this.clazz = clazz;
    }
}
