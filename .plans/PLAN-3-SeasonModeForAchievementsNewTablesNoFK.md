# 计划：为成就系统引入赛季模式（新增独立表，无物理外键） - v2 路径修正

**ID:** 3
**状态:** [ ] 未完成
**概要:** 参考 `SeasonWishController` 实现，为成就系统（`RuleAchievement` 和 `RuleAchievementLog` 相关功能）引入赛季模式。采用新增独立表（不修改现有表）的方式，并且表之间不设置物理外键，仅做逻辑关联。所有赛季相关接口通过请求头 `X-Season-Id` 传递赛季ID。**Controller层接口路径设计规则：新Controller的类级别路径 = `"/api/season"` + `原始Controller的类级别路径`；方法级别路径保持原始结构不变。**

## 1. 背景与目标

（与前版本一致）

## 2. 详细实施计划

### 2.1. 数据库层面（无物理外键）

（与前版本一致，但移除 season_member_point_logs 表）

1.  **`season_rule_achievements`**
2.  **`season_rule_achievement_logs`**

### 2.2. Java 代码层面

#### 2.2.1. Model (实体类)
（与前版本一致，但移除 SeasonMemberPointLogs.java）
*   **`SeasonRuleAchievement.java`**
*   **`SeasonRuleAchievementLog.java`**

#### 2.2.2. Mapper (DAO层)
（与前版本一致）

#### 2.2.3. Service (服务层)
（与前版本一致）

#### 2.2.4. Controller (控制层) - v2 路径修正
在 `com.tencent.wxcloudrun.controller` 包下新增。所有接口方法通过 `@RequestHeader(value = "X-Season-Id", required = true) Long seasonId` 获取赛季ID。
**类级别 `@RequestMapping` = `"/api/season"` + `原始Controller的类级别路径`。方法级别的 `@<HttpVerb>Mapping` 注解中的路径值保持与原始Controller中的一致。**

*   **`SeasonRuleAchievementController.java`**
    *   假设原始的成就定义Controller (例如 `RuleAchievementController`) 的类注解为 `@RequestMapping("/api/rule-achievements")`。
    *   `@RestController`
    *   `@RequestMapping("/api/season/api/rule-achievements")` // 类路径 = "/api/season" + "/api/rule-achievements"
    *   方法示例 (方法注解中的路径值与原始Controller保持一致):
        *   `@PostMapping("")` // 假设原始方法为 @PostMapping("") 或 @PostMapping("/")
            `createSeasonAchievement(@RequestHeader("X-Season-Id") Long seasonId, @RequestBody SeasonRuleAchievement achievement)`
            *   路径: `/api/season/api/rule-achievements`
        *   `@GetMapping("/{achievementId}")`
            `getSeasonAchievement(@RequestHeader("X-Season-Id") Long seasonId, @PathVariable Long achievementId)`
            *   路径: `/api/season/api/rule-achievements/{achievementId}`
        *   `@GetMapping("/rule/{ruleId}")`
            `listByRule(@RequestHeader("X-Season-Id") Long seasonId, @PathVariable Integer ruleId)`
            *   路径: `/api/season/api/rule-achievements/rule/{ruleId}`
        *   `@PutMapping("/{achievementId}")`
            `updateSeasonAchievement(@RequestHeader("X-Season-Id") Long seasonId, @PathVariable Long achievementId, @RequestBody SeasonRuleAchievement achievement)`
            *   路径: `/api/season/api/rule-achievements/{achievementId}`
        *   `@DeleteMapping("/{achievementId}")`
            `deleteSeasonAchievement(@RequestHeader("X-Season-Id") Long seasonId, @PathVariable Long achievementId)`
            *   路径: `/api/season/api/rule-achievements/{achievementId}`

*   **`SeasonRuleAchievementLogController.java`**
    *   原始 `RuleAchievementLogController` 的类注解为 `@RequestMapping("/api/rule-achievement-logs")`。
    *   `@RestController`
    *   `@RequestMapping("/api/season/api/rule-achievement-logs")` // 类路径 = "/api/season" + "/api/rule-achievement-logs"
    *   方法 (方法注解中的路径值与原始 `RuleAchievementLogController` 保持一致, 路径变量名可能因新实体而调整):
        *   `@GetMapping("/{logId}")` // 原始为 /{id}, 对应新实体字段 logId
            `getLogById(@RequestHeader("X-Season-Id") Long seasonId, @PathVariable("logId") Long logId)`
            *   路径: `/api/season/api/rule-achievement-logs/{logId}`
        *   `@GetMapping("/member/{mid}")`
            `getLogsByMemberId(@RequestHeader("X-Season-Id") Long seasonId, @PathVariable("mid") Integer mid)`
            *   路径: `/api/season/api/rule-achievement-logs/member/{mid}`
        *   `@GetMapping("/achievement/{raId}")` // raId 对应 SeasonRuleAchievement 的 ID (sra_id)
            `getLogsByAchievementId(@RequestHeader("X-Season-Id") Long seasonId, @PathVariable("raId") Long raId)`
            *   路径: `/api/season/api/rule-achievement-logs/achievement/{raId}`
        *   `@GetMapping("/member-achievements/{mid}")`
            `getMemberSeasonAchievements(@RequestHeader("X-Season-Id") Long seasonId, @PathVariable("mid") Integer mid)`
            *   路径: `/api/season/api/rule-achievement-logs/member-achievements/{mid}`
        *   `@PostMapping("")` // 假设原始方法为 @PostMapping("") 或 @PostMapping("/")
            `insertLog(@RequestHeader("X-Season-Id") Long seasonId, @RequestBody SeasonRuleAchievementLog log)`
            *   路径: `/api/season/api/rule-achievement-logs`
        *   `@DeleteMapping("/{logId}")` // 原始为 /{id}
            `deleteLogById(@RequestHeader("X-Season-Id") Long seasonId, @PathVariable("logId") Long logId)`
            *   路径: `/api/season/api/rule-achievement-logs/{logId}`
        *   `@DeleteMapping("/achievement/{raId}/member/{mid}")`
            `deleteLogByAchievementAndMember(@RequestHeader("X-Season-Id") Long seasonId, @PathVariable("raId") Long raId, @PathVariable("mid") Integer mid)`
            *   路径: `/api/season/api/rule-achievement-logs/achievement/{raId}/member/{mid}`
    *   新增强制检查成就接口 (作为 `SeasonRuleAchievementLogController` 的新方法):
        *   `@PostMapping("/member/{mid}/rule/{ruleId}/check-achievements")`
            `checkMemberAchievements(@RequestHeader("X-Season-Id") Long seasonId, @PathVariable Integer mid, @PathVariable Integer ruleId)`
            *   路径: `/api/season/api/rule-achievement-logs/member/{mid}/rule/{ruleId}/check-achievements`

### 2.3. Mermaid 图表 (示例：赛季成就检查流程)
（与前版本一致, 注意图中 HTTP POST 路径根据上述规则会是 `/api/season/api/rule-achievement-logs/member/{mid}/rule/{ruleId}/check-achievements`）
```mermaid
flowchart TD
    A[HTTP POST /api/season/api/rule-achievement-logs/member/{mid}/rule/{ruleId}/check-achievements] --> B{Controller: checkMemberAchievements};
    B -- seasonId, mid, ruleId --> C[Service: checkAndGrantSeasonAchievements];
    C --> D{Mapper: listActiveBySeasonIdAndRuleId};
    D -- List<SeasonRuleAchievement> --> C;
    C --> E{Loop: For each achievement};
    E -- Yes --> F{Mapper: getBySeasonIdAndSraIdAndMid (isAchieved?)};
    F -- Not Achieved --> G{Calculate Condition};
    G -- Condition Type --> H1[Mapper: getCheckInCount...];
    G -- Condition Type --> H2[Mapper: getPointLogsCount...];
    G -- Condition Type --> H3[Mapper: getPointSum...];
    H1 --> I{Condition Met?};
    H2 --> I;
    H3 --> I;
    I -- Yes --> J[Build SeasonRuleAchievementLog object];
    J -- log object (with seasonId) --> K[Mapper: insert Log];
    K --> E;
    I -- No --> E;
    F -- Achieved --> E;
    E -- No more achievements --> L[Return New Achievements List];
    L --> B;
    B --> M[HTTP Response];

    style A fill:#87CEEB,stroke:#333,stroke-width:2px
    style M fill:#FFA500,stroke:#333,stroke-width:2px
    style C fill:#90EE90,stroke:#333,stroke-width:2px
    style E fill:#90EE90,stroke:#333,stroke-width:2px
    style G fill:#90EE90,stroke:#333,stroke-width:2px
    style I fill:#90EE90,stroke:#333,stroke-width:2px
    style J fill:#90EE90,stroke:#333,stroke-width:2px
```

## 3. 潜在风险与应对
（与前版本一致）

## 4. 测试策略
（与前版本一致）

## 5. 总结
（与前版本一致, 强调新的路径构造规则）
通过新增独立的表和相应的代码模块，可以在不影响现有功能的前提下，为成就系统引入赛季模式。Controller 的路径设计将严格遵循：新Controller的类级别路径 = `"/api/season"` + `原始Controller的类级别路径`，而方法级别路径注解值保持原始结构不变。这确保了迁移后接口路径的逻辑一致性和可预测性。

（注：SeasonMemberPointLogs 已经以 SeasonPointLog 的形式存在于系统中，不需要重复添加这部分内容。）