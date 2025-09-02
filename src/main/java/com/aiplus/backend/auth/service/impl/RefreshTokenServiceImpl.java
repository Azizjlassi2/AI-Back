package com.aiplus.backend.auth.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.aiplus.backend.auth.exceptions.ExpiredTokenException;
import com.aiplus.backend.auth.model.RefreshToken;
import com.aiplus.backend.auth.repository.RefreshTokenRepository;
import com.aiplus.backend.auth.service.RefreshTokenService;
import com.aiplus.backend.users.model.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository repo;
    private static final Long REFRESH_DURATION_S = (long) 604800;

    @Override
    public RefreshToken createForUser(User user) {
        RefreshToken rt;
        // check if a refresh token exists
        repo.deleteByUser(user);
        List<RefreshToken> rts = repo.findByUser(user);

        if (rts.isEmpty()) {

            rt = new RefreshToken();
            rt.setUser(user);
            rt.setExpiryDate(LocalDateTime.now().plusSeconds(REFRESH_DURATION_S));
            rt.setToken(UUID.randomUUID().toString());
        } else {
            rt = rts.get(0);

        }
        return repo.save(rt);
    }

    @Override
    public RefreshToken verify(String token) {
        RefreshToken rt = repo.findByToken(token)
                .orElseThrow(() -> new ExpiredTokenException("Invalid refresh token"));
        if (rt.getExpiryDate().isBefore(LocalDateTime.now())) {
            repo.delete(rt);
            throw new ExpiredTokenException("Refresh token expired");
        }
        return rt;
    }

    @Override
    public void deleteForUser(User user) {
        repo.deleteByUser(user);
    }

}
