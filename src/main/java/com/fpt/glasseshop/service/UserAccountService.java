package com.fpt.glasseshop.service;

import com.fpt.glasseshop.entity.UserAccount;
import com.fpt.glasseshop.entity.dto.*;
import com.fpt.glasseshop.exception.ResourceNotFoundException;
import com.fpt.glasseshop.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public List<UserAccountDTO> getAllUsersDTO() {
        return userAccountRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserAccountDTO> getUserDTOById(Long id) {
        return userAccountRepository.findById(id).map(this::convertToDTO);
    }

    public UserAccountDTO createUser(UserAccountDTO dto) {
        UserAccount user = UserAccount.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .role(dto.getRole())
                .passwordHash(dto.getPassword() != null ? passwordEncoder.encode(dto.getPassword()) : null)
                .accountStatus(dto.getAccountStatus() != null ? dto.getAccountStatus() : "ACTIVE")
                .build();
        UserAccount saved = userAccountRepository.save(user);
        return convertToDTO(saved);
    }

    public void deleteUser(Long id) {
        if (!userAccountRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userAccountRepository.deleteById(id);
    }

    private UserAccountDTO convertToDTO(UserAccount user) {
        return UserAccountDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .accountStatus(user.getAccountStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public UserResponseDTO getUser(String email) {
        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return toDTO(user);
    }

    @Transactional
    public UserResponseDTO updateMyProfile(String email, UpdateProfileRequest req) {
        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        user.setName(req.getName());
        user.setPhone(req.getPhone());

        UserAccount saved = userAccountRepository.save(user);
        return toDTO(saved);
    }

    @Transactional
    public UserResponseDTO updateMyEmail(String email, UpdateEmailRequest req) {
        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // confirm password
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Password confirmation is incorrect");
        }

        String newEmail = req.getNewEmail().trim().toLowerCase();

        // email unique
        if (!newEmail.equalsIgnoreCase(user.getEmail()) && userAccountRepository.existsByEmail(newEmail)) {
            throw new RuntimeException("Email already exists: " + newEmail);
        }

        user.setEmail(newEmail);
        UserAccount saved = userAccountRepository.save(user);

        return toDTO(saved);
    }

    @Transactional
    public void changeMyPassword(String email, ChangePasswordRequest req) {
        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        userAccountRepository.save(user);
    }

    private UserResponseDTO toDTO(UserAccount user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .accountStatus(user.getAccountStatus())
                .build();
    }
}
