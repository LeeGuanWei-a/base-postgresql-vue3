package com.basic.workflow.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.basic.workflow.domain.WfFormCategory;

/**
 * 鑷畾涔夊崟鍒嗙被Service鎺ュ彛
 *
 * @author basic
 * @date 2024-03-27
 */
public interface IWfFormCategoryService extends IService<WfFormCategory>
{

    /**
     * @return List<WfFormCategory> 鑷畾涔夊崟鍒嗙被闆嗗悎
     * @description 鎸夋煡璇㈡潯浠舵煡璇㈣嚜瀹氫箟鍗曞垎绫诲垪琛?
     * @params [wfFormCategory] 鑷畾涔夊崟鍒嗙被鏌ヨ鏉′欢
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    public List<WfFormCategory> selectWfFormCategoryList(WfFormCategory wfFormCategory);

    /**
     * @return List<WfFormCategory> 瀛愮骇鑷畾涔夊崟鍒嗙被闆嗗悎
     * @description 鎸夋煡璇㈡潯浠舵煡璇㈣嚜瀹氫箟鍗曞垎绫荤殑瀛愮骇鍒楄〃
     * @params [params] 鑷畾涔夊崟鍒嗙被鏌ヨ鏉′欢
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    public List<WfFormCategory> selectWfFormCategoryChildrenList(WfFormCategory params);

    /**
     * @description 鏂板涓€鏉¤嚜瀹氫箟鍗曞垎绫?
     * @params [wfFormCategory] 寰呮柊澧炵殑鑷畾涔夊崟鍒嗙被
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    public void insertWfFormCategory(WfFormCategory wfFormCategory);

    /**
     * @description 鎸変富閿慨鏀逛竴鏉¤嚜瀹氫箟鍗曞垎绫?
     * @params [wfFormCategory] 寰呬慨鏀圭殑鑷畾涔夊崟鍒嗙被
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    public void updateWfFormCategory(WfFormCategory wfFormCategory);

    /**
     * @return boolean 鏄惁鍒犻櫎鎴愬姛
     * @description 鎸変富閿泦鍚堟壒閲忓垹闄よ嚜瀹氫箟鍗曞垎绫?
     * @params [ids] 闇€瑕佸垹闄ょ殑鑷畾涔夊崟鍒嗙被涓婚敭闆嗗悎
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    public boolean deleteWfFormCategoryByIds(List<String> ids);
}
