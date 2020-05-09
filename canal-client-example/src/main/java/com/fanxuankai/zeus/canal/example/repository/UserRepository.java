package com.fanxuankai.zeus.canal.example.repository;

import com.fanxuankai.zeus.canal.example.domain.User;
import com.fanxuankai.zeus.data.jpa.repository.LogicDeleteRepository;

/**
 * @author fanxuankai
 */
public interface UserRepository extends LogicDeleteRepository<User, Long> {

}
