package com.aiplus.backend.users.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aiplus.backend.users.dto.AccountDto;
import com.aiplus.backend.users.dto.AccountUpdateRequest;
import com.aiplus.backend.users.mapper.AccountMapper;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.users.service.AccountService;
import com.aiplus.backend.utils.responses.ApiResponse;
import com.aiplus.backend.utils.responses.ResponseUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    /**
     * Updates the authenticated user's account based on the request payload.
     *
     * @param user    the authenticated User principal
     * @param request the polymorphic update request
     * @return HTTP 200 with the updated account DTO
     */
    @PutMapping
    public ResponseEntity<ApiResponse<AccountDto>> updateAccount(@AuthenticationPrincipal User user,
            @RequestBody AccountUpdateRequest request) {

        AccountDto dto = accountMapper.toAccountDto(accountService.updateAccount(user, request));
        return ResponseEntity.ok(ResponseUtil.success("Account Updated", dto, null));
    }

    /**
     * Retrieves the authenticated user's account details.
     *
     * @param user the authenticated User principal
     * @return HTTP 200 with the account DTO
     */
    @GetMapping
    public ResponseEntity<ApiResponse<AccountDto>> getAccount(@AuthenticationPrincipal User user) {

        AccountDto dto = accountMapper.toAccountDto(accountService.findByUser(user));
        return ResponseEntity.ok(ResponseUtil.success("Account Found", dto, null));
    }

}
