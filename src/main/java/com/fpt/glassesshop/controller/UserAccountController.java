package com.fpt.glassesshop.controller;

import com.fpt.glassesshop.entity.UserAccount;
import com.fpt.glassesshop.model.dto.UserAccountDto;
import com.fpt.glassesshop.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/user-accounts")
public class UserAccountController {
    private final UserAccountService userAccountService;
    //GET ALL
    @GetMapping
    public List<UserAccountDto> getAllUserAccount(){
        return userAccountService.getAllUserAccount();
    }
    //GET BY ID
    @GetMapping("/{id}")
    public UserAccountDto getUserAccountById(@PathVariable long id){
            return userAccountService.getUserAccountById(id);

    }
    //CREATE USER
    @PostMapping
    public UserAccount createUserAccount(@RequestBody UserAccount  userAccount) throws BadRequestException {
            return userAccountService.createUserAccount(userAccount);
        
    }
    //UPDATE USER
    @PutMapping("/{id}")
    public UserAccountDto updateUserAccount(@RequestBody UserAccount userAccount, @PathVariable("id") Long id){
        return userAccountService.updateUserAccount(userAccount, id);
    }
    //DELETE USER
    @DeleteMapping("/{id}")
    public void deleteUserAccount(@PathVariable("id") Long userId){
        userAccountService.deleteUserAccount(userId);
    }


}

