package com.aiplus.backend.auth.service;

import com.aiplus.backend.auth.model.RefreshToken;
import com.aiplus.backend.users.model.User;

public interface RefreshTokenService {

    public RefreshToken createForUser(User user);

    public RefreshToken verify(String token);

    public void deleteForUser(User user);

}
