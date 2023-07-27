package com.project.cafeteriaManagementSystem.model.material;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MaterialInfo {

    private String materialName;
    private String unitMeasure;
    private double quantity;
}
