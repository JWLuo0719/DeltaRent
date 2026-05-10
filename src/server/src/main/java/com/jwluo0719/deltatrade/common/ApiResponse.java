package com.jwluo0719.deltatrade.common;


//通用接口返回结果封装
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<T>(true, message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(true, "ok", data);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<T>(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
//返回必要数据
// {
//   "success": true/false,
//   "message": "提示信息",
//   "data": 业务数据
// }