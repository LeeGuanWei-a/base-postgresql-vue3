package com.basic.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.basic.workflow.domain.WfFormCategory;
import com.basic.workflow.mapper.WfFormCategoryMapper;
import com.basic.workflow.service.IWfFormCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 鑷畾涔夊崟鍒嗙被Service涓氬姟灞傚鐞?
 *
 * @author basic
 * @date 2024-03-27
 */
@RequiredArgsConstructor
@Service
public class WfFormCategoryServiceImpl extends ServiceImpl<WfFormCategoryMapper, WfFormCategory> implements IWfFormCategoryService
{
    private final WfFormCategoryMapper baseMapper;

    /**
     * @return List<WfFormCategory> 甯﹀瓙鍒嗙被鐨勯《绾у垎绫诲垪琛?
     * @description 鏌ヨ鑷畾涔夎〃鍗曞垎绫绘爲锛屽厛鍙栭《绾у垎绫诲啀閫愪釜濉厖鍏跺瓙鍒嗙被
     * @params [wfFormCategory] 鑷畾涔夎〃鍗曞垎绫绘煡璇㈠璞★紝褰撳墠瀹炵幇鏈弬涓庤繃婊ゆ潯浠?
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public List<WfFormCategory> selectWfFormCategoryList(WfFormCategory wfFormCategory)
    {
        WfFormCategory params = new WfFormCategory();
        params.setParent("0");
        List<WfFormCategory> list = baseMapper.selectWfFormCategoryList(params);
        if(list.size()>0){
            for(WfFormCategory item : list){
                params.setParent(item.getId());
                List<WfFormCategory> children = selectWfFormCategoryChildrenList(params);
                item.setChildren(children);
            }
        }
        return list;
    }

    /**
     * @return List<WfFormCategory> 甯﹀瓙鍒嗙被鐨勫垎绫诲垪琛紝鏃犱笅绾ф椂 children 涓虹┖
     * @description 鎸夌埗鍒嗙被 ID 閫掑綊鏌ヨ瀛愬垎绫诲苟閫愮骇濉厖 children
     * @params [params] 鎼哄甫鐖跺垎绫?ID 鐨勬煡璇㈡潯浠?
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public List<WfFormCategory> selectWfFormCategoryChildrenList(WfFormCategory params){
        List<WfFormCategory> list = baseMapper.selectWfFormCategoryList(params);
        if(list.size()>0){
            for(WfFormCategory item : list){
                WfFormCategory param = new WfFormCategory();
                param.setParent(item.getId());
                List<WfFormCategory> children = selectWfFormCategoryChildrenList(param);
                item.setChildren(children);
            }
        }
        return list;
    }

    /**
     * @description 鏂板鑷畾涔夎〃鍗曞垎绫伙紝鐩存帴淇濆瓨浼犲叆鐨勫垎绫诲璞?
     * @params [wfFormCategory] 寰呮柊澧炵殑鑷畾涔夎〃鍗曞垎绫?
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public void insertWfFormCategory(WfFormCategory wfFormCategory)
    {
        this.save(wfFormCategory);
    }

    /**
     * @description 淇敼鑷畾涔夎〃鍗曞垎绫伙紝涓婚敭宸插瓨鍦ㄥ垯鏇存柊銆佷笉瀛樺湪鍒欐柊澧?
     * @params [wfFormCategory] 寰呬慨鏀圭殑鑷畾涔夎〃鍗曞垎绫?
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public void updateWfFormCategory(WfFormCategory wfFormCategory)
    {
        this.saveOrUpdate(wfFormCategory);
    }

    /**
     * @return boolean 鎵ц瀹屾垚鍚庡浐瀹氳繑鍥?true
     * @description 鎸変富閿泦鍚堥€愪釜鍒犻櫎鑷畾涔夎〃鍗曞垎绫伙紝闆嗗悎涓虹┖鏃朵笉鍋氫换浣曞垹闄?
     * @params [ids] 闇€瑕佸垹闄ょ殑鑷畾涔夎〃鍗曞垎绫讳富閿泦鍚?
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public boolean deleteWfFormCategoryByIds(List<String> ids)
    {
        if (CollUtil.isNotEmpty(ids)) {
            for (String id : ids) {
                baseMapper.deleteById(id);
            }
        }
        return true;
    }
}
