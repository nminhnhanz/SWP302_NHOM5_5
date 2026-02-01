package com.fpt.glassesshop.service;

import com.fpt.glassesshop.model.dto.UserAccountDto;
import com.fpt.glassesshop.entity.UserAccount;
import org.apache.coyote.BadRequestException;


import java.util.List;

public interface UserAccountService {
     List<UserAccountDto> getAllUserAccount();
     UserAccountDto getUserAccountById(Long userId);
     UserAccount createUserAccount(UserAccount req) throws BadRequestException;
    UserAccountDto updateUserAccount(UserAccount req, Long userId);
    void deleteUserAccount(Long userId);

}
