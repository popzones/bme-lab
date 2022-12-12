package hu.bme.bitcoin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsConfirmation {
    private Integer id;
    private String txID;
    private String nowBestBlockHeight;
    private String confirmedBlockHeight;
    private String txFee;
}
