package com.basic.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.basic.common.annotation.DataSource;
import com.basic.common.constant.Constants;
import com.basic.common.constant.UserConstants;
import com.basic.common.core.redis.RedisCache;
import com.basic.common.core.text.Convert;
import com.basic.common.enums.DataSourceType;
import com.basic.common.exception.ServiceException;
import com.basic.common.utils.StringUtils;
import com.basic.system.domain.SysConfig;
import com.basic.system.domain.SysFiles;
import com.basic.system.mapper.SysConfigMapper;
import com.basic.system.mapper.SysFilesMapper;
import com.basic.system.service.ISysConfigService;
import com.basic.system.service.ISysFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

/**
 * 参数配置 服务层实现
 * 
 * @author basic
 */
@Service
public class SysFilesServiceImpl extends ServiceImpl<SysFilesMapper, SysFiles> implements ISysFilesService
{

}
