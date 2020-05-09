package com.fanxuankai.zeus.canal.example.repository;

import com.fanxuankai.zeus.canal.example.domain.Customer;
import com.fanxuankai.zeus.data.jpa.repository.LogicDeleteRepository;

/**
 * @author fanxuankai
 */
public interface CustomerRepository extends LogicDeleteRepository<Customer, Long> {

}
