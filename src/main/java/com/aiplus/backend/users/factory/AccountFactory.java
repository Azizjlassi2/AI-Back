package com.aiplus.backend.users.factory;

import org.springframework.stereotype.Component;

import com.aiplus.backend.users.model.Account;
import com.aiplus.backend.users.model.AdminAccount;
import com.aiplus.backend.users.model.ClientAccount;
import com.aiplus.backend.users.model.DeveloperAccount;
import com.aiplus.backend.users.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AccountFactory {

    public Account createAccountForUser(User user) {
        log.info("Creating account for user: {} with role: {}", user.getUsername(), user.getRole().name());
        switch (user.getRole().name()) {
        case "DEVELOPER" -> {
            DeveloperAccount developerAccount = new DeveloperAccount();
            developerAccount.setUser(user);
            log.info("Created DeveloperAccount for user: {}", user.getUsername());
            return developerAccount;
        }
        case "ADMIN" -> {
            AdminAccount adminAccount = new AdminAccount();
            adminAccount.setUser(user);
            log.info("Created AdminAccount for user: {}", user.getUsername());
            return adminAccount;
        }
        case "CLIENT" -> {
            ClientAccount clientAccount = new ClientAccount();
            clientAccount.setUser(user);
            log.info("Created ClientAccount for user: {}", user.getUsername());
            return clientAccount;
        }

        default -> throw new IllegalArgumentException("Unknown role : " + user.getRole());

        }

    }

}
