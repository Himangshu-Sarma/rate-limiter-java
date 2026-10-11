package com.project.ratelimiter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;
import java.util.List;

@Configuration
public class RedisConfig {

    @Bean
    public RedisScript<Long> tokenBucketScript() {
        RedisScript<Long> script = RedisScript.of(new ClassPathResource("lua/token_bucket.lua"), Long.class);
        return script;
    }
}
