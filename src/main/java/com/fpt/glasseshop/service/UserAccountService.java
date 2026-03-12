package com.fpt.glasseshop.service;

import com.fpt.glasseshop.entity.UserAccount;
import com.fpt.glasseshop.entity.dto.UserAccountDTO;
import com.fpt.glasseshop.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    public boolean checkEmailExists(String email) {
        System.out.println("Checking email: " + email);
        boolean exists = userAccountRepository.existsByEmail(email);
        System.out.println("Exists? " + exists);
        return exists;
    }
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
    public boolean authenticate(String email, String password) {
        Optional<UserAccount> user = userAccountRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPasswordHash())) {
            return true;
        }
        return false;
    }
    private UserAccountDTO convertToDTO(UserAccount user) {
        return UserAccountDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .password(user.getPasswordHash())
                .role(user.getRole())
                .accountStatus(user.getAccountStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
