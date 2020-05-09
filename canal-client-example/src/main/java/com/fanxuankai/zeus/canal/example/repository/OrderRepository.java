package com.fanxuankai.zeus.canal.example.repository;

import com.fanxuankai.zeus.canal.example.domain.Order;
import com.fanxuankai.zeus.data.jpa.repository.BatchOperationRepository;

/**
 * @author fanxuankai
 */
public interface OrderRepository extends BatchOperationRepository<Order, Long> {

}
