package com.ht.ussp.uc.app.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort.Direction;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "PageConf", description = "页表配置")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageConf {

    @ApiModelProperty(value = "第几页", dataType = "int", example = "0")
    Integer page = 1;

    @ApiModelProperty(value = "页面条数", dataType = "int", example = "10")
    Integer size = 10;

    @ApiModelProperty(value = "搜索内容")
    String search;

    @ApiModelProperty(value = "排序列名集")
    List<String> sortNames = new ArrayList<String>() {
        /**
        * 
        */
        private static final long serialVersionUID = 7354697850723798363L;

        {
            add("id");
        }
    };

    @ApiModelProperty(value = "排序方向")
    List<Direction> sortOrders = new ArrayList<Direction>() {

        /**
         * 
         */
        private static final long serialVersionUID = 300067217264411943L;

        {
            add(Direction.ASC);
        }
    };

    public PageConf(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }
}
