package hu.bme.bitcoin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HoursStatistic {
    private Integer id;
    private String newNumber;
    private String recordTime;
}
