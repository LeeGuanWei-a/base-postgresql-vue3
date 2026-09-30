package com.core.service.impl;


import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.core.domain.Demo;
import com.core.mapper.DemoMapper;
import com.core.service.DemoService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 示例代码Service业务层处理
 *
 * @author basic
 * @date 2022-06-22
 */
@Service
public class DemoServiceImpl extends ServiceImpl<DemoMapper, Demo> implements DemoService {


    /**
     * 查询示例代码
     *
     * @param id 示例代码主键
     * @return 示例代码
     */
    @Override
    public Demo selectDemoById(Long id)
    {
        return baseMapper.selectDemoById(id);
    }

    /**
     * 查询示例代码列表
     * 
     * @param Demo 示例代码
     * @return 示例代码
     */
    @Override
    public List<Demo> selectDemoList(Demo demo)
    {
        return baseMapper.selectDemoList(demo);
    }

    /**
     * 新增示例代码
     * 
     * @param Demo 示例代码
     * @return 结果
     */
    @Override
    public int insertDemo(Demo demo)
    {
        return baseMapper.insertDemo(demo);
    }

    /**
     * 修改示例代码
     * 
     * @param Demo 示例代码
     * @return 结果
     */
    @Override
    public int updateDemo(Demo demo)
    {
        return baseMapper.updateDemo(demo);
    }

    /**
     * 批量删除示例代码
     * 
     * @param ids 需要删除的示例代码主键
     * @return 结果
     */
    @Override
    public int deleteDemoByIds(Long[] ids)
    {
        return baseMapper.deleteDemoByIds(ids);
    }

    /**
     * 删除示例代码信息
     * 
     * @param id 示例代码主键
     * @return 结果
     */
    @Override
    public int deleteDemoById(Long id)
    {
        return baseMapper.deleteDemoById(id);
    }
}
