package com.tencent.wxcloudrun.controller.drugRecord;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.drugRecord.Tags;
import com.tencent.wxcloudrun.service.drugRecord.TagDrugRelasService;
import com.tencent.wxcloudrun.service.drugRecord.TagsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drugRecord/tags")
public class TagsController {
    
    @Autowired
    private TagsService tagsService;
    
    @Autowired
    private TagDrugRelasService tagDrugRelasService;
    
    @GetMapping("/list")
    public ApiResponse list(@RequestHeader(value = "X-WX-OPENID", required = false) String openid) {
        if (StringUtils.isEmpty(openid)) {
            return ApiResponse.error("openid null");
        }
        List<Tags> tags = tagsService.listByOpenid(openid);
        return ApiResponse.ok(tags);
    }
    
    @GetMapping("/drug/{drugId}")
    public ApiResponse listByDrug(@PathVariable Integer drugId,
                                @RequestHeader(value = "X-WX-OPENID", required = false) String openid) {
        if (StringUtils.isEmpty(openid)) {
            return ApiResponse.error("openid null");
        }
        List<Tags> tags = tagsService.listByDrugId(drugId, openid);
        return ApiResponse.ok(tags);
    }
    
    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Integer id,
                             @RequestHeader(value = "X-WX-OPENID", required = false) String openid) {
        if (StringUtils.isEmpty(openid)) {
            return ApiResponse.error("openid null");
        }
        Tags tags = tagsService.getByIdAndOpenid(id, openid);
        return ApiResponse.ok(tags);
    }
    
    @PostMapping
    public ApiResponse add(@RequestBody Tags tags,
                          @RequestHeader(value = "X-WX-OPENID", required = false) String openid) {
        if (StringUtils.isEmpty(openid)) {
            return ApiResponse.error("openid null");
        }
        tags.setOpenid(openid);
        int result = tagsService.insert(tags);
        return result > 0 ? ApiResponse.ok(tags) : ApiResponse.error("添加失败");
    }
    
    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable Integer id,
                            @RequestBody Tags tags,
                            @RequestHeader(value = "X-WX-OPENID", required = false) String openid) {
        if (StringUtils.isEmpty(openid)) {
            return ApiResponse.error("openid null");
        }
        tags.setId(id);
        tags.setOpenid(openid);
        int result = tagsService.update(tags);
        return result > 0 ? ApiResponse.ok(tags) : ApiResponse.error("更新失败");
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Integer id,
                            @RequestHeader(value = "X-WX-OPENID", required = false) String openid) {
        if (StringUtils.isEmpty(openid)) {
            return ApiResponse.error("openid null");
        }
        // 先删除标签与药品的关联关系
        tagDrugRelasService.removeAllDrugsFromTag(id);
        // 再删除标签
        int result = tagsService.deleteByIdAndOpenid(id, openid);
        return result > 0 ? ApiResponse.ok() : ApiResponse.error("删除失败");
    }
    
    @PostMapping("/{tagId}/drug/{drugId}")
    public ApiResponse addTagToDrug(@PathVariable Integer tagId,
                                  @PathVariable Integer drugId) {
        int result = tagDrugRelasService.addTagToDrug(drugId, tagId);
        return result > 0 ? ApiResponse.ok() : ApiResponse.error("添加标签失败");
    }
    
    @DeleteMapping("/{tagId}/drug/{drugId}")
    public ApiResponse removeTagFromDrug(@PathVariable Integer tagId,
                                       @PathVariable Integer drugId) {
        int result = tagDrugRelasService.removeTagFromDrug(drugId, tagId);
        return result > 0 ? ApiResponse.ok() : ApiResponse.error("移除标签失败");
    }
} 