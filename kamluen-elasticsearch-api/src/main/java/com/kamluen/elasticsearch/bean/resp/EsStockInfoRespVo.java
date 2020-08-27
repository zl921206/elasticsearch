package com.kamluen.elasticsearch.bean.resp;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author zhanglei
 *  股票基本信息ES检索响应实体类
 * @date 2018-11-02
 */
public class EsStockInfoRespVo implements Serializable {

	/** [股票基本信息检索] */
	private static final long serialVersionUID = -1256242655693685993L;

	/**
	 * 返回结果包装
	 */
	List<Map<String, Object>> resultInfo;

	public List<Map<String, Object>> getResultInfo() {
		return resultInfo;
	}

	public void setResultInfo(List<Map<String, Object>> resultInfo) {
		this.resultInfo = resultInfo;
	}
}
