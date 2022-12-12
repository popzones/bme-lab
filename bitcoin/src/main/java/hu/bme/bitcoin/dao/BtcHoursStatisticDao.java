package hu.bme.bitcoin.dao;

import hu.bme.bitcoin.entity.HoursStatistic;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BtcHoursStatisticDao {
    //insert
    int insertBtcHoursStatistic(HoursStatistic hoursStatistic);
}
