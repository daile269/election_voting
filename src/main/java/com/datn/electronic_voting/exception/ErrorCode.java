package com.datn.electronic_voting.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    TIME_ERROR(400," Thời gian kết thúc cuộc phải sau thời gian bắt đầu"),
    START_TIME_ERROR(400," Thời gian bắt đầu cuộc bầu cử k hợp lệ"),
    USER_IS_EXISTS(400,"Người dùng đã tồn tại"),
    USER_IS_NOT_EXISTS(400,"Người dùng không tồn tại"),
    USERNAME_OR_PASSWORD_VALID(969,"Username or password is valid!"),
    ELECTION_NOT_FOUND(400,"Cuộc bỏ phiếu không tồn tại"),
    CANDIDATE_NOT_FOUND(400,"Ứng viên k tồn tại"),
    RESULT_NOT_FOUND(400,"Không có kết quả của cuộc bỏ phiếu"),
    VOTE_NOT_FOUND(400," Không tìm thấy phiếu bầu"),
    VOTELIST_NULL(400," Chưa có phiếu bầu cho cuộc bầu cử này"),
    EMAIL_IS_EXISTS(400,"Email already exists"),
    ELECTION_CODE_VALID(400," Mã cuộc bỏ phiếu không chính xác"),
    VOTE_IS_EXITS(400,"User đã bầu cho ứng viên này rồi, vui lòng k bầu lại"),
    VERIFY_CODE_VALID(400,"Mã xác nhận không chính xác, vui lòng kiểm tra lại trong email!!"),
    USER_IS_ACTIVE(400,"Người dùng đã xác thực"),
    LOGIN_VALID(400,"Tên đăng nhập hoặc mật khẩu không chính xác!"),
    UNAUTHENTICATED(403,"Không có quyền truy cập" );

    private final int code;
    private final String message;
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
