package com.datn.electronic_voting.controller.admin;

import com.datn.electronic_voting.dto.UserDTO;
import com.datn.electronic_voting.dto.request.ChangePasswordRequest;
import com.datn.electronic_voting.dto.request.ResetPasswordRequest;
import com.datn.electronic_voting.dto.response.ApiResponse;
import com.datn.electronic_voting.dto.response.PaginatedResponse;
import com.datn.electronic_voting.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserController {

    final private UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserDTO> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping(value = "/paginated")
    @PreAuthorize("hasAuthority('ADMIN')")
    public PaginatedResponse<UserDTO> getUserList(@RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page-1,size);
        return PaginatedResponse.<UserDTO>builder()
                .listElements(userService.getUsersPageable(pageable))
                .totalPages((int) Math.ceil( (double) (userService.totalItem())/size))
                .build();
    }
    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<UserDTO> getUsersPaginated(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role
    ) {
        return userService.searchUsersPaginated(page, size, fullName, username, email, role);
    }

    @GetMapping("/election/{electionId}")
    public List<UserDTO> getUserInElection(@PathVariable Long electionId){
        return userService.findUserInElection(electionId);
    }

    @GetMapping("/not-in-election/{electionId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserDTO> getUsersNotInElection(@PathVariable Long electionId){
        return userService.findUserNotInElection(electionId);
    }
    @GetMapping("/{id}")
    @PostAuthorize("returnObject.username==authentication.name or hasAuthority('ADMIN')")
    public UserDTO getUserById(@PathVariable Long id){
        return userService.findUserById(id);
    }

    @GetMapping("/find-user/{username}")
    public UserDTO getUserByUsername(@PathVariable String username){
        return userService.findUserByUsername(username);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserDTO createUser(@Valid @RequestBody UserDTO user){
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    @PostAuthorize("returnObject.username==authentication.name or hasAuthority('ADMIN')")
    public UserDTO updateUser(@Valid @RequestBody UserDTO user, @PathVariable Long id){
        return userService.updateUser(user,id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok().body("Xóa thành công");
    }

    @PatchMapping("/upload-image/{userId}")
    @PostAuthorize("returnObject.result.username==authentication.name or hasAuthority('ADMIN')")
    public ApiResponse<UserDTO> uploadImage(@PathVariable Long userId, @RequestParam("image") MultipartFile image) throws IOException {
        return ApiResponse.<UserDTO>builder()
                .code(200)
                .message("Cập nhật ảnh thành công")
                .result(userService.updateImage(userId,image))
                .build();
    }
    @PatchMapping("/reset-password")
    public ApiResponse resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ApiResponse.builder()
                .code(200)
                .message("Mật khẩu mới đã được gửi vào email của bạn. Hãy đăng nhập bằng mật khẩu mới!")
                .build();
    }
    @PutMapping("/change-password")
    @PostAuthorize("returnObject.result.username==authentication.name or hasAuthority('ADMIN')")
    public ApiResponse changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.builder()
                .code(200)
                .message("Thay đổi mật khẩu thành công")
                .result(userService.findUserByUsername(request.getUsername()))
                .build();
    }

}
