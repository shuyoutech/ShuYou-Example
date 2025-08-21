package com.shuyoutech.example.controller;

import com.shuyoutech.common.core.model.R;
import com.shuyoutech.common.core.model.group.SaveGroup;
import com.shuyoutech.common.core.model.group.UpdateGroup;
import com.shuyoutech.example.domain.VectorRecordEntity;
import com.shuyoutech.example.service.VectorRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author YangChao
 * @date 2025-08-13 21:55:39
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/vectorRecord")
@Tag(name = "VectorRecordController", description = "向量记录API控制器")
public class VectorRecordController {

    @PostMapping(path = "createCollection")
    @Operation(description = "创建集合")
    public R<Void> createCollection() {
        vectorRecordService.createCollection();
        return R.success();
    }

    @PostMapping(path = "get/{id}")
    @Operation(description = "查询向量记录")
    public R<Void> get(@PathVariable String id) {
        vectorRecordService.get(id);
        return R.success();
    }

    @PostMapping(path = "save")
    @Operation(description = "新增向量记录")
    public R<Void> save(@Validated({SaveGroup.class}) @RequestBody VectorRecordEntity entity) {
        vectorRecordService.save(entity);
        return R.success();
    }

    @PostMapping(path = "update")
    @Operation(description = "修改向量记录")
    public R<Void> update(@Validated({UpdateGroup.class}) @RequestBody VectorRecordEntity entity) {
        vectorRecordService.update(entity);
        return R.success();
    }

    @PostMapping(path = "delete/{id}")
    @Operation(description = "删除向量记录")
    public R<Void> delete(@PathVariable String id) {
        vectorRecordService.delete(id);
        return R.success();
    }

    @PostMapping(path = "search")
    @Operation(description = "搜索向量记录")
    public R<Void> search(@RequestBody VectorRecordEntity entity) {
        vectorRecordService.search(entity.getTitle());
        return R.success();
    }

    private final VectorRecordService vectorRecordService;

}
