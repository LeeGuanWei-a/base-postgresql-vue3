package com.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.core.domain.Demo;

import java.util.List;

/**
 * 示例代码Service接口
 *
 * @author basic
 * @date 2022-06-22
 */
public interface DemoService extends IService<Demo> {
    /**
     * 查询示例代码
     *
     * @param id 示例代码主键
     * @return 示例代码
     */
    public Demo selectDemoById(Long id);

    /**
     * 查询示例代码列表
     * 
     * @param Demo 示例代码
     * @return 示例代码集合
     */
    public List<Demo> selectDemoList(Demo demo);

    /**
     * 新增示例代码
     * 
     * @param Demo 示例代码
     * @return 结果
     */
    public int insertDemo(Demo demo);

    /**
     * 修改示例代码
     * 
     * @param Demo 示例代码
     * @return 结果
     */
    public int updateDemo(Demo demo);

    /**
     * 批量删除示例代码
     * 
     * @param ids 需要删除的示例代码主键集合
     * @return 结果
     */
    public int deleteDemoByIds(Long[] ids);

    /**
     * 删除示例代码信息
     * 
     * @param id 示例代码主键
     * @return 结果
     */
    public int deleteDemoById(Long id);
}
