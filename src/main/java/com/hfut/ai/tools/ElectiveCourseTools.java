package com.hfut.ai.tools;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.hfut.ai.entity.po.CourseReservation;
import com.hfut.ai.entity.po.ElectiveCourse;
import com.hfut.ai.entity.po.School;
import com.hfut.ai.entity.query.ElectiveCourseQuery;
import com.hfut.ai.service.ICourseReservationService;
import com.hfut.ai.service.IElectiveCourseService;
import com.hfut.ai.service.ISchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ElectiveCourseTools {

    private final IElectiveCourseService electiveCourseService;
    private final ISchoolService schoolService;
    private final ICourseReservationService courseReservationService;


    /**
     * 将用户输入的“周一到周日”、“周末”、“工作日”等转换为“星期一”到“星期天”的列表
     *
     * @param userInput 用户输入的时间段描述
     * @return 星期几列表，如 ["星期一", "星期二"]
     */
    public static List<String> parseDayOfWeek(String userInput) {
        if (userInput == null || userInput.isEmpty()) {
            return List.of();
        }

        userInput = userInput.trim().toLowerCase();
        System.out.println("userInput: " + userInput);

        if (userInput.contains("周一") && userInput.contains("周日")) {
            // “周一到周日”
            return List.of("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天");
        } else if (userInput.contains("周末")) {
            // “周末”
            return List.of("星期六", "星期天");
        } else if (userInput.contains("工作日")) {
            // “工作日”
            return List.of("星期一", "星期二", "星期三", "星期四", "星期五");
        } else if (userInput.contains("周一") || userInput.contains("星期一")) {
            return List.of("星期一");
        } else if (userInput.contains("周二") || userInput.contains("星期二")) {
            return List.of("星期二");
        } else if (userInput.contains("周三") || userInput.contains("星期三")) {
            return List.of("星期三");
        } else if (userInput.contains("周四") || userInput.contains("星期四")) {
            return List.of("星期四");
        } else if (userInput.contains("周五") || userInput.contains("星期五")) {
            return List.of("星期五");
        } else if (userInput.contains("周六") || userInput.contains("星期六")) {
            return List.of("星期六");
        } else if (userInput.contains("周日") || userInput.contains("星期天")) {
            return List.of("星期天");
        } else {
            return List.of();
        }
    }

    @Tool(description = "根据条件查询选修课程")
    public List<ElectiveCourse> queryElectiveCourse(@ToolParam(required = false, description = "选修课程查询条件") ElectiveCourseQuery query) {

        if (query == null)
        {
            // 如果没有查询条件，则返回所有选修课程
            return electiveCourseService.list();
        }

        QueryChainWrapper<ElectiveCourse> wrapper = electiveCourseService.query();
        wrapper
                .like(query.getType() != null, "type", query.getType()) //  课程类型要求
                .le(query.getGradeRequirement() != null, "grade_requirement", query.getGradeRequirement()); // 学生年级要求
                // .eq(query.getDayOfWeek() != null, "day_of_week", query.getDayOfWeek()); //  星期几要求

        // 星期几要求
        if (query.getDayOfWeek() != null)
        {
            List<String> dayList = parseDayOfWeek(query.getDayOfWeek());
            System.out.println("模糊查询的星期几列表：" + dayList);
            if (!dayList.isEmpty()) {
                wrapper.in("day_of_week", dayList);
            }
        }

        // 根据校区名称进行模糊查询，要求校区必须开设该课程
        if (query.getCampusName() != null && !query.getCampusName().isEmpty()) {
            String campusName = query.getCampusName();
            wrapper.exists(
                    "SELECT 1 FROM school s " +
                            "JOIN school_course sc ON s.id = sc.school_id " +
                            "WHERE s.name LIKE CONCAT('%', {0}, '%') AND sc.course_id = elective_course.id",
                    campusName
            );
        }
        // 如果存在排序条件，则进行排序
        if(query.getSorts() != null) {
            for (ElectiveCourseQuery.Sort sort : query.getSorts()) {
                wrapper.orderBy(true, sort.getAsc(), sort.getField());
            }
        }
        // 如果没有排序条件，则默认按学分从高到低、时长从短到长排序
        if (query.getSorts() == null || query.getSorts().isEmpty()) {
            wrapper.orderBy(true, false, "credit")  // 默认按学分从高到低
                    .orderBy(true, true, "duration_weeks");  // 学习时长短优先
        }
        return wrapper.list();
    }

    /**
     * 如果没有找到符合要求的课程,根据年级查询该年级可选的其他课程
     * @param gradeRequirement
     * @return
     */
    @Tool(description = "查询符合用户年级的其它课程推荐")
    public List<ElectiveCourse> queryOtherCoursesByGradeRequirement(
            @ToolParam(description = "学员所在年级") Integer gradeRequirement) {
        if (gradeRequirement == null) {
            return electiveCourseService.list(); // 如果年级为空，返回全部课程
        }
        return electiveCourseService.query()
                .le("grade_requirement", gradeRequirement) // 年级 ≤ 用户年级即可选
                .orderBy(true, false, "credit")  // 默认按学分从高到低
                .orderBy(true, true, "duration_weeks")  // 学习时长短优先
                .list();
    }

    /**
     * 检查校区是否存在
     * @param campusName 校区名称
     * @return true 表示校区存在，false 表示校区不存在
     */
    @Tool(description = "检查校区是否存在")
    public boolean isCampusExists(@ToolParam(description = "校区名称") String campusName) {
        return schoolService.query()
                .like("name", campusName)
                .count() > 0;
    }

    /**
     * 如果用户输入的校区不存在，查询所有校区列表，让用户重新选择
     * @return
     */
    @Tool(description = "查询所有校区列表")
    public List<School> getAllCampusList() {
        return schoolService.list();
    }

    @Tool(description = "根据校区名称查询当前校区的所有课程")
    public List<ElectiveCourse> queryCourseByCampusName(@ToolParam(required = true, description = "校区名称") String campusName) {
        return electiveCourseService.query()
                .exists(
                        "SELECT 1 FROM school s " +
                        "JOIN school_course sc ON s.id = sc.school_id " +
                        "WHERE s.name LIKE CONCAT('%', {0}, '%') AND sc.course_id = elective_course.id"
                ).list();
    }

    @Tool(description = "根据课程名称和校区名称查询是否开设该课程")
    public ElectiveCourse queryCourseByCourseNameAndCampusName(
            @ToolParam(description = "课程名称") String courseName,
            @ToolParam(description = "校区名称") String campusName) {
        // 防止用户输入为空时导致 SQL 错误或 NPE
        if (courseName == null || campusName == null) {
            return null;
        }
        return electiveCourseService.query()
                .eq("name", courseName)
                .exists(
                        "SELECT 1 FROM school s " +
                                "JOIN school_course sc ON s.id = sc.school_id " +
                                "WHERE s.name LIKE CONCAT('%', {0}, '%') AND sc.course_id = elective_course.id",
                        campusName
                ).one();
    }

    /**
     * 如果用户选择了某门课程但该校区未开设此课程，根据校区名称和其他查询条件重新筛选课程
     * @param campusName 校区名称
     * @param query 其他查询条件
     * @return 筛选后的课程列表
     */
    @Tool(description = "根据校区名称和之前的查询条件筛选课程")
    public List<ElectiveCourse> queryCourseByCampusWithCondition(
            @ToolParam(description = "校区名称") String campusName,
            @ToolParam(description = "选修课程查询条件") ElectiveCourseQuery query) {

        QueryChainWrapper<ElectiveCourse> wrapper = electiveCourseService.query();

        // 先添加原有查询条件
        wrapper
                .like(query.getType() != null, "type", query.getType())
                .le(query.getGradeRequirement() != null, "grade_requirement", query.getGradeRequirement())
                .eq(query.getDayOfWeek() != null, "day_of_week", query.getDayOfWeek());

        // 再加上校区限制
        wrapper.exists(
                "SELECT 1 FROM school s " +
                        "JOIN school_course sc ON s.id = sc.school_id " +
                        "WHERE s.name LIKE CONCAT('%', {0}, '%') AND sc.course_id = elective_course.id",
                campusName);

        if (query.getSorts() != null) {
            for (ElectiveCourseQuery.Sort sort : query.getSorts()) {
                wrapper.orderBy(true, sort.getAsc(), sort.getField());
            }
        }
        return wrapper.list();
    }


    @Tool(description = "生成预约单，返回预约单号")
    public Integer crateCourseReservation(
            @ToolParam(description = "预约课程") String course,
            @ToolParam(description = "学生姓名") String studentName,
            @ToolParam(description = "联系方式") String contactInfo,
            @ToolParam(description = "预约校区") String school,
            @ToolParam(required = false, description = "备注") String remark) {
        //  生成预约单
        CourseReservation reservation = new CourseReservation();
        reservation.setCourse(course);
        reservation.setStudentName(studentName);
        reservation.setContactInfo(contactInfo);
        reservation.setSchool(school);
        reservation.setRemark(remark);
        //  保存预约单
        courseReservationService.save(reservation);

        return reservation.getId();
    }

}
