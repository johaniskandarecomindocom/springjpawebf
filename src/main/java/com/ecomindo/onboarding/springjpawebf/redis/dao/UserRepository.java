package com.ecomindo.onboarding.springjpawebf.redis.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ecomindo.onboarding.springjpawebf.redis.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, String> {}