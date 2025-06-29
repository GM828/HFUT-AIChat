package com.hfut.ai.entity.query;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;

import java.math.BigDecimal;
import java.util.List;

/**
 * 选修课程可能存在的查询条件
 */
@Data
public class ElectiveCourseQuery {
    @ToolParam(required = false, description = "课程类型：哲学、历史，文学、语言，经济、法律，自然、环境，信息、编程，艺体、健康，创业、就业")
    private String type;

    @ToolParam(required = false, description = "学生年级要求：1-至少大一，2-至少大二，3-至少大三，4-至少大四")
    private Integer gradeRequirement;

    @ToolParam(required = false, description = "课程学分：可取值0.5，1，1.5，2")
    private BigDecimal credit;

    @ToolParam(required = false, description = "学习时长，单位: 周")
    private Integer durationWeeks;

    @ToolParam(required = false, description = "上课时间：如星期一到星期天、周末、工作日等")
    private String dayOfWeek;

    @ToolParam(required = false, description = "校区名称")
    private String campusName;

    @ToolParam(required = false, description = "排序方式")
    private List<Sort> sorts;

    @Data
    public static class Sort {
        @ToolParam(required = false, description = "排序字段: credit或durationWeeks")
        private String field;
        @ToolParam(required = false, description = "是否是升序: true/false")
        private Boolean asc;
    }
}