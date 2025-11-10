package com.crm.common.aop;

import cn.hutool.core.thread.threadlocal.NamedThreadLocal;
import com.alibaba.fastjson2.JSON;
import com.aliyun.core.http.HttpMethod;
import com.crm.common.filter.PropertyPreExcludeFilter;
import com.crm.entity.OperLog;
import com.crm.enums.BusinessStatus;
import com.crm.security.user.ManagerDetail;
import com.crm.security.user.SecurityUser;
import com.crm.service.OperLogService;
import com.crm.utils.IpUtils;
import com.crm.utils.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import java.util.Collection;
import java.util.Map;

@Aspect
@Component
@AllArgsConstructor
public class LogAspect {
    // 1、需要过滤的敏感参数字段名
    public static final String[] EXCLUDE_PROPERTIES = {"password", "oldPassword"};
    // 2、记录代码执行的过程日志
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class.getName());
    // 3、记录操作消耗时间
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new NamedThreadLocal<>("Cost Time");

    private final OperLogService operLogService;

    @Before(value = "@annotation(controllerLog)")
    public void doBefore(JoinPoint joinPoint, Log controllerLog) {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    // 处理异常情况
    @AfterThrowing(pointcut = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }


    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult) {
        try {
            ManagerDetail loginManager = SecurityUser.getManager();
            OperLog operLog = new OperLog();
            operLog.setStatus(BusinessStatus.SUCCESS.ordinal());
            String ip = IpUtils.getIpAddr();
            operLog.setOperIp(ip);
            operLog.setOperUrl(StringUtils.substring(ServletUtils.getRequest().getRequestURI(), 0, 255));
            if (loginManager != null) {
                operLog.setOperName(loginManager.getAccount());
                operLog.setManagerId(loginManager.getId().toString());
            }
            if (e != null) {
                operLog.setStatus(BusinessStatus.FAIL.ordinal());
                operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            operLog.setRequestMethod(ServletUtils.getRequest().getMethod());
            getControllerMethodDescription(joinPoint, controllerLog, operLog, jsonResult);
            operLog.setCostTime(System.currentTimeMillis() - TIME_THREADLOCAL.get());
            operLogService.recordOperLog(operLog);
        } catch (Exception exp) {
            log.error("日志记录异常:{}", exp.getMessage());
        } finally {
            TIME_THREADLOCAL.remove();
        }
    }
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, OperLog openLog, Object jsonResult) throws Exception {
        // 设置模块名称
        openLog.setTitle(log.title());
        // 请求操作类型
        openLog.setOperType(log.businessType().ordinal());
        // 是否需要保存request 请求参数
        if(log.isSaveRequestData())
            setRequestValue(joinPoint, openLog, log.excludeParamNames());

        // 是否需要保存response结果
        if(log.isSaveResponseData())
            openLog.setJsonResult(StringUtils.substring(JSON.toJSONString(jsonResult, excludePropertyPreFilter(EXCLUDE_PROPERTIES)), 0, 2000));
    }

    private void setRequestValue(JoinPoint joinPoint, OperLog operLog, String[] excludeParamNames) throws Exception {
        Map<?, ?> paramsMap = ServletUtils.getParamMap(ServletUtils.getRequest());
        String requestMethod = operLog.getRequestMethod();
        if (paramsMap.isEmpty()
                && (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod))) {
            String params = argsArrayToString(joinPoint.getArgs(), excludeParamNames);
            operLog.setOperParam(StringUtils.substring(params, 0, 2000));
        } else {
            operLog.setOperParam(StringUtils.substring(JSON.toJSONString(paramsMap, excludePropertyPreFilter(excludeParamNames)), 0, 2000));
        }
    }

    public PropertyPreExcludeFilter excludePropertyPreFilter(String[] excludeProperties){
        return new PropertyPreExcludeFilter().addExcludes(ArrayUtils.addAll(EXCLUDE_PROPERTIES,excludeProperties));
    }

    private String argsArrayToString(Object[] paramsArray, String[] excludeParamNames) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                if (ObjectUtils.isNotEmpty(o) && !isFilterObject(o)) {
                    try {
                        String jsonObj = JSON.toJSONString(o, excludePropertyPreFilter(excludeParamNames));
                        params.append(jsonObj).append(" ");
                    } catch (Exception e) {
                    }
                }
            }
        }
        return params.toString().trim();
    }
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        // 检查是否是数组
        if (clazz.isArray()) {
            // 判断数组的元素类型是否是MultipartFile或其子类
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            // 只要集合中有一个元素是MultipartFile，就整个过滤掉
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            // 只要Map中有一个值是MultipartFile，就整个过滤掉
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }

}
