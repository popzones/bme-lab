package hu.bme.bitcoin.entity;

import com.alibaba.fastjson.JSONObject;
import hu.bme.bitcoin.dao.BtcBlockChainInfoDao;
import hu.bme.bitcoin.dao.BtcChainReorgDao;
import hu.bme.bitcoin.dao.BtcHoursStatisticDao;
import hu.bme.bitcoin.utils.ShellUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@EnableAsync
@EnableScheduling
public class BitcoinService {
    @Resource
    BtcBlockChainInfoDao btcBlockChainInfoDao;
    @Resource
    BtcHoursStatisticDao btcHoursStatisticDao;
    @Resource
    BtcChainReorgDao btcChainReorgDao;
    //create a list to storage the block's hash code
    LinkedList<JSONObject> list=new LinkedList();
    HashMap hashMap=new HashMap();
    List<HashMap> memoryList=new ArrayList();
    HashMap<String,StatisticsConfirmation> statisticsConfirmationHashMap=new HashMap<>();
    int currentHeight=0;




    public int getHeightOfTheChain() throws IOException {
        String[] cmd = {"/bin/sh", "-c", "cd /home/ubuntu/bitcoin_source/bitcoin-23.0/src;./bitcoin-cli getblockcount"};
        StringBuffer height = ShellUtils.executeShell(cmd);
        return Integer.valueOf(height.toString());
    }

    public StringBuffer getBlockHash(int height) throws IOException {
        String[] cmd = {"/bin/sh", "-c", "cd /home/ubuntu/bitcoin_source/bitcoin-23.0/src;./bitcoin-cli getblockhash"+" "+height};
        StringBuffer blockHash = ShellUtils.executeShell(cmd);
        return blockHash;
    }
    //storage 6 block's hash code to the list.
    public void initList() throws IOException {
        int heightOfTheChain = getHeightOfTheChain();
        currentHeight=heightOfTheChain;
        for(int i=5;i>=0;i--)
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("blockHeight",heightOfTheChain-i);
            jsonObject.put("blockHashCode",getBlockHash(heightOfTheChain-i));
            list.add(jsonObject);
        }
        System.out.println(list.get(1));
        return;
    }
    public void adjustTheList() throws IOException {
        int heightOfTheChain = getHeightOfTheChain();
        list.remove(0);
        //add new block information into the list.
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("blockHeight",heightOfTheChain);
        jsonObject.put("blockHashCode",getBlockHash(heightOfTheChain));
        list.add(jsonObject);
        currentHeight=heightOfTheChain;
    }

    public void queryTheHashCodeOfTheBlock() throws IOException {
        for (JSONObject jsonObject : list) {
            int blockHeight =  jsonObject.getIntValue("blockHeight");
            String blockHashCode = jsonObject.getString("blockHashCode");
            StringBuffer newBlockHashCode = getBlockHash(blockHeight);
            //compare
            if((newBlockHashCode.toString()).equals(blockHashCode))
            {
                log.info("height: "+blockHeight+" hashCode: "+blockHashCode+" was not Reorganized");
            }
            else{
                log.info("height:"+blockHeight+"has been Reorganized"+"origin hashCode is "+blockHashCode+"the new hashCode is "+newBlockHashCode);
                btcChainReorgDao.insertBtcChainReorg(blockHashCode,newBlockHashCode.toString(), new Date().toLocaleString());
            }
        }
    }

    //compare the origin and new block's hashcode of every block's
    public void execute() throws IOException {
        //judge whether there is a new block was created.

        if(getHeightOfTheChain()>currentHeight)
        {
            //delete the first block information of the list.
            adjustTheList();
            queryTheHashCodeOfTheBlock();
        }
        else {
            queryTheHashCodeOfTheBlock();
        }
    }


    //定时任务，5秒一次

    public void queryMemoryPool() throws IOException {
        String[] cmd = {"/bin/sh", "-c", "cd /home/ubuntu/bitcoin_source/bitcoin-23.0/src;./bitcoin-cli getrawmempool true"};
        StringBuffer poolInfo = ShellUtils.executeShell(cmd);
        JSONObject jsonObject = JSONObject.parseObject(String.valueOf(poolInfo));
        //get tx id;
        Set<String> strings = jsonObject.keySet();
        for (String string : strings) {
            StatisticsConfirmation statisticsConfirmation = new StatisticsConfirmation();
            statisticsConfirmation.setTxID(string);
            //get start time;
            //if(statisticsConfirmation.getTxID().equals("94241e050b515a7c8358f9e2e849aa1edf7fd9dbd2883b4909453c9761c20004"))
            //{
                String time = jsonObject.getJSONObject(string).getString("height");
                if(time.equals("766540"))
                {
                  //  System.out.println(time+"---"+statisticsConfirmation.getTxID());
                }

            //}

            //get tx fee;
            String txFee = jsonObject.getJSONObject(string).getJSONObject("fees").getString("base");
            statisticsConfirmation.setTxFee(txFee);
            if(!statisticsConfirmationHashMap.containsKey(statisticsConfirmation.getTxID()))
            {
                statisticsConfirmationHashMap.put(statisticsConfirmation.getTxID(),statisticsConfirmation);
            }

        }
        System.out.println(statisticsConfirmationHashMap.size());
    }
    @Scheduled(cron = "0/1 * * * * ?")
    public void statisticConfirmation() throws IOException {
        queryMemoryPool();
        log.info("-----------");
        if(statisticsConfirmationHashMap.size()>0)
        {
            Set<String> strings = statisticsConfirmationHashMap.keySet();
            for (String string : strings) {
                StatisticsConfirmation statisticsConfirmation = statisticsConfirmationHashMap.get(string);
                String txID = statisticsConfirmation.getTxID();
                String[] cmd = {"/bin/sh", "-c", "cd /home/ubuntu/bitcoin_source/bitcoin-23.0/src;./bitcoin-cli gettxout \""+txID+"\" 0"};

                StringBuffer poolInfo = ShellUtils.executeShell(cmd);
                try{
                    Integer confirmations =Integer.valueOf(JSONObject.parseObject(String.valueOf(poolInfo)).getString("confirmations"));

                    if(confirmations ==330)
                    {

                        log.info(String.valueOf(statisticsConfirmation));
                    }
                }
                catch (Exception e){
                    continue;
                }

            }

            }

    }



}













