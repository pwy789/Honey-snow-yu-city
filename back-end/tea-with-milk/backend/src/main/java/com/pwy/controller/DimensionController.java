package com.pwy.controller;


import com.pwy.common.Result;
import com.pwy.entity.pojo.Dimension;
import com.pwy.entity.vo.DimensionVo;
import com.pwy.service.DimensionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dimension")
public class DimensionController {
    @Autowired
    private DimensionService dimensionService;
    //获取所有的维度以及对应的sku
  @GetMapping("/getall")
    public Result getAll(){
      List<DimensionVo> dimensionVos = dimensionService.getAll();
      return Result.success(dimensionVos);

  }
    //添加维度
  @PostMapping("/add")
    public Result addDimension(@RequestBody Dimension dimension){


      if(dimensionService.save(dimension)){
          return  Result.success("添加成功");
      }
     return  Result.error("添加失败,请稍后重试");
  }
  //修改维度
    @PutMapping("/update")
    public  Result updateDimension(@RequestBody Dimension dimension){
        if (dimensionService.updateById(dimension)) {
            return  Result.success("修改成功");
        }
        return  Result.error("修改失败,请稍后重试");
    }
    //删除维度
    @DeleteMapping("/delete/{id}")
    public Result deleteDimensionById(@PathVariable Integer id){
        if (dimensionService.delById(id)) {
            return  Result.success("删除成功");
        }
        return  Result.error("删除失败,请稍后重试");
    }
}
