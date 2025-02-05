package com.tencent.wxcloudrun.controller.drugRecord;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.PageResponse;
import com.tencent.wxcloudrun.dto.drugRecord.DrugCodeResponse;
import com.tencent.wxcloudrun.model.drugRecord.Drugs;
import com.tencent.wxcloudrun.service.drugRecord.DrugBarcodeService;
import com.tencent.wxcloudrun.service.drugRecord.DrugsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drugRecord/drugs")
public class DrugsController {
    
    @Autowired
    private DrugsService drugsService;
    
    @Autowired
    private DrugBarcodeService drugBarcodeService;
    
    @GetMapping("/list")
    public ApiResponse list(@RequestHeader(value = "X-WX-OPENID", required = false) String openid,
                          @RequestParam(required = false) String name,
                          @RequestParam(required = false) String code,
                          @RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize) {
        if (StringUtils.isEmpty(openid)) {
            return ApiResponse.error("openid null");
        }
        PageResponse<Drugs> pageResponse = drugsService.listByCondition(openid, name, code, pageNum, pageSize);
        return ApiResponse.ok(pageResponse);
    }
    
    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Integer id, 
                             @RequestHeader(value = "X-WX-OPENID", required = false) String openid) {
        if (StringUtils.isEmpty(openid)) {
            return ApiResponse.error("openid null");
        }
        Drugs drugs = drugsService.getByIdAndOpenid(id, openid);
        return ApiResponse.ok(drugs);
    }
    
    @PostMapping
    public ApiResponse add(@RequestBody Drugs drugs,
                          @RequestHeader(value = "X-WX-OPENID", required = false) String openid) {
        if (StringUtils.isEmpty(openid)) {
            return ApiResponse.error("openid null");
        }
        drugs.setOpenid(openid);
        int result = drugsService.insert(drugs);
        return result > 0 ? ApiResponse.ok(drugs) : ApiResponse.error("添加失败");
    }
    
    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable Integer id,
                            @RequestBody Drugs drugs,
                            @RequestHeader(value = "X-WX-OPENID", required = false) String openid) {
        if (StringUtils.isEmpty(openid)) {
            return ApiResponse.error("openid null");
        }
        drugs.setId(id);
        drugs.setOpenid(openid);
        int result = drugsService.update(drugs);
        return result > 0 ? ApiResponse.ok(drugs) : ApiResponse.error("更新失败");
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Integer id,
                            @RequestHeader(value = "X-WX-OPENID", required = false) String openid) {
        if (StringUtils.isEmpty(openid)) {
            return ApiResponse.error("openid null");
        }
        int result = drugsService.deleteByIdAndOpenid(id, openid);
        return result > 0 ? ApiResponse.ok() : ApiResponse.error("删除失败");
    }
    
    @GetMapping("/barcode/{code}")
    public ApiResponse queryBarcode(@PathVariable String code) {
        if (StringUtils.isEmpty(code)) {
            return ApiResponse.error("条形码不能为空");
        }
        
        DrugCodeResponse response = drugBarcodeService.queryByCode(code);
        if (response == null) {
            return ApiResponse.error("未查询到药品信息");
        }
        return ApiResponse.ok(response);
    }
} 