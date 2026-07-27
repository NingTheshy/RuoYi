package com.ruoyi.common.core.exception;

import com.ruoyi.common.core.domain.R;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 使用 {@code @RestControllerAdvice} 拦截所有 Controller 层抛出的异常，
 * 将异常转换为统一的 {@link R} 响应格式返回给前端。
 * </p>
 *
 * <p>异常处理优先级（从精确到宽泛）：</p>
 * <ol>
 *   <li>ServiceException - 业务异常（400/401/500 等自定义码）</li>
 *   <li>MethodArgumentNotValidException - Bean Validation 参数校验失败（400）</li>
 *   <li>AuthenticationException - Spring Security 认证失败（401）</li>
 *   <li>AccessDeniedException - Spring Security 授权失败（403）</li>
 *   <li>HttpRequestMethodNotSupportedException - HTTP 方法不支持（405）</li>
 *   <li>HttpMessageNotReadableException - 请求体 JSON 解析失败（400）</li>
 *   <li>NoHandlerFoundException / NoResourceFoundException - 资源不存在（404）</li>
 *   <li>Exception - 兜底异常（500）</li>
 * </ol>
 *
 * @author NingTheshy
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务逻辑异常
     * <p>由 Service 层通过 {@code throw new ServiceException(...)} 抛出</p>
     *
     * @param e 业务异常
     * @return 包含错误码和消息的响应
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<R<Void>> handleServiceException(ServiceException e) {
        log.warn("[异常] 业务异常: code={}, msg={}", e.getCode(), e.getMessage());
        return buildResponse(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid 注解触发）
     * <p>将所有字段错误拼接为一条消息返回，格式："field1: message1; field2: message2"</p>
     *
     * @param e 参数校验异常
     * @return 400 状态码响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<R<Void>> handleValidException(MethodArgumentNotValidException e) {
        String message = buildFieldErrorMessage(e.getBindingResult().getFieldErrors().stream());
        log.warn("[异常] 参数校验失败: {}", message);
        return buildResponse(400, message);
    }

    /**
     * 处理表单/查询参数绑定校验异常（GET 查询对象等场景）。
     *
     * @param e 绑定异常
     * @return 400 状态码响应
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<R<Void>> handleBindException(BindException e) {
        String message = buildFieldErrorMessage(e.getBindingResult().getFieldErrors().stream());
        log.warn("[异常] 绑定参数校验失败: {}", message);
        return buildResponse(400, message);
    }

    /**
     * 处理方法级参数校验异常（Spring 6+）。
     *
     * @param e 方法参数校验异常
     * @return 400 状态码响应
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<R<Void>> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        String message = e.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("[异常] 方法参数校验失败: {}", message);
        return buildResponse(400, message);
    }

    /**
     * 处理约束校验异常（如手动触发 validator 或方法参数校验）。
     *
     * @param e 约束校验异常
     * @return 400 状态码响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<R<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(this::buildConstraintViolationMessage)
                .collect(Collectors.joining("; "));
        log.warn("[异常] 约束校验失败: {}", message);
        return buildResponse(400, message);
    }

    /**
     * 处理认证异常（401）
     * <p>JWT Token 无效、过期或缺失时触发</p>
     *
     * @param e 认证异常
     * @return 401 状态码响应
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<R<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("[异常] 认证失败: {}", e.getMessage());
        return buildResponse(401, "未登录或Token已过期");
    }

    /**
     * 处理授权异常（403）
     * <p>用户已认证但没有访问资源所需的权限时触发</p>
     *
     * @param e 授权异常
     * @return 403 状态码响应
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<R<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("[异常] 权限不足: {}", e.getMessage());
        return buildResponse(403, "权限不足，无法访问");
    }

    /**
     * 处理 HTTP 请求方法不支持异常（405）
     * <p>例如用 GET 访问只支持 POST 的接口</p>
     *
     * @param e 请求方法不支持异常
     * @return 405 状态码响应
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<R<Void>> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("不支持的请求方法: {}", e.getMethod());
        return buildResponse(405, "不支持的请求方法: " + e.getMethod());
    }

    /**
     * 处理请求体解析异常（400）
     * <p>请求体不是合法 JSON 或字段类型不匹配时触发</p>
     *
     * @param e 消息不可读异常
     * @return 400 状态码响应
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<R<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败: {}", e.getMessage());
        return buildResponse(400, "请求体格式错误或 JSON 解析失败");
    }

    /**
     * 处理数据库约束冲突异常（400）
     *
     * @param e 唯一键或完整性约束异常
     * @return 400 状态码响应
     */
    @ExceptionHandler({DuplicateKeyException.class, DataIntegrityViolationException.class})
    public ResponseEntity<R<Void>> handleDataConflictException(Exception e) {
        log.warn("数据库约束冲突: {}", e.getMessage());
        return buildResponse(400, "数据已存在或违反约束条件");
    }

    /**
     * 处理资源不存在异常（404）
     *
     * @param e 资源不存在异常
     * @return 404 状态码响应
     */
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<R<Void>> handleNotFoundException(Exception e) {
        log.warn("资源不存在: {}", e.getMessage());
        return buildResponse(404, "请求的资源不存在");
    }

    /**
     * 兜底异常处理（500）
     * <p>捕获所有未被上述处理器处理的异常，记录完整堆栈日志</p>
     *
     * @param e 未知异常
     * @return 500 状态码响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<R<Void>> handleException(Exception e) {
        log.error("[异常] 系统异常: type={}, msg={}", e.getClass().getSimpleName(), e.getMessage(), e);
        return buildResponse(500, "系统内部错误");
    }

    private ResponseEntity<R<Void>> buildResponse(int code, String message) {
        HttpStatus httpStatus = HttpStatus.resolve(code);
        if (httpStatus == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(httpStatus).body(R.fail(code, message));
    }

    private String buildFieldErrorMessage(java.util.stream.Stream<FieldError> fieldErrors) {
        return fieldErrors
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
    }

    private String buildConstraintViolationMessage(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath() != null
                ? violation.getPropertyPath().toString()
                : "parameter";
        return propertyPath + ": " + violation.getMessage();
    }
}
