package cn.edu.nju.software.pinpoint.statistics.dao;

import cn.edu.nju.software.pinpoint.statistics.entity.DynamicAnalysisInfo;
import cn.edu.nju.software.pinpoint.statistics.entity.DynamicAnalysisInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DynamicAnalysisInfoMapper {
    int countByExample(DynamicAnalysisInfoExample example);

    int deleteByExample(DynamicAnalysisInfoExample example);

    int deleteByPrimaryKey(String id);

    int insert(DynamicAnalysisInfo record);

    int insertSelective(DynamicAnalysisInfo record);

    List<DynamicAnalysisInfo> selectByExample(DynamicAnalysisInfoExample example);

    DynamicAnalysisInfo selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DynamicAnalysisInfo record, @Param("example") DynamicAnalysisInfoExample example);

    int updateByExample(@Param("record") DynamicAnalysisInfo record, @Param("example") DynamicAnalysisInfoExample example);

    int updateByPrimaryKeySelective(DynamicAnalysisInfo record);

    int updateByPrimaryKey(DynamicAnalysisInfo record);
}