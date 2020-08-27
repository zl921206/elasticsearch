package com.kamluen.elasticsearch.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kamluen.elasticsearch.constant.ElasticConstant;
import com.kamluen.elasticsearch.entity.ElasticsearchVO.EsUserInfo;
import com.kamluen.elasticsearch.entity.PtfNoteInfo;
import com.kamluen.elasticsearch.entity.UserInfo;
import com.kamluen.elasticsearch.entity.UserInfoBO;
import com.kamluen.elasticsearch.service.kamluen.UserInfoBOService;
import com.kamluen.elasticsearch.service.kamluen.UserInfoService;
import com.kamluen.elasticsearch.utils.StringUtils;
import com.kamluen.security.SecurityKey;
import com.kamluen.security.util.IDTransUtil;
import com.kamluen.utils.ProtocolUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.poi.ss.formula.functions.Columns;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 包: com.kamluen.elasticsearch.job
 * 开发者: LQW
 * 开发时间: 2019/5/21
 * 功能： 用户信息数据导入ES
 */
@JobHandler("userInfoImportHandler")
@Component
public class UserInfoImportHandler extends IJobHandler {
    private static Logger logger = LoggerFactory.getLogger(UserInfoImportHandler.class);

    @Value("${oss.prefix}")
    private String prefix;
    @Value("${oss.user.suffix}")
    private String suffix;
    @Value("${oss.wrong.data}")
    private String writeKey;
    @Value("${oss.default.image}")
    private String defaultImage;



    @Resource
    private RestHighLevelClient restHighLevelClient;
    @Resource
    private UserInfoBOService userInfoBOService;
    @Resource
    private UserInfoService userInfoService;


    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("UserInfoImportHandler 动态信息数据导入elasticsearch任务开始执行......");
        logger.info("UserInfoImportHandler 动态信息数据导入elasticsearch任务开始执行......");

        selectUserInformationToImportIntoES();

        XxlJobLogger.log("UserInfoImportHandler 动态信息数据导入elasticsearch任务执行结束......");
        logger.info("UserInfoImportHandler 动态信息数据导入elasticsearch任务执行结束......");
        return SUCCESS;
    }

    private void selectUserInformationToImportIntoES() {
        logger.info("进入方法：insertBatchUserInfo...... start... ");
        // 先删除原有索引（因为es暂时没有提供清除所有doc的api），再创建索引并指定分词器，最后倒入数据
        try {
            restHighLevelClient.indices().delete(new DeleteIndexRequest(ElasticConstant.USER_INDEX));
            restHighLevelClient.indices().create(new CreateIndexRequest(ElasticConstant.USER_INDEX).settings(Settings.builder().put("analysis.analyzer.default.type", "ik_max_word")));
        } catch (Exception e) {
            logger.error("删除索引：{}异常，异常信息{}", ElasticConstant.USER_INDEX, e.getMessage());
        }
        EsUserInfo userInfo = new EsUserInfo();
        BulkRequest request = new BulkRequest();
        // 设置超时时间为5分钟
        request.timeout(TimeValue.timeValueMinutes(5));
        Page<UserInfo> page = new Page<UserInfo>(1, 10000);
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
//        Columns column = Columns.create()
//                .column("user_id")
//                .column("nick_name")
//                .column("user_icon")
//                .column("privacy")
//                .column("cell_phone")
//                .column("update_time");
//        wrapper.setSqlSelect(column);
        wrapper.select("user_id","nick_name","user_icon","privacy","cell_phone","update_time");
        IPage<UserInfo> userInfos = userInfoService.page(page, wrapper);
        logger.info("查找到的用户数据集合size:" + userInfos.getRecords().size());
        try {
            if (null != userInfos && userInfos.getRecords().size() > 0) {
                for (UserInfo info : userInfos.getRecords()) {
                    try {
                        judgeIsNull(userInfo, info);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.info("在判断设置中出现异常,异常信息为:{}", e.getMessage());
                    }
                    logger.info("输出用户信息数据：" + JSONObject.toJSONString(userInfo));
                    //将数据转换为json格式写入
                    String str = JSONObject.toJSONString(userInfo);
                    request.add(new IndexRequest(ElasticConstant.USER_INDEX, ElasticConstant.USER_TYPE, String.valueOf(userInfo.getUserId()))
                            .source(str, XContentType.JSON));
                }
            } else {
                logger.info("查询数据库用户信息数据为空......");
                return;
            }
            logger.info("所有用户信息需要插入Elasticsearch中的数据请求包装成功，开始执行插入动作......");
            BulkResponse response = restHighLevelClient.bulk(request);
        } catch (ElasticsearchException e) {
            e.printStackTrace();
            e.getDetailedMessage();
            logger.error("用户信息数据批量插入 Elasticsearch 异常，异常信息：{}", e.getMessage());
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            ex.getLocalizedMessage();
            logger.error("用户信息数据批量插入 Elasticsearch 发生IO异常，异常信息：{}", ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("批量导入用户信息数据集合异常：{}", e.getMessage());
        }
        logger.info("股票基本信息数据批量插入 Elasticsearch 完成......");
        logger.info("结束方法：insertBatchUserInfo...... end... ");
        return;
    }

    private void judgeIsNull(EsUserInfo userInfo, UserInfo info) {
        logger.info("进入 judgeIsNull方法,查询到的数据为{}", info);
        userInfo.setUserId(info.getUserId().toString());
        userInfo.setuIdLong(IDTransUtil.encodeId(Long.parseLong(info.getUserId().toString()), SecurityKey.ID_KEY));
        userInfo.setNickName(info.getNickName());
        try {
            userCenterBasedUserAvatars(userInfo,info);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("用户信息设置数据异常,异常信息{}",e.getMessage());
        }
        userInfo.setPrivacy(info.getPrivacy());
        if (StringUtils.isNotNull(info.getCellPhone())) {
            //手机号解密存储
            userInfo.setCellPhone(ProtocolUtils.getDecryptPhone(info.getCellPhone()));
        } else {
            userInfo.setCellPhone("");
        }
        userInfo.setUpdateTime(info.getUpdateTime());
    }

    private void userCenterBasedUserAvatars(EsUserInfo userInfo, UserInfo info) {
        if (StringUtils.isNotNull(info.getUserIcon())) {
            if (info.getUserIcon().contains("http")){
                userInfo.setUserIcon(info.getUserIcon());
            }else if (info.getUserIcon().contains(writeKey)){
                String url = info.getUserIcon().substring(writeKey.length());
                userInfo.setUserIcon(prefix + suffix + url);
            }else if (info.getUserIcon().contains(suffix)){
                userInfo.setUserIcon(prefix + info.getUserIcon());
            }else {
                userInfo.setUserIcon(prefix + suffix + info.getUserIcon());
            }
        } else {
            userInfo.setUserIcon(defaultImage);
        }
    }
}
