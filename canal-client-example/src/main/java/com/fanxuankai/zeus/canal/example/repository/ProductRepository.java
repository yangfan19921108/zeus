package com.fanxuankai.zeus.canal.example.repository;

import com.fanxuankai.zeus.canal.example.domain.Product;
import com.fanxuankai.zeus.data.jpa.repository.LogicDeleteRepository;

/**
 * @author fanxuankai
 */
public interface ProductRepository extends LogicDeleteRepository<Product, Long> {
}
