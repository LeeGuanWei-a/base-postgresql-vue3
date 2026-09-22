package com.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.core.domain.Demo;

import java.util.List;

/**
 * 示例代码Mapper接口
 *
 * @author basic
 * @date 2022-06-22
 */
public interface DemoMapper extends BaseMapper<Demo> {
    /**
     * 查询示例代码
     *
     * @param id 示例代码主键
     * @return 示例代码
     */
    Demo selectDemoById(Long id);

    /**
     * 查询示例代码列表
     *
     * @param sysDemo 示例代码
     * @return 示例代码集合
     */
    List<Demo> selectDemoList(Demo sysDemo);

    /**
     * 新增示例代码
     *
     * @param sysDemo 示例代码
     * @return 结果
     */
    int insertDemo(Demo sysDemo);

    /**
     * 修改示例代码
     *
     * @param sysDemo 示例代码
     * @return 结果
     */
    int updateDemo(Demo sysDemo);

    /**
     * 删除示例代码
     *
     * @param id 示例代码主键
     * @return 结果
     */
    int deleteDemoById(Long id);

    /**
     * 批量删除示例代码
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteDemoByIds(Long[] ids);
}
