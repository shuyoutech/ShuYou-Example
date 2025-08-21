package com.shuyoutech.example.service;

import com.shuyoutech.example.domain.VectorRecordEntity;

/**
 * @author YangChao
 * @date 2025-08-13 21:55:39
 **/
public interface VectorRecordService {

    void createCollection();

    void save(VectorRecordEntity entity);

    void update(VectorRecordEntity entity);

    void get(String id);

    void delete(String id);

    void search(String keyword);

}
