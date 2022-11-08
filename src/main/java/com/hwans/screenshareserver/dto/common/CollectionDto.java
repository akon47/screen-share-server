package com.hwans.screenshareserver.dto.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@ApiModel(description = "Collection Dto")
public class CollectionDto<T extends Serializable> {
    @ApiModelProperty(value = "조회된 데이터")
    List<T> data;
    @ApiModelProperty(value = "조회된 데이터 수")
    int size;
}
