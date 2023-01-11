package io.daff.uacs.web.aspect;

import io.daff.logging.DaffLogger;
import io.daff.uacs.core.enums.BaseModule;
import io.daff.uacs.service.util.JacksonUtil;
import io.daff.uacs.web.anno.Monitor;
import io.daff.uacs.web.context.IpRequestContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * 监控切面
 *
 * @author daffupman
 * @since 2020/8/31
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)  // 将优先级设置为最高，可以防止Spring事务捕获不到异常
public class MonitorAspect {

    private static final DaffLogger log = DaffLogger.getLogger(MonitorAspect.class);

    //实现一个返回Java基本类型默认值的工具。其实，你也可以逐一写很多if-else判断类型，然后手动设置其默认值。这里为了减少代码量用了一个小技巧，即通过初始化一个具有1个元素的数组，然后通过获取这个数组的值来获取基本类型默认值
    private static final Map<Class<?>, Object> DEFAULT_VALUES = Stream.of(
            boolean.class, byte.class, char.class, double.class,
            float.class, int.class, long.class, short.class
    ).collect(toMap(clazz -> clazz, clazz -> Array.get(Array.newInstance(clazz, 1), 0)));

    @SuppressWarnings("unchecked")
    public static <T> T getDefaultValue(Class<T> clazz) {
        return (T) DEFAULT_VALUES.get(clazz);
    }

    //@annotation指示器实现对标记了Metrics注解的方法进行匹配
    @Pointcut("within(@io.daff.uacs.web.anno.Monitor *)")
    public void withMetricsAnnotation() {
    }

    //within指示器实现了匹配那些类型上标记了@RestController注解的方法
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerBean() {
    }

    @Around("controllerBean() || withMetricsAnnotation())")
    public Object metrics(ProceedingJoinPoint pjp) throws Throwable {

        Instant start = Instant.now();

        //通过连接点获取方法签名和方法上Metrics注解，并根据方法签名生成日志中要输出的方法定义描述
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String name = String.format("%s#%s => ", signature.getDeclaringType().getSimpleName(), signature.getMethod().getName());

        // 优先从方法上获取
        Monitor monitor = signature.getMethod().getAnnotation(Monitor.class);
        // 因为需要默认对所有@RestController标记的Web控制器实现@Metrics注解的功能，在这种情况下方法上必然是没有@Metrics注解的，我们需要获取一个默认注解。虽然可以手动实例化一个@Metrics注解的实例出来，但为了节省代码行数，我们通过在一个内部类上定义@Metrics注解方式，然后通过反射获取注解的小技巧，来获得一个默认的@Metrics注解的实例
        if (monitor == null) {
            // 如果方法上没有，再从类上获取
            monitor = signature.getMethod().getDeclaringClass().getAnnotation(Monitor.class);
        }
        if (monitor == null) {
            // 如果类上没有，则是有默认的
            @Monitor
            final class c {
            }
            monitor = c.class.getAnnotation(Monitor.class);
        }
        //尝试从请求上下文（如果有的话）获得请求URL，以方便定位问题
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            name += String.format("%s", request.getRequestURL().toString());
        }
        //实现的是入参的日志输出
        Object[] requestParams = filterOutPrintableDateType(pjp.getArgs());
        if (monitor.recordRequestParams()) {
            log.info("【接口::入参】【{}】：【{}】", BaseModule.MONITOR, name, JacksonUtil.beanToString(requestParams));
        }
        //实现连接点方法的执行，以及成功失败的打点，出现异常的时候还会记录日志
        Object returnValue;
        try {
            returnValue = pjp.proceed();
            if (monitor.recordExecuteSuccessTimeCost()) {
                //在生产级代码中，我们应考虑使用类似Micrometer的指标框架，把打点信息记录到时间序列数据库中，实现通过图表来查看方法的调用次数和执行时间，在设计篇我们会重点介绍
                log.info("【接口::耗时】【{}】调用成功：{} ms", BaseModule.MONITOR, name, Duration.between(start, Instant.now()).toMillis());
            }
            //实现了返回值的日志输出
            if (monitor.recordReturnValue()) {
                log.info("【接口::出参】【{}】：【{}】", BaseModule.MONITOR, name, JacksonUtil.beanToString(returnValue));
            }
        } catch (Exception e) {
            if (monitor.recordExecuteFailureTimeCost()) {
                log.info("【接口::耗时】【{}】调用失败：%d ms", BaseModule.MONITOR, name, Duration.between(start, Instant.now()).toMillis());
            }
            if (monitor.recordException()) {
                log.info("【接口::异常】【{}】，异常信息：", BaseModule.MONITOR, name);
            }

            //忽略异常的时候，使用一开始定义的getDefaultValue方法，来获取基本类型的默认值
            // if (monitor.ignoreException()) {
            //     returnValue = getDefaultValue(signature.getReturnType());
            // } else {
            //     throw e;
            // }
            throw e;
        } finally {
            IpRequestContext.remove();
        }
        return returnValue;
    }

    /**
     * 检查参数的类型是否是java简单的数据类型
     */
    private boolean isSimpleDataType(Object[] params) {
        if (params.length > 0) {
            for (Object param : params) {
                if (param instanceof String || param instanceof Boolean ||
                        param instanceof Byte || param instanceof Character ||
                        param instanceof Double || param instanceof Float ||
                        param instanceof Short || param instanceof Long ||
                        param instanceof Integer) {

                    return true;
                }
            }
        }
        return false;
    }

    private Object[] filterOutPrintableDateType(Object[] params) {
        return Arrays.stream(params).filter(this::isPrintableDataType).toArray();
    }

    private boolean isPrintableDataType(Object param) {
        return !(param instanceof InputStreamSource)
                && !(param instanceof ServletRequest)
                && !(param instanceof ServletResponse);
    }
}
