package com.tuuzed.common.webapi;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReflectTest {


    public Response<List<String>> methodMap(String str, Map<String, String> map, String[] array, List<String> list) {

        return null;
    }

    @Test
    public void test() {
        Method[] methods = ReflectTest.class.getMethods();
//        for (Method method:methods) {
//            System.out.println(method);
//        }

        Method method = methods[0];
//        System.out.println(method.getGenericReturnType().getClass());
        for (Type genericParameterType : method.getGenericParameterTypes()) {
            if (genericParameterType instanceof ParameterizedType) {
                System.out.println(((ParameterizedType) genericParameterType).getRawType() == Map.class);
                Type[] actualTypeArguments = ((ParameterizedType) genericParameterType).getActualTypeArguments();
                System.out.println((actualTypeArguments.length == 2 && actualTypeArguments[0] == String.class && actualTypeArguments[1] == Object.class));
                System.out.println(Arrays.toString(((ParameterizedType) genericParameterType).getActualTypeArguments()));
            }
        }
//        System.out.println(Arrays.toString(method.getGenericParameterTypes()));
    }
}
