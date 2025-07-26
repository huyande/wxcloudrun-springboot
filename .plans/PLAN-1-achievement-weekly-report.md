# 计划：成就周报功能实现

**文件状态**：[✅] 已完成

---

## 1. 功能概述

本计划旨在为"家庭积分小达人"应用实现"成就周报"功能。该功能将自动生成一个可视化的、个性化的周报，展示孩子在上一周的任务完成情况、积分表现和成长轨迹。此功能旨在增强家长对孩子表现的了解，促进亲子间的积极互动和良好习惯的养成，同时遵循轻量级实现原则，不引入新的核心数据表。

## 2. 详细实施计划

### 2.1 API 设计

我们将在 `StatisticsController` 中新增一个 API 端点，用于获取成就周报数据。

**端点**
```
GET /statistics/weekly-report/{mid}
```

**路径参数**
- `{mid}` (Integer): 必需，会员（孩子）的唯一标识符。

**查询参数**
- `date` (String, 可选): 查询周报的指定日期（格式: `YYYY-MM-DD`)。如果未提供，则默认为当前日期，系统将计算并返回上一个完整周（周一至周日）的报告。

**响应数据结构 (DTO)**
我们将定义一个新的 DTO `WeeklyReportDTO` 来封装周报数据。

```java
// src/main/java/com/tencent/wxcloudrun/dto/WeeklyReportDTO.java
@Data
public class WeeklyReportDTO {
    private String dateRange; // "📅 周报时间范围：7月15日 - 7月21日"
    private OverviewStats overview;
    private List<ChartData> dailyTrend; // 每日打卡趋势图 (柱状图)
    private List<ChartData> taskDistribution; // 各打卡项完成次数 (饼图)
    private String summaryText; // "本周孩子共打卡..."
    private List<String> highlights; // ["✅ 连续打卡...", "✅ 首次完成..."]
    private ComparisonStats comparison;

    @Data
    public static class OverviewStats {
        private int totalCheckIns;          // 打卡总次数
        private int pointsEarned;           // 获得总积分 (所有 num > 0 的总和)
        private int wishPointsSpent;        // 【调整】心愿消费积分
        private int penaltyPointsLost;      // 【新增】惩罚/其他扣分 (所有 num < 0 的总和)
        private String bestPerformingTask;     // 表现最好的任务名称
        private int bestPerformingTaskCount;  // 表现最好的任务完成次数
        private int activeDays;             // 活跃天数
    }

    @Data
    public static class ChartData {
        private String name; // e.g., "周一" or "阅读"
        private Integer value;
    }

    @Data
    public static class ComparisonStats {
        private String checkInsComparison; // "↑ 5次" or "↓ 2次"
        private String pointsEarnedComparison; // "↑ 20分" or "↓ 10分"
    }
}
```

### 2.2 详细数据指标

#### 2.2.1 核心概览指标 (Overview Metrics)

| 指标名称 | 描述 | 计算逻辑 / 数据源 |
| :--- | :--- | :--- |
| **打卡总次数**<br/>(Total Check-ins) | 反映孩子本周的整体参与度。 | **`COUNT(*)`** <br/> 从 `(Season)MemberPointLogsMapper` 查询，条件：`type` 为打卡类型（如 0, 5），`day` 在周报时间范围内。|
| **获得积分**<br/>(Points Earned) | 衡量孩子本周通过积极行为获得的所有奖励。 | **`SUM(num)`** <br/> 从 `(Season)MemberPointLogsMapper` 查询，条件：**`num > 0`**，`day` 在周报时间范围内。 |
| **心愿消费积分**<br/>(Wish Points Spent) | 孩子本周通过"心愿兑换"主动消费的积分。 | **`SUM(point)`** <br/> 从 `(Season)WishLogMapper` 查询，条件：`createdAt` 在周报时间范围内。 |
| **惩罚/扣分项**<br/>(Penalty Points Lost) | 孩子本周因非主动消费行为被扣除的积分。 | **`SUM(ABS(num))`** <br/> 从 `(Season)MemberPointLogsMapper` 查询，条件：**`num < 0`**，`day` 在周报时间范围内。|
| **表现最好的任务**<br/>(Best Performing Task) | 找出孩子本周最坚持、完成次数最多的任务。 | **`GROUP BY ruleId`**, **`COUNT(*)`**, **`ORDER BY COUNT DESC`**, **`LIMIT 1`** <br/> 从 `(Season)MemberPointLogsMapper` 查询，并JOIN `(season_)rules` 表获取任务名称。|
| **活跃天数**<br/>(Active Days) | 统计本周内孩子有打卡行为的总天数。 | **`COUNT(DISTINCT DATE(day))`** <br/> 从 `(Season)MemberPointLogsMapper` 查询，`day` 在周报时间范围内。|

#### 2.2.2 图表分析指标 (Chart Metrics)

| 指标名称 | 描述 | 计算逻辑 / 数据源 |
| :--- | :--- | :--- |
| **每日打卡趋势**<br/>(Daily Check-in Trend) | 用于生成**柱状图**，展示孩子在一周内每天的打卡次数。 | **`GROUP BY DATE(day)`**, **`COUNT(*)`** <br/> 从 `(Season)MemberPointLogsMapper` 查询。后端补全无打卡记录的日期。 |
| **各打卡项分布**<br/>(Task Distribution) | 用于生成**饼图**，展示本周各项任务完成次数的占比。 | **`GROUP BY ruleId`**, **`COUNT(*)`** <br/> 返回所有任务及其完成次数。|

#### 2.2.3 高光时刻触发指标 (Highlight Trigger Metrics)

| 指标名称 | 描述 | 计算逻辑 / 数据源 |
| :--- | :--- | :--- |
| **最大连续打卡天数**<br/>(Max Consecutive Days) | 计算本周内的最大连续打卡天数。 | 在 Service 层处理：查询周内所有打卡日期，排序后遍历计算。 |
| **首次完成特定任务**<br/>(First Time Task Completion) | 识别本周是否完成了某个之前从未做过的任务。 | 在 Service 层处理：对本周完成的每个任务，查询历史上是否存在记录。 |

### 2.3 后端实现

#### 2.3.1 Controller 层 (`StatisticsController.java`)
- **任务**: 新增 `getWeeklyReport` 方法。
- **代码**:
  ```java
  // ... existing imports ...
  import com.tencent.wxcloudrun.dto.WeeklyReportDTO; // 新增 DTO
  import java.time.LocalDate;
  import org.springframework.format.annotation.DateTimeFormat;
  
  // ... in StatisticsController class ...
  @GetMapping("/weekly-report/{mid}")
  public ApiResponse getWeeklyReport(
          @PathVariable Integer mid,
          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
          @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
      try {
          LocalDate reportDate = (date == null) ? LocalDate.now() : date;
          WeeklyReportDTO result = statisticsService.getWeeklyReport(mid, reportDate, seasonId);
          return ApiResponse.ok(result);
      } catch (Exception e) {
          logger.error("获取成就周报失败", e);
          return ApiResponse.error("获取成就周报失败: " + e.getMessage());
      }
  }
  ```

#### 2.3.2 Service 层 (`StatisticsService.java` 和 `StatisticsServiceImpl.java`)

1.  **`StatisticsService.java` (接口)**
    - **任务**: 添加 `getWeeklyReport` 方法声明。
2.  **`StatisticsServiceImpl.java` (实现)**
    - **任务**: 实现 `getWeeklyReport` 方法，聚合所有数据。

#### 2.3.3 DAO/Mapper 层

- **`MemberPointLogsMapper.xml / SeasonPointLogMapper.xml`**:
    - **任务**: 确保有按日期范围查询的方法，用于获取`打卡次数`、`获得积分 (num > 0)`、`扣分项 (num < 0)`、`任务分布`和`活跃天数`。大部分可复用或微调现有查询。
- **`WishLogMapper.xml`**:
    - **任务**: `getConsumedPointsByDateRange` 方法已存在，可以直接使用。
- **`SeasonWishLogMapper.xml`**:
    - **任务**: **需要新增**一个按日期范围查询心愿消费的方法 `getConsumedPointsByDateRange`。

### 2.4 业务逻辑流程图

```mermaid
graph TD;
    subgraph Frontend
        A[fa:fa-user 用户点击 "查看周报"] --> B{GET /statistics/weekly-report/{mid}};
    end

    subgraph Backend - Controller
        B --> C[StatisticsController];
    end

    subgraph Backend - Service
        C --> D{StatisticsService.getWeeklyReport};
        D --> E[计算日期范围];
        subgraph Data Aggregation
            E --> F1[获得积分<br/>(num>0)];
            E --> F2[心愿消费];
            E --> F3[惩罚扣分<br/>(num<0)];
            F1 & F2 & F3 --> H{数据计算与处理};
        end
        H --> I[生成总结语和高光时刻];
        I --> J[组装WeeklyReportDTO];
    end
    
    subgraph Backend - DAO/Mapper
        F1 --> DB1[(fa:fa-database MemberPointLogsMapper)];
        F2 --> DB2[(fa:fa-database WishLogMapper)];
        F3 --> DB1;
    end

    subgraph Frontend
        J --> K{返回DTO给前端};
        K --> L[fa:fa-chart-bar 前端渲染周报页面];
    end

    style A fill:#87CEEB; style L fill:#FFDAB9
    style C fill:#90EE90; style D fill:#90EE90; style J fill:#90EE90
```

## 3. 风险分析及应对措施

- **风险 1: 性能问题**
  - **描述**: 大数据量下实时计算周报可能会有延迟。
  - **应对措施**: SQL 优化、添加索引、考虑缓存。

- **风险 2: 数据源不完整**
  - **描述**: 积分消耗的来源可能不止我们讨论的这几种。
  - **应对措施**: 与业务方最终确认所有积分消耗场景，确保查询逻辑覆盖所有情况。

## 4. 测试策略

- **单元测试**:
  - [ ] **日期计算**: 验证各种边界条件下的周范围计算。
  - [ ] **积分计算**: 使用 Mock 数据验证 `pointsEarned`、`wishPointsSpent` 和 `penaltyPointsLost` 的计算逻辑是否正确。
  - [ ] **空数据处理**: 测试用户在周内无任何活动时的表现。
- **集成测试**:
  - [ ] **API 连通性**: 请求接口并验证返回 `200 OK` 和正确的 DTO 结构。
  - [ ] **数据准确性**: 在测试数据库中构造数据，调用 API 并断言返回结果的准确性。
  - [ ] **赛季/非赛季切换**: 验证 `seasonId` 存在与否能正确切换数据源。

---
**实施状态跟踪**
- [✅] DTO 定义
- [✅] Controller 修改
- [✅] Service 接口及实现
- [✅] DAO/Mapper 修改与新增
- [ ] 单元测试编写
- [ ] 集成测试编写
