package dev.gneto.file_converter.controller.docs;

import dev.gneto.file_converter.dto.request.AuthUserRequest;
import dev.gneto.file_converter.dto.request.UserRegisterRequest;
import dev.gneto.file_converter.dto.response.AuthUserResponse;
import dev.gneto.file_converter.dto.response.UserRegisteredResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "Authentication Endpoints")
public interface AuthControllerDocs {

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns the user's ID, name, email address, and role.",
            tags = {"Authentication Endpoints"},
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201",
                            content = @Content(schema = @Schema(implementation = UserRegisteredResponse.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Unprocessable Content", responseCode = "422", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<UserRegisteredResponse> signup(@Valid UserRegisterRequest request);

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user and returns a JWT access token.",
            tags = {"Authentication Endpoints"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = AuthUserResponse.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Unprocessable Content", responseCode = "422", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<AuthUserResponse> login(@Valid AuthUserRequest request);

    @Operation(
            summary = "Refresh access token",
            description = "Refreshes the access token for an authenticated user and returns a new JWT.",
            tags = {"Authentication Endpoints"},
            parameters = {
                    @Parameter(
                            name = "X-Refresh-Token",
                            description = "Refresh token (Bearer token)",
                            required = true,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string")
                    )

            },
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = AuthUserResponse.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<AuthUserResponse> refreshToken(@NotEmpty String authorizationHeader);

    @Operation(
            summary = "Log out user",
            description = "Logs out the user by revoking the refresh token.",
            tags = {"Authentication Endpoints"},
            parameters = {
                    @Parameter(
                            name = "X-Refresh-Token",
                            description = "Refresh token (Bearer token)",
                            required = true,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string")
                    )

            },
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Unprocessable Content", responseCode = "422", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Void> logout(@NotEmpty String authorizationHeader);

    @Operation(
            summary = "Log out all user sessions",
            description = "Logs out the user from all active sessions by revoking all associated refresh tokens.",
            tags = {"Authentication Endpoints"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Unprocessable Content", responseCode = "422", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Void> logoutAll(Authentication authentication);
}
