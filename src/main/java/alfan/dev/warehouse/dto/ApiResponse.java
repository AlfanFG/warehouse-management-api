package alfan.dev.warehouse.dto;

public record ApiResponse<T>(
        int code,
        String status,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, "success", message, data);
    }
}
