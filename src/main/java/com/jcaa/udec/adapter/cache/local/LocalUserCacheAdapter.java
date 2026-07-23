package com.jcaa.udec.adapter.cache.local;

import com.jcaa.udec.domain.models.User;
import com.jcaa.udec.domain.ports.out.UserRepositoryPort;
import com.jcaa.udec.domain.valueobjects.EmailAddress;
import com.jcaa.udec.domain.valueobjects.UserId;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

@Repository
public class LocalUserCacheAdapter implements UserRepositoryPort {

    private static final String CACHE_NOT_CONFIGURED_MESSAGE = "Users cache is not configured";
    private static final String UNSUPPORTED_CACHE_MESSAGE = "Users cache does not expose a concurrent local map";

    private final Cache userCache;

    public LocalUserCacheAdapter(CacheManager cacheManager) {
        Cache configuredCache = cacheManager.getCache(LocalCacheNames.USERS);
        if (Objects.isNull(configuredCache)) {
            throw new IllegalStateException(CACHE_NOT_CONFIGURED_MESSAGE);
        }
        this.userCache = configuredCache;
    }

    @Override
    public void save(User user) {
        userCache.put(user.getId().value(), user);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return Optional.ofNullable(userCache.get(userId.value(), User.class));
    }

    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return findAll().stream()
                .filter(user -> Objects.equals(user.getEmail(), email))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        Object nativeCache = userCache.getNativeCache();
        if (!(nativeCache instanceof ConcurrentMap<?, ?> localValues)) {
            throw new IllegalStateException(UNSUPPORTED_CACHE_MESSAGE);
        }
        return localValues.values().stream()
                .filter(User.class::isInstance)
                .map(User.class::cast)
                .sorted(Comparator.comparing(User::getCreatedAt)
                        .thenComparing(user -> user.getId().value()))
                .toList();
    }

    @Override
    public void deleteById(UserId userId) {
        userCache.evict(userId.value());
    }
}
