package com.rahul.durgesh.rediscaching.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //data set into redis
    public void setDataInRedisCache(String key, String value, int timeToLive) {
        redisTemplate.opsForValue().set(key, value, timeToLive, TimeUnit.SECONDS);
    }

    //get data from redis
    public String getDataFromRedisCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
