package io.daff.uacs.service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.daff.logging.DaffLogger;
import io.daff.uacs.core.enums.BaseModule;
import io.daff.web.entity.Response;
import io.daff.web.enums.Hint;
import io.daff.web.exception.BaseException;
import io.daff.web.exception.BusinessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolationException;

/**
 * 全局异常处理
 *
 * @author daff
 * @since 2020/7/12
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DaffLogger log = DaffLogger.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Response<Void> error(HttpMessageNotReadableException e) {
        log.info("参数格式有误", BaseModule.ERROR, e);
        return Response.error(Hint.SYSTEM_ERROR, "参数格式有误");
    }

    @ExceptionHandler(JsonProcessingException.class)
    public Response<Void> error(JsonProcessingException e) {
        log.error("jackson序列化错误", BaseModule.ERROR, e);
        return Response.error(Hint.SYSTEM_ERROR);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response<Void> error(HttpRequestMethodNotSupportedException e) {
        log.info(e.getMessage(), BaseModule.ERROR, e);
        return Response.error(Hint.SYSTEM_ERROR, "请求方法错误，请确认后重试");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Response<Void> error(MaxUploadSizeExceededException e) {
        log.info(e.getMessage(), BaseModule.ERROR, e);
        return Response.error(Hint.SYSTEM_ERROR, "上传的文件过大，请压缩或降低画质后上传");
    }

    @ExceptionHandler(BindException.class)
    public Response<Void> error(BindException e) {
        log.info(e.getMessage(), BaseModule.ERROR, e);
        return Response.error(Hint.PARAM_VALIDATE_ERROR, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public Response<Void> error(BusinessException e) {
        log.info(e.getMessage(), BaseModule.ERROR, e);
        return Response.error(Hint.SYSTEM_ERROR, e.getMessage());
    }

    @ExceptionHandler(BaseException.class)
    public Response<Void> error(BaseException e) {
        log.info(e.getMessage(), BaseModule.ERROR, e);
        return Response.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Response<Void> error(Exception e) {
        log.error(e.getMessage(), BaseModule.ERROR, e);
        return Response.error(Hint.SYSTEM_ERROR, "系统开小差了！");
    }

    /**
     * validator参数校验失败的异常处理器
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Void> error(MethodArgumentNotValidException e) {
        log.info(e.getMessage(), BaseModule.ERROR, e);
        return Response.error(Hint.PARAM_VALIDATE_ERROR, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Response<Void> error(ConstraintViolationException e) {
        log.info(e.getMessage(), BaseModule.ERROR, e);
        return Response.error(Hint.PARAM_VALIDATE_ERROR, e.getConstraintViolations().iterator().next().getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Response<Void> error(MissingServletRequestParameterException e) {
        log.info(e.getMessage(), BaseModule.ERROR, e);
        return Response.error(Hint.PARAM_MISS_ERROR, "请求参数缺失：" + e.getParameterName());
    }
}
