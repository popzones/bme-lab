package hu.bme.bitcoin.entity;

import cn.hutool.core.date.DateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BtcBlockChainInfo {
    private Integer id;
    private String chain;
    private String blocks;
    private String headers;
    private String bestBlockHash;
    private String difficulty;
    private Date time;
    private Date medianTime;
    private String verificationProgress;
    private String initialBlockDownload;
    private  String chainWork;
    private  String sizeOnDisk;
    private  String pruned;
    private  String warnings;

}
