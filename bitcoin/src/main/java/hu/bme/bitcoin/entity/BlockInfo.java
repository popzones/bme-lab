package hu.bme.bitcoin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class BlockInfo {
    String hash;
    String confirmations;
    String size;
    String strippedsize;
    String weight;
    String version;
    String versionHex;
    String merkleroot;
    String[] tx;
    String time;
    String mediantime;
    String nonce;
    String bits;
    String difficulty;
    String chainwork;
    String nTx;
    String previousblockhash;
    String nextblockhash;



}
