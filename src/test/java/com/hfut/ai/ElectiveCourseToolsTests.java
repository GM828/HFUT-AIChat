package com.hfut.ai;

import com.hfut.ai.entity.po.ElectiveCourse;
import com.hfut.ai.entity.po.School;
import com.hfut.ai.entity.query.ElectiveCourseQuery;
import com.hfut.ai.service.IElectiveCourseService;
import com.hfut.ai.tools.ElectiveCourseTools;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@RequiredArgsConstructor
public class ElectiveCourseToolsTests {

    @Autowired
    private ElectiveCourseTools electiveCourseTools;

    @Autowired
    private IElectiveCourseService electiveCourseService; // 可选：用于手动构建查询验证

    @Test
    void testQueryWithAllConditions() {
        // 准备查询条件
        ElectiveCourseQuery query = new ElectiveCourseQuery();
        query.setType("信息、编程");
        query.setGradeRequirement(4);
        query.setDayOfWeek("星期天");
        query.setCampusName("宣城");

        // 添加排序条件
        ElectiveCourseQuery.Sort sort1 = new ElectiveCourseQuery.Sort();
        sort1.setField("credit");
        sort1.setAsc(false);

        ElectiveCourseQuery.Sort sort2 = new ElectiveCourseQuery.Sort();
        sort2.setField("duration_weeks");
        sort2.setAsc(true);

        query.setSorts(List.of(sort1, sort2));

        // 执行查询
        List<ElectiveCourse> courses = electiveCourseTools.queryElectiveCourse(query);

        // 输出结果数量验证
        System.out.println("查询到课程数：" + courses.size());
        assertTrue(courses.size() >= 0); // 确保没有异常抛出
    }

    @Test
    void testQueryWithoutCampusAndSort() {
        ElectiveCourseQuery query = new ElectiveCourseQuery();
        query.setType("艺体、健康");
        query.setGradeRequirement(1);
        query.setDayOfWeek("星期五");

        List<ElectiveCourse> courses = electiveCourseTools.queryElectiveCourse(query);

        System.out.println("查询结果数量：" + courses.size());
        courses.forEach(System.out::println);
    }

    @Test
    void testDefaultSort() {
        ElectiveCourseQuery query = new ElectiveCourseQuery();
        query.setType("文学、语言");

        List<ElectiveCourse> courses = electiveCourseTools.queryElectiveCourse(query);

        System.out.println("默认排序结果数量：" + courses.size());
        courses.forEach(System.out::println);
    }


    @Test
    void testQueryCourseByCampusWithCondition() {
        String campusName = "宣城校区";
        ElectiveCourseQuery query = new ElectiveCourseQuery();
        query.setType("信息、编程");
        query.setGradeRequirement(4);
        query.setDayOfWeek("星期天");

        // 添加排序条件
        ElectiveCourseQuery.Sort sort1 = new ElectiveCourseQuery.Sort();
        sort1.setField("credit");
        sort1.setAsc(false);

        ElectiveCourseQuery.Sort sort2 = new ElectiveCourseQuery.Sort();
        sort2.setField("duration_weeks");
        sort2.setAsc(true);

        query.setSorts(List.of(sort1, sort2));

        // 执行查询
        List<ElectiveCourse> courses = electiveCourseTools.queryCourseByCampusWithCondition(campusName,query);

        // 输出结果数量验证
        System.out.println("查询到课程数：" + courses.size());
        courses.forEach(System.out::println);
    }

    @Test
    void testParseDayOfWeek() {
        String userInput = "工作日";
        System.out.println("解析结果：" + ElectiveCourseTools.parseDayOfWeek(userInput));
        System.out.println("解析结果：" + ElectiveCourseTools.parseDayOfWeek(userInput));
        System.out.println("解析结果：" + ElectiveCourseTools.parseDayOfWeek(userInput));
        System.out.println("解析结果：" + ElectiveCourseTools.parseDayOfWeek(userInput));
        System.out.println("解析结果：" + ElectiveCourseTools.parseDayOfWeek(userInput));
    }

    @Test
    void testGetAllCampusList() {
        List<School> schools = electiveCourseTools.getAllCampusList();
        System.out.println("所有校区列表：" + schools);
    }
}
