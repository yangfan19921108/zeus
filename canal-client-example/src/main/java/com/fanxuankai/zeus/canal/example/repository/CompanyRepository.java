package com.fanxuankai.zeus.canal.example.repository;

import com.fanxuankai.zeus.canal.example.domain.Company;
import com.fanxuankai.zeus.data.jpa.repository.LogicDeleteRepository;

/**
 * @author fanxuankai
 */
public interface CompanyRepository extends LogicDeleteRepository<Company, Long> {

}
