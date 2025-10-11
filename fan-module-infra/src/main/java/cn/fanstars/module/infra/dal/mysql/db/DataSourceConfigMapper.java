package cn.fanstars.module.infra.dal.mysql.db;

import cn.fanstars.framework.mybatis.core.mapper.BaseMapperX;
import cn.fanstars.module.infra.dal.dataobject.db.DataSourceConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源配置 Mapper
 *
 * @author 繁星源码
 */
@Mapper
public interface DataSourceConfigMapper extends BaseMapperX<DataSourceConfigDO> {
}
