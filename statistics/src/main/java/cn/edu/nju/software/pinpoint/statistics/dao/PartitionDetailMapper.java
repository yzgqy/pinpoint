package cn.edu.nju.software.pinpoint.statistics.dao;

import cn.edu.nju.software.pinpoint.statistics.entity.PartitionDetail;
import cn.edu.nju.software.pinpoint.statistics.entity.PartitionDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PartitionDetailMapper {
    int countByExample(PartitionDetailExample example);

    int deleteByExample(PartitionDetailExample example);

    int deleteByPrimaryKey(String id);

    int insert(PartitionDetail record);

    int insertSelective(PartitionDetail record);

    List<PartitionDetail> selectByExample(PartitionDetailExample example);

    PartitionDetail selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") PartitionDetail record, @Param("example") PartitionDetailExample example);

    int updateByExample(@Param("record") PartitionDetail record, @Param("example") PartitionDetailExample example);

    int updateByPrimaryKeySelective(PartitionDetail record);

    int updateByPrimaryKey(PartitionDetail record);
}