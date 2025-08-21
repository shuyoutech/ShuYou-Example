package com.shuyoutech.example.domain;

import com.shuyoutech.common.mongodb.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author YangChao
 * @date 2025-08-13 21:55:39
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "向量记录表类")
public class VectorRecordEntity extends BaseEntity<VectorRecordEntity> {

    @Schema(description = "标题")
    private String title;

}
