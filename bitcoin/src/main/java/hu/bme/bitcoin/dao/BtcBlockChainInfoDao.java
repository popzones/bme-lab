package hu.bme.bitcoin.dao;

import hu.bme.bitcoin.entity.BtcBlockChainInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BtcBlockChainInfoDao {
    //插入数据
    int insertBtcBlockChainInfo(BtcBlockChainInfo btcBlockChainInfo);
}
