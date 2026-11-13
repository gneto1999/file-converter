package dev.gneto.file_converter.services;

import dev.gneto.file_converter.dto.request.UpdateUserByAdminRequest;
import dev.gneto.file_converter.dto.request.UpdateUserRequest;
import dev.gneto.file_converter.dto.response.UserResponse;
import dev.gneto.file_converter.exception.EmailAlreadyExistsException;
import dev.gneto.file_converter.exception.UserNotFoundException;
import dev.gneto.file_converter.mapper.UserMapper;
import dev.gneto.file_converter.model.User;
import dev.gneto.file_converter.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    public UserService(UserRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(mapper::userToUserResponse);
    }

    public UserResponse getUserById(UUID id) {
        var user = findUserByPublicId(id);

        return mapper.userToUserResponse(user);
    }

    public void deleteUserById(UUID id) {
        var user = findUserByPublicId(id);

        userRepository.delete(user);
    }

    public UserResponse getCurrentUser(User user) {
        return mapper.userToUserResponse(user);
    }

    @Transactional
    public UserResponse update(User authUser, UpdateUserRequest updatedUser) {
        if (updatedUser.firstName() != null && updatedUser.firstName().isPresent()) {
            authUser.setFirstName(updatedUser.firstName().get());
        }

        if (updatedUser.lastName() != null && updatedUser.lastName().isPresent()) {
            authUser.setLastName(updatedUser.lastName().get());
        }

        return mapper.userToUserResponse(userRepository.save(authUser));
    }

    @Transactional
    public UserResponse updateByAdmin(UUID id, @Valid UpdateUserByAdminRequest updatedUser) {
        var user = findUserByPublicId(id);

        if (updatedUser.firstName() != null && updatedUser.firstName().isPresent()) {
            user.setFirstName(updatedUser.firstName().get());
        }

        if (updatedUser.lastName() != null && updatedUser.lastName().isPresent()) {
            user.setLastName(updatedUser.lastName().get());
        }

        if (updatedUser.email() != null && updatedUser.email().isPresent()) {
            if (userRepository.findUserByEmail(updatedUser.email().get()).isPresent()) {
                throw new EmailAlreadyExistsException("Email already in use.");
            }

            user.setEmail(updatedUser.email().get());
        }

        return mapper.userToUserResponse(userRepository.save(user));
    }

    private User findUserByPublicId(UUID id) {
        return userRepository.findByPublicId(id)
                .orElseThrow(() -> new UserNotFoundException("User not found for ID: " + id));
    }
}
