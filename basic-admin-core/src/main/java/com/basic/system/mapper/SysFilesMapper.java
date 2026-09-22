package com.basic.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basic.system.domain.SysConfig;
import com.basic.system.domain.SysFiles;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 附件 数据层
 * 
 * @author basic
 */
@Mapper
public interface SysFilesMapper extends BaseMapper<SysFiles>
{

}
