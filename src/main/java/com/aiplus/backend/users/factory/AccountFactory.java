package com.aiplus.backend.users.factory;

import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.aiplus.backend.users.model.Account;
import com.aiplus.backend.users.model.AdminAccount;
import com.aiplus.backend.users.model.ClientAccount;
import com.aiplus.backend.users.model.DeveloperAccount;
import com.aiplus.backend.users.model.User;

@Component
public class AccountFactory {
    private static final Logger logger = Logger.getLogger(AccountFactory.class.getName());

    public Account createAccountForUser(User user) {

        switch (user.getRole().name()) {
            case "DEVELOPER" -> {
                DeveloperAccount developerAccount = new DeveloperAccount();
                developerAccount.setUser(user);
                return developerAccount;
            }
            case "ADMIN" -> {
                AdminAccount adminAccount = new AdminAccount();
                adminAccount.setUser(user);
                return adminAccount;
            }
            case "CLIENT" -> {
                ClientAccount clientAccount = new ClientAccount();
                clientAccount.setUser(user);
                return clientAccount;
            }

            default -> throw new IllegalArgumentException("Unknown role : " + user.getRole());

        }

    }

}
