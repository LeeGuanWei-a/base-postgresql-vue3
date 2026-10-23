package com.basic.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.basic.workflow.domain.ExtendProcinst;

import java.util.List;

/**
 * @description 娴佺▼瀹炰緥涓氬姟鎷撳睍 Service 鎺ュ彛锛岀淮鎶ゆ祦绋嬪疄渚嬩笌涓氬姟鏁版嵁鐨勫叧鑱旇褰曞強鍏跺巻鍙叉暟鎹?
 * @author yanbin
 * @date 2023/10/26
 */
public interface IWfExtendProcinstService extends IService<ExtendProcinst> {
    /**
     * @description 淇濆瓨娴佺▼瀹炰緥鍚姩鏃剁殑涓氬姟鎷撳睍淇℃伅锛屽苟鍚屾鍐欎竴浠藉巻鍙茶褰?
     * @params [extendProcinst] 娴佺▼瀹炰緥鐨勪笟鍔℃嫇灞曚俊鎭?
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    void saveExtendProcinstAndHis(ExtendProcinst extendProcinst);

    /**
     * @return ExtendProcinst 璇ュ疄渚嬪惎鍔ㄦ椂淇濆瓨鐨勬嫇灞曚俊鎭紝鏃犺褰曟椂涓?null
     * @description 鎸夋祦绋嬪疄渚?ID 鏌ヨ瀹炰緥鍚姩鏃朵繚瀛樼殑涓氬姟鎷撳睍淇℃伅
     * @params [processInstanceId] 娴佺▼瀹炰緥 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    ExtendProcinst selectProcInstanId(String processInstanceId);

    /**
     * @description 鎸夋祦绋嬪疄渚?ID 闆嗗悎鎵归噺鍒犻櫎杩欎簺瀹炰緥鐨勪笟鍔℃嫇灞曚俊鎭?
     * @params [procInsIds] 瑕佹竻鐞嗘嫇灞曚俊鎭殑娴佺▼瀹炰緥 ID 闆嗗悎
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    void delProcessExtend(List<String> procInsIds);
}
