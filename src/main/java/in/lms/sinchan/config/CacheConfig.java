package in.lms.sinchan.config;

import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

import lombok.extern.slf4j.Slf4j;

@EnableCaching
@Configuration
public class CacheConfig {

	@Bean
	public CacheManager cacheManager() {
		String specAsString = "initialCapacity=100, maximunSize = 500, expireAfterAccess=5m, recordStats";
		CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager("booksCache", "studentCache");
		caffeineCacheManager.setAllowNullValues(false);
		// caffeineCacheManager.setCacheSpecification(specAsString);
		// caffeineCacheManager.setCaffeineSpec(caffeineSpec());
		caffeineCacheManager.setCaffeine(caffeineCacheBuilder());
		return caffeineCacheManager;
	}

	private Caffeine<Object, Object> caffeineCacheBuilder() {
		return Caffeine.newBuilder().initialCapacity(100).maximumSize(500).expireAfterAccess(5, TimeUnit.MINUTES)
				.expireAfterWrite(5, TimeUnit.MINUTES).recordStats().removalListener(new CustomRemovalListener());
	}

	private CaffeineSpec caffeineSpec() {
		return CaffeineSpec.parse("initialCapacity=100, maximunSize = 500, expireAfterAccess=5m, recordStats");
	}
}

@Slf4j
class CustomRemovalListener implements RemovalListener<Object, Object> {
	@Override
	public void onRemoval(@Nullable Object key, @Nullable Object value, @NonNull RemovalCause cause) {
		log.info("Removal listener called with key {} cause {} causeWasEvicted {}", key, cause, cause.wasEvicted());
	}

}
