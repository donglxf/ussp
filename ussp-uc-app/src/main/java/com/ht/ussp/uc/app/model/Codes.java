package com.ht.ussp.uc.app.model;

import java.util.HashSet;
import java.util.Set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "Codes", description = "编码集")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Codes {

    @ApiModelProperty(value = "codes", dataType = "string")
    Set<String> codes = new HashSet<String>(0);
    
}
