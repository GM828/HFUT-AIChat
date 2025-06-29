package com.hfut.ai.entity.po;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 选修课程表
 * </p>
 *
 * @author GM
 * @since 2025-06-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("elective_course")
public class ElectiveCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 学生年级要求：1-至少大一，2-至少大二，3-至少大三，4-至少大四
     */
    private Integer gradeRequirement;

    /**
     * 课程类型：（哲学、历史）、（文学、语言）、（经济、法律）、（自然、环境）、（信息、编程）、（艺体、健康）、（创业、就业）
     */
    private String type;

    /**
     * 课程学分：可取值0.5，1，1.5，2
     */
    private BigDecimal credit;

    /**
     * 学习时长，单位: 周
     */
    private Integer durationWeeks;

    /**
     * 上课星期：如星期一到星期天
     */
    private String dayOfWeek;


}
