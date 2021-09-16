package com.ecomindo.onboarding.springjpawebf.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
public class Redis {

	@Autowired
	Config config;

	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
		configuration.setHostName(config.getRedisHost());
		configuration.setPort(config.getRedisPort());
		configuration.setPassword(config.getRedisPassword());
		configuration.setDatabase(config.getRedisDatabase());

		JedisClientConfiguration jedisConfig = JedisClientConfiguration.builder().useSsl().build();

		JedisConnectionFactory jcf = new JedisConnectionFactory(configuration, jedisConfig);
		return jcf;
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
		return template;
	}

}
