package in.lms.sinchan.config;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.common.cache.CacheBuilder;

@EnableCaching
@Configuration
@RefreshScope
public class CacheConfig {

    @Value("${cache.book.cache.init.capacity:10}")
    private Integer bookCacheInitCapacity;

    @Value("${cache.schedules.refresh.time:60}")
    private Integer schedulesRefreshTime;

    @Bean("bookCache")
    public Cache getBookCache() {
        return new GuavaCache("bookCache",
                        CacheBuilder.newBuilder().initialCapacity(bookCacheInitCapacity)
                                        .maximumSize(bookCacheInitCapacity)
                                        .expireAfterWrite(schedulesRefreshTime, TimeUnit.MINUTES)
                                        .build());
    }

}
