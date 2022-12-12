package hu.bme.bitcoin.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BtcChainReorgDao {
    //insert
    int insertBtcChainReorg(@Param("oldHashCode")String oldHashCode,@Param("newHashCode")String newHashCode,@Param("time")String time);
}
