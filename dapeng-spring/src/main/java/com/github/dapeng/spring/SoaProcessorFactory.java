package com.github.dapeng.spring;

import com.github.dapeng.core.CustomConfig;
import com.github.dapeng.core.CustomConfigInfo;
import com.github.dapeng.core.definition.SoaFunctionDefinition;
import com.github.dapeng.core.definition.SoaServiceDefinition;
import com.github.dapeng.core.Processor;
import com.github.dapeng.core.Service;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Soa Processor Factory
 *
 * @author craneding
 * @date 16/1/19
 */
public class SoaProcessorFactory implements FactoryBean<SoaServiceDefinition<?>> {

    private Object serviceRef;
    private String refId;

    public SoaProcessorFactory(Object serviceRef, String refId) {
        this.serviceRef = serviceRef;
        this.refId = refId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SoaServiceDefinition<?> getObject() throws Exception {
        final Class<?> aClass = serviceRef.getClass();
        final List<Class<?>> interfaces = Arrays.asList(aClass.getInterfaces());

        List<Class<?>> filterInterfaces = interfaces.stream()
                .filter(anInterface -> anInterface.isAnnotationPresent(Service.class) && anInterface.isAnnotationPresent(Processor.class))
                .collect(toList());

        if (filterInterfaces.isEmpty()) {
            throw new RuntimeException("not config @Service & @Processor in " + refId);
        }

        Class<?> interfaceClass = filterInterfaces.get(filterInterfaces.size() - 1);

        Processor processor = interfaceClass.getAnnotation(Processor.class);


        Class<?> processorClass = Class.forName(processor.className(), true, interfaceClass.getClassLoader());
        Constructor<?> constructor = processorClass.getConstructor(interfaceClass, Class.class);
        SoaServiceDefinition tProcessor = (SoaServiceDefinition) constructor.newInstance(serviceRef, interfaceClass);
        /**
         * idl service custom config
         */
        if (interfaceClass.isAnnotationPresent(CustomConfig.class)) {
            CustomConfig customConfig = interfaceClass.getAnnotation(CustomConfig.class);
            long timeout = customConfig.timeout();
            tProcessor.setConfigInfo(new CustomConfigInfo(timeout));
        }
        /**
         * 过滤有 @CustomConfig 的方法
         */
        Method[] serviceMethods = interfaceClass.getDeclaredMethods();
        List<Method> configMethod = Arrays.stream(serviceMethods)
                .filter(method -> tProcessor.functions.keySet().contains(method.getName()))
                .filter(method -> method.isAnnotationPresent(CustomConfig.class))
                .collect(Collectors.toList());
        /**
         * 将值设置到 functions 中
         */
        configMethod.forEach(method -> {
            CustomConfig customConfig = method.getAnnotation(CustomConfig.class);
            SoaFunctionDefinition functionDefinition = (SoaFunctionDefinition) tProcessor.functions.get(method.getName());
            functionDefinition.setCustomConfigInfo(new CustomConfigInfo(customConfig.timeout()));
        });

        return tProcessor;
    }

    @Override
    public Class<?> getObjectType() {
        return SoaServiceDefinition.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
