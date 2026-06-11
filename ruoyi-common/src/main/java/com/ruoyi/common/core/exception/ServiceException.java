package com.ruoyi.common.core.exception;

/**
 * 业务逻辑异常
 * <p>
 * 当业务规则校验失败时抛出此异常，由 {@link GlobalExceptionHandler} 捕获并返回统一格式响应。
 * 例如：用户名已存在、不允许删除超级管理员、密码错误等。
 * </p>
 *
 * <p>使用示例：</p>
 * <pre>
 * throw new ServiceException("用户名已存在");
 * throw new ServiceException(401, "密码错误");
 * </pre>
 *
 * @author NingTheshy
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 错误码，默认 500 */
    private final int code;

    /** 错误消息 */
    private final String message;

    /**
     * 构造业务异常（默认错误码 500）
     *
     * @param message 错误消息
     */
    public ServiceException(String message) {
        this.code = 500;
        this.message = message;
    }

    /**
     * 构造业务异常（自定义错误码）
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public ServiceException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
