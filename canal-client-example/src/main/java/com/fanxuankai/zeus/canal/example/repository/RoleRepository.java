package com.fanxuankai.zeus.canal.example.repository;

import com.fanxuankai.zeus.canal.example.domain.Role;
import com.fanxuankai.zeus.data.jpa.repository.LogicDeleteRepository;

/**
 * @author fanxuankai
 */
public interface RoleRepository extends LogicDeleteRepository<Role, Long> {
}
