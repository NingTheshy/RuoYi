package com.ruoyi.common.core.domain;

import lombok.Data;
import java.io.Serializable;

/**
 * 统一响应结果封装
 * <p>
 * 所有 REST API 的响应都使用此类包装，保证前端接收到一致的 JSON 结构：
 * </p>
 * <pre>
 * {
 *   "code": 200,
 *   "msg": "操作成功",
 *   "data": { ... }
 * }
 * </pre>
 *
 * <p>常用状态码：</p>
 * <ul>
 *   <li>200 - 操作成功</li>
 *   <li>400 - 请求参数错误</li>
 *   <li>401 - 未认证或认证已过期</li>
 *   <li>403 - 权限不足</li>
 *   <li>404 - 资源不存在</li>
 *   <li>405 - 不支持的请求方法</li>
 *   <li>500 - 系统内部错误</li>
 * </ul>
 *
 * @param <T> 响应数据的类型
 * @author NingTheshy
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private int code;

    /** 提示消息 */
    private String msg;

    /** 响应数据 */
    private T data;

    /**
     * 返回成功响应（无数据）
     */
    public static <T> R<T> ok() {
        return restResult(null, 200, "操作成功");
    }

    /**
     * 返回成功响应（带数据）
     *
     * @param data 响应数据
     */
    public static <T> R<T> ok(T data) {
        return restResult(data, 200, "操作成功");
    }

    /**
     * 返回成功响应（带数据和自定义消息）
     *
     * @param data 响应数据
     * @param msg  自定义提示消息
     */
    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, 200, msg);
    }

    /**
     * 返回失败响应（默认 500）
     */
    public static <T> R<T> fail() {
        return restResult(null, 500, "操作失败");
    }

    /**
     * 返回失败响应（自定义消息）
     *
     * @param msg 错误消息
     */
    public static <T> R<T> fail(String msg) {
        return restResult(null, 500, msg);
    }

    /**
     * 返回失败响应（自定义状态码和消息）
     *
     * @param code 状态码
     * @param msg  错误消息
     */
    public static <T> R<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }

    /**
     * 内部构建响应对象
     */
    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    /**
     * 判断请求是否成功
     *
     * @return true 表示成功（code=200）
     */
    public boolean isSuccess() {
        return this.code == 200;
    }
}
