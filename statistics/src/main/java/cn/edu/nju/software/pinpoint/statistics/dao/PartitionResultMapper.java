package cn.edu.nju.software.pinpoint.statistics.dao;

import cn.edu.nju.software.pinpoint.statistics.entity.PartitionResult;
import cn.edu.nju.software.pinpoint.statistics.entity.PartitionResultExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PartitionResultMapper {
    int countByExample(PartitionResultExample example);

    int deleteByExample(PartitionResultExample example);

    int deleteByPrimaryKey(String id);

    int insert(PartitionResult record);

    int insertSelective(PartitionResult record);

    List<PartitionResult> selectByExample(PartitionResultExample example);

    PartitionResult selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") PartitionResult record, @Param("example") PartitionResultExample example);

    int updateByExample(@Param("record") PartitionResult record, @Param("example") PartitionResultExample example);

    int updateByPrimaryKeySelective(PartitionResult record);

    int updateByPrimaryKey(PartitionResult record);
}