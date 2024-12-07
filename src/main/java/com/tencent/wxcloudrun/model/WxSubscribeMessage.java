package com.tencent.wxcloudrun.model;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class WxSubscribeMessage {
    private String touser;
    private String template_id;
    private String page;
    private Map<String, TemplateData> data;

    @Data
    @Builder
    public static class TemplateData {
        private String value;
    }
}
