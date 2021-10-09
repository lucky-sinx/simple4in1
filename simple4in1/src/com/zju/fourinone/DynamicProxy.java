package com.zju.fourinone;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxy implements InvocationHandler {
    private Object[] object;

    public DynamicProxy() {
    }

    public DynamicProxy(Object object) {
        this.object = new Object[] {object};
    }

    public void setObject(Object object) {
        this.object = new Object[] {object};
    }

    public Object bind(Class cls) {
        return Proxy.newProxyInstance(cls.getClassLoader(), new Class<?>[]{cls}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        MyWorkerService service=(MyWorkerService) this.object[0];
//
//        for (Method method1 : service.getClass().getMethods()) {
//            if (method1.getName().equals(method.getName())) {
//                return method1.invoke(service, args);
//            }
//        }
//        return null;

        for (Method method1 : this.object[0].getClass().getMethods()) {
            if (method1.getName().equals(method.getName())) {
                return method1.invoke(this.object[0], args);
            }
        }
        return null;
    }
}
