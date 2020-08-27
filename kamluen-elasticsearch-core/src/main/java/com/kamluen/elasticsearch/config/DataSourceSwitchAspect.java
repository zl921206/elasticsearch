package com.kamluen.elasticsearch.config;

import com.kamluen.elasticsearch.db.DbContextHolder;
import com.kamluen.elasticsearch.enums.DBTypeEnum;
import com.kamluen.elasticsearch.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(-100) //这是为了保证AOP在事务注解之前生效,Order的值越小,优先级越高
@Slf4j
public class DataSourceSwitchAspect {

    @Pointcut("execution(* com.kamluen.elasticsearch.dao.mktinfo..*.*(..)) || execution(* com.kamluen.elasticsearch.service.mktinfo..*.*(..))")
    private void mktinfoAspect() {
    }

    @Pointcut("execution(* com.kamluen.elasticsearch.dao.kamluen..*.*(..)) || execution(* com.kamluen.elasticsearch.service.kamluen..*.*(..))")
    private void kamluenAspect() {
    }
    @Pointcut("execution(* com.kamluen.elasticsearch.dao.strategy..*.*(..)) || execution(* com.kamluen.elasticsearch.service.strategy..*.*(..))")
    private void strategyAspect(){
    }

    @Before( "mktinfoAspect()" )
    public void mktinfo() {
        if(StringUtils.isEmpty(DbContextHolder.getDbType()) || !DbContextHolder.getDbType().equals(DBTypeEnum.mktinfo.getValue())){
            log.info("=============================================================================================");
            log.info("切换到 mktinfo 数据源......");
            DbContextHolder.setDbType(DBTypeEnum.mktinfo);
        }
    }

    @Before("kamluenAspect()" )
    public void kamluen() {
        if(StringUtils.isEmpty(DbContextHolder.getDbType()) || !DbContextHolder.getDbType().equals(DBTypeEnum.kamluen.getValue())) {
            log.info("=============================================================================================");
            log.info("切换到 kamluen 数据源......");
            DbContextHolder.setDbType(DBTypeEnum.kamluen);
        }
    }
    @Before("strategyAspect()" )
    public void strategy(){
        if(StringUtils.isEmpty(DbContextHolder.getDbType()) || !DbContextHolder.getDbType().equals(DBTypeEnum.strategy.getValue())) {
            log.info("=============================================================================================");
            log.info("切换到 strategy 数据源......");
            DbContextHolder.setDbType(DBTypeEnum.strategy);
        }
    }
}
