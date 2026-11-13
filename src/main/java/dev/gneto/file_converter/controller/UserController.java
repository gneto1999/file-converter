package dev.gneto.file_converter.controller;

import dev.gneto.file_converter.controller.docs.UserControllerDocs;
import dev.gneto.file_converter.dto.request.UpdateUserByAdminRequest;
import dev.gneto.file_converter.dto.request.UpdateUserRequest;
import dev.gneto.file_converter.dto.response.UserResponse;
import dev.gneto.file_converter.model.User;
import dev.gneto.file_converter.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserController implements UserControllerDocs {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "firstName",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable
    ) {
        int size = Math.min(pageable.getPageSize(), 50);

        Pageable limitedPageable = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());

        return ResponseEntity.ok().body(service.getAllUsers(limitedPageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/{id}")
    @Override
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(service.getUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id) {
        service.deleteUserById(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/{id}")
    @Override
    public ResponseEntity<UserResponse> updateByAdmin(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateUserByAdminRequest updatedUser
    ) {
        return ResponseEntity.ok().body(service.updateByAdmin(id, updatedUser));
    }

    @GetMapping(value = "/me")
    @Override
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User authUser) {
        return ResponseEntity.ok().body(service.getCurrentUser(authUser));
    }

    @PatchMapping(value = "/me")
    @Override
    public ResponseEntity<UserResponse> update(
            @AuthenticationPrincipal User authUser,
            @RequestBody @Valid UpdateUserRequest updatedUser
    ) {
        return ResponseEntity.ok().body(service.update(authUser, updatedUser));
    }
}
