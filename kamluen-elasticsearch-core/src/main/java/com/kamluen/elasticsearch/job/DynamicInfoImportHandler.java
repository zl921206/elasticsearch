package com.kamluen.elasticsearch.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kamluen.elasticsearch.constant.ElasticConstant;
import com.kamluen.elasticsearch.entity.ElasticsearchVO.EsPtfNoteInfo;
import com.kamluen.elasticsearch.entity.PtfNoteInfo;
import com.kamluen.elasticsearch.entity.UserInfo;
import com.kamluen.elasticsearch.service.kamluen.DynamicInfoService;
import com.kamluen.elasticsearch.service.ElasticsearchService;
import com.kamluen.elasticsearch.service.kamluen.UserInfoService;
import com.kamluen.elasticsearch.utils.StringUtils;
import com.kamluen.security.SecurityKey;
import com.kamluen.security.util.IDTransUtil;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * 包: com.kamluen.elasticsearch.job
 * 开发者: LQW
 * 开发时间: 2019/5/20
 * 功能：动态信息数据导入ES
 */
@JobHandler("dynamicInfoImportHandler")
@Component
public class DynamicInfoImportHandler extends IJobHandler {

    private static Logger logger = LoggerFactory.getLogger(DynamicInfoImportHandler.class);

    @Resource
    private RestHighLevelClient restHighLevelClient;
    @Resource
    private ElasticsearchService elasticsearchService;
    @Resource
    private DynamicInfoService dynamicInfoService;
    @Resource
    private UserInfoService userInfoService;

    @Value("${oss.prefix}")
    private String prefix;
    @Value("${oss.user.suffix}")
    private String suffix;
    @Value("${oss.wrong.data}")
    private String writeKey;
    @Value("${oss.dynamic.suffix}")
    private String dynamic_suffix;
    @Value("${oss.default.image}")
    private String defaultImage;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("DynamicInfoImportHandler 动态信息数据导入elasticsearch任务开始执行......");
        logger.info("DynamicInfoImportHandler 动态信息数据导入elasticsearch任务开始执行......");

        selectDynamicInformationToImportIntoES();

        XxlJobLogger.log("DynamicInfoImportHandler 动态信息数据导入elasticsearch任务执行结束......");
        logger.info("DynamicInfoImportHandler 动态信息数据导入elasticsearch任务执行结束......");
        return SUCCESS;
    }

    /**
     * 选择要导入的动态信息到ES
     */
    private void selectDynamicInformationToImportIntoES() {
        logger.info("进入方法：selectDynamicInformationToImportIntoES...... start... ");
        // 先删除原有索引（因为es暂时没有提供清除所有doc的api），再创建索引并指定分词器，最后倒入数据
        try {
            restHighLevelClient.indices().delete(new DeleteIndexRequest(ElasticConstant.DYNAMIC_INDEX));
            restHighLevelClient.indices().create(new CreateIndexRequest(ElasticConstant.DYNAMIC_INDEX).settings(Settings.builder().put("analysis.analyzer.default.type", "ik_max_word")));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("删除索引异常,{}的索引异常,异常信息为{}",ElasticConstant.DYNAMIC_INDEX,e.getMessage());
        }
        EsPtfNoteInfo eNInfo = new EsPtfNoteInfo();
        // 每次查询导入20000条
        Page<PtfNoteInfo> page = new Page<PtfNoteInfo>(1, 20000);
        QueryWrapper<PtfNoteInfo> wrapper = new QueryWrapper<>();
//        Columns columns = Columns.create();
//        columns.column("ptf_note_id")
//                .column("user_id")
//                .column("note_type")
//                .column("bus_content")
//                .column("is_status")
//                .column("note_share_num")
//                .column("note_read_num")
//                .column("update_time");
//        wrapper.setSqlSelect(columns);
        wrapper.select("ptf_note_id","user_id","note_type","bus_content","is_status","update_time");
        //只查询有效的动态信息
        wrapper.eq("is_status",1);
        IPage<PtfNoteInfo> ptfNoteInfo = dynamicInfoService.page(page, wrapper);
        BulkRequest request = new BulkRequest();
        request.timeout(TimeValue.timeValueMinutes(5));
        if (null != ptfNoteInfo && ptfNoteInfo.getRecords().size() > 0){
            for (PtfNoteInfo ptfNote : ptfNoteInfo.getRecords()) {
                //将bus_content转换为map字段
                Map<String,Object> map = (Map<String, Object>) JSONObject.parse(ptfNote.getBusContent());
                judgeValueByTheKeyOrSetting(ptfNote,eNInfo,map);
                //将数据转换为json格式写入
                String str = JSONObject.toJSONString(eNInfo);
                request.add(new IndexRequest(ElasticConstant.DYNAMIC_INDEX,ElasticConstant.DYNAMIC_TYPE,eNInfo.getPtfNoteId().toString())
                        .source(str, XContentType.JSON));
            }
        }else {
            logger.info("查询数据库用户信息数据为空......");
            return;
        }
        logger.info("动态信息数据封装完毕,准备执行插入请求,开始执行插入......");
        logger.info("动态信息数据封装完毕,封装结果:{}",request);
        try {
            BulkResponse bulk = restHighLevelClient.bulk(request);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("动态信息数据插入elasticSearch产生IO异常,异常信息: {}",e.getMessage());
        }catch (ElasticsearchException es){
            es.getDetailedMessage();
            logger.error("动态信息数据插入elasticSearch失败,失败原因: {}",es.getMessage());
        }
        logger.info("动态信息数据插入ElasticSearch完成......");
        logger.info("结束方法：selectDynamicInformationToImportIntoES...... end... ");
    }

    private void judgeValueByTheKeyOrSetting(PtfNoteInfo ptfNote, EsPtfNoteInfo eNInfo, Map<String, Object> map) {
        try {
            UserInfo userInfo = userInfoService.getById(ptfNote.getUserId());
            if (StringUtils.isNotNull(userInfo)){
                dynamicCenterBasedUserAvatars(eNInfo, userInfo);
            }else {
                eNInfo.setUserIcon(defaultImage);
                eNInfo.setUserName("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询用户信息数据异常,异常信息{}",e.getMessage());
        }
        eNInfo.setPtfNoteId(ptfNote.getPtfNoteId());
        eNInfo.setNoteType(ptfNote.getNoteType());
        eNInfo.setBusContent(ptfNote.getBusContent());
        eNInfo.setIsLimit(0);
        eNInfo.setIsReal(0);
        eNInfo.setPerm(0);
        eNInfo.setNoteShareNum(ptfNote.getNoteShareNum());
        eNInfo.setNoteReadNum(ptfNote.getNoteReadNum());
        //将用户ID转换成longID
        eNInfo.setUserId(ptfNote.getUserId());
        eNInfo.setUIdLong(IDTransUtil.encodeId(Long.parseLong(ptfNote.getUserId().toString()), SecurityKey.ID_KEY));
        eNInfo.setUpdateTime(ptfNote.getUpdateTime());
        try {
            judgeValueByTheKey(eNInfo, map);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("在bus_content转换为map后取值出现异常,异常信息{}",e.getMessage());
        }
    }

    /**
     *  基于用户头像
     * @param eNInfo    ES中的动态信息
     * @param userInfo  用户信息
     */
    private void dynamicCenterBasedUserAvatars(EsPtfNoteInfo eNInfo, UserInfo userInfo) {
        if (StringUtils.isNotNull(userInfo)) {
            eNInfo.setUserName(userInfo.getNickName());
        } else {
            eNInfo.setUserName("");
        }
        if (StringUtils.isNotNull(userInfo.getUserIcon())) {
            if (userInfo.getUserIcon().contains("https")){
                eNInfo.setUserIcon(userInfo.getUserIcon());
            }else if (userInfo.getUserIcon().contains(writeKey)){
                String url = userInfo.getUserIcon().substring(writeKey.length());
                eNInfo.setUserIcon(prefix + suffix + url);
            }else if (userInfo.getUserIcon().contains(suffix)){
                eNInfo.setUserIcon(prefix + userInfo.getUserIcon());
            }else {
                eNInfo.setUserIcon(prefix + suffix + userInfo.getUserIcon());
            }
        } else {
            eNInfo.setUserIcon(defaultImage);
        }
    }
    /**
     *  根据键判断值
     * @param eNInfo  ES中的动态信息
     * @param map     json转换为map后的对象
     */
    private void judgeValueByTheKey(EsPtfNoteInfo eNInfo, Map<String, Object> map) {
        if (StringUtils.isNotNull(map.get("atIds"))){
            eNInfo.setAtIds((List<Integer>) map.get("atIds"));
        }else {
            eNInfo.setAtIds(new ArrayList<>());
        }

        if (StringUtils.isNotNull(map.get("assets"))){
            eNInfo.setAssets((List<String>) map.get("assets"));
        }else {
            eNInfo.setAssets(new ArrayList<>());
        }

        if (StringUtils.isNotNull(map.get("artId"))){
            eNInfo.setArtId(IDTransUtil.encodeId(Long.valueOf(map.get("artId").toString()), SecurityKey.ID_KEY).intValue());
        }else {
            //没有的全部为-1
            eNInfo.setArtId(-1);
        }
        if (StringUtils.isNotNull(map.get("categoryName"))){
            eNInfo.setCategoryName(map.get("categoryName").toString());
        }else {
            eNInfo.setCategoryName("");
        }
        if (StringUtils.isNotNull(map.get("content"))){
            eNInfo.setContent(map.get("content").toString());
        }else {
            eNInfo.setContent("");
        }
        if (StringUtils.isNotNull(map.get("noteTitle"))){
            eNInfo.setNoteTitle(map.get("noteTitle").toString());
        }else {
            eNInfo.setNoteTitle("");
        }
        if (StringUtils.isNotNull(map.get("title"))){
            eNInfo.setTitle(map.get("title").toString());
        }else {
            eNInfo.setTitle("");
        }
        if (StringUtils.isNotNull(map.get("newsImg"))){
            eNInfo.setNewsImg(map.get("newsImg").toString());
        }else {
            eNInfo.setNewsImg("");
        }
        if (StringUtils.isNotNull(map.get("urls"))) {
            List<String> address = new ArrayList<>();
            JSONArray array = JSONArray.parseArray(map.get("urls").toString());
            for (Object url : array) {
                String dynamicInformation = determineWhetherTheUrlIsCorrectBasedOnTheDynamicInformation(String.valueOf(url));
                address.add(dynamicInformation);
            }
            eNInfo.setUrls(address);
        } else {
            eNInfo.setUrls(new ArrayList<>());
        }
        if (StringUtils.isNotNull(map.get("type"))){
            eNInfo.setType(Integer.valueOf(map.get("type").toString()));
        }else {
            eNInfo.setType(0);
        }
    }

    private String determineWhetherTheUrlIsCorrectBasedOnTheDynamicInformation(String url) {
        String dynamicUrl = "";
        if (url.contains(ElasticConstant.DYNAMIC_WRONG_DATA)){
            url = url.substring(ElasticConstant.DYNAMIC_WRONG_DATA.length());
            dynamicUrl = prefix + dynamic_suffix + url;
        }else if (url.contains(dynamic_suffix)){
            dynamicUrl = prefix + url;
        }else if (url.contains(ElasticConstant.DYNAMIC_WRONG_SUFFIX)) {
            url = url.substring(ElasticConstant.DYNAMIC_WRONG_SUFFIX.length());
            dynamicUrl = prefix + dynamic_suffix +  url;
        }else {
            dynamicUrl = prefix + dynamic_suffix + url;
        }
        return dynamicUrl;
    }
}
