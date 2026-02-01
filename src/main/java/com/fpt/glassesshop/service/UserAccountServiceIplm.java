package com.fpt.glassesshop.service;

import com.fpt.glassesshop.model.dto.UserAccountDto;
import com.fpt.glassesshop.entity.UserAccount;
import com.fpt.glassesshop.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserAccountServiceIplm implements UserAccountService {

    private final UserAccountRepository userAccountRepo;

    @Override
    public List<UserAccountDto> getAllUserAccount() {
        return userAccountRepo.findAll()
                                .stream()
                                .map(this::mapUserAccountToDto)
                                .toList();
        }

    @Override
    public UserAccountDto getUserAccountById(Long userId) {
        UserAccount userAccount = userAccountRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return  mapUserAccountToDto(userAccount);
    }

    @Override
    public UserAccount createUserAccount(UserAccount req) throws BadRequestException {
        if(userAccountRepo.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already exists: " + req.getEmail());
        }
        UserAccount u = new UserAccount();
        u.setName(req.getName());
        u.setEmail(req.getEmail());
        u.setPhone(req.getPhone());
        u.setAccountStatus(req.getAccountStatus());
        u.setPasswordHash(req.getPasswordHash());
        u.setRole(req.getRole());
        return userAccountRepo.save(u);
    }

    @Override
    public UserAccountDto updateUserAccount(UserAccount req, Long userId) {

        UserAccount u = userAccountRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (req.getName() != null) u.setName(req.getName());
        if (req.getPhone() != null) u.setPhone(req.getPhone());
        if (req.getAccountStatus() != null) u.setAccountStatus(req.getAccountStatus());
        if (req.getRole() != null) u.setRole(req.getRole());

        UserAccount saved = userAccountRepo.save(u);

        return mapUserAccountToDto(saved); // DTO trả về
    }


    @Override
    public void deleteUserAccount(Long userId) {
        userAccountRepo.deleteById(userId);
    }

    public UserAccountDto mapUserAccountToDto(UserAccount userAccount) {
        return UserAccountDto.builder()
                .userId(userAccount.getUserId())
                .name(userAccount.getName())
                .phone(userAccount.getPhone())
                .role(userAccount.getRole())
                .accountStatus(userAccount.getAccountStatus())
                .createdAt(userAccount.getCreatedAt()).build();
    }

}

