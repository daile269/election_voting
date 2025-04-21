package com.datn.electronic_voting.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    TIME_ERROR(903," Thời gian kết thúc cuộc phải sau thời gian bắt đầu"),
    START_TIME_ERROR(903," Thời gian bắt đầu cuộc bầu cử k hợp lệ"),
    USER_IS_EXISTS(999,"Người dùng đã tồn tại"),
    USER_IS_NOT_EXISTS(999,"Người dùng không tồn tại"),
    USERNAME_OR_PASSWORD_VALID(969,"Username or password is valid!"),
    ELECTION_NOT_FOUND(999,"Cuộc bỏ phiếu không tồn tại"),
    CANDIDATE_NOT_FOUND(999,"Ứng viên k tồn tại"),
    RESULT_NOT_FOUND(999,"Không có kết quả của cuộc bỏ phiếu"),
    VOTE_NOT_FOUND(999," Không tìm thấy phiếu bầu"),
    VOTELIST_NULL(999," Chưa có phiếu bầu cho cuộc bầu cử này"),

    ELECTION_CODE_VALID(999," Mã cuộc bỏ phiếu không chính xác"),
    UNAUTHENTICATED(777,"Không có quyền truy cập" );

    private final int code;
    private final String message;
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
