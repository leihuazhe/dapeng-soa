package com.github.dapeng.impl.plugins.netty;

import com.github.dapeng.core.helper.SoaSystemEnvProperties;
import com.github.dapeng.impl.filters.LogFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用MDC辅助类
 * @author: zhup
 * @date: 2018/9/7 10:11
 */

public class MdcCtxInfoUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MdcCtxInfoUtil.class);
    private static final Map<ClassLoader, MdcCtxInfo> mdcCtxInfoCache = new ConcurrentHashMap<>(16);

    static class MdcCtxInfo {
        final ClassLoader appClassLoader;
        final Class<?> mdcClass;
        final Method put;
        final Method remove;

        MdcCtxInfo(ClassLoader cl, Class<?> mdcClass, Method put, Method remove) {
            this.appClassLoader = cl;
            this.mdcClass = mdcClass;
            this.put = put;
            this.remove = remove;
        }
    }


    public static void switchMdcToAppClassLoader(String methodName, ClassLoader appClassLoader, String sessionTid) {
        try {
            MdcCtxInfo mdcCtxInfo = mdcCtxInfoCache.get(appClassLoader);
            if (mdcCtxInfo == null) {
                synchronized (appClassLoader) {
                    mdcCtxInfo = mdcCtxInfoCache.get(appClassLoader);
                    if (mdcCtxInfo == null) {
                        Class<?> mdcClass = appClassLoader.loadClass(MDC.class.getName());

                        mdcCtxInfo = new MdcCtxInfo(appClassLoader,
                                mdcClass,
                                mdcClass.getMethod("put", String.class, String.class),
                                mdcClass.getMethod("remove", String.class)
                        );
                        mdcCtxInfoCache.put(appClassLoader, mdcCtxInfo);
                    }
                }
            }
            if (methodName.equals("put")) {
                mdcCtxInfo.put.invoke(mdcCtxInfo.mdcClass, SoaSystemEnvProperties.KEY_LOGGER_SESSION_TID, sessionTid);
            } else {
                mdcCtxInfo.remove.invoke(mdcCtxInfo.mdcClass, SoaSystemEnvProperties.KEY_LOGGER_SESSION_TID);
            }
        } catch (ClassNotFoundException | NoSuchMethodException
                | IllegalAccessException |
                InvocationTargetException e) {
            LOGGER.error(appClassLoader.getClass().getSimpleName() + "::switchMdcToAppClassLoader," + e.getMessage(), e);
        }
    }
}
