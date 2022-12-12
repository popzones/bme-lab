package main

import (
	"bitcoin/entity"
	"encoding/json"
	"fmt"
	"os/exec"
	"strconv"
	"strings"
	"sync"
	"time"

	"github.com/jinzhu/gorm"
	_ "github.com/jinzhu/gorm/dialects/mysql"
)

var getrawmempoolShell = "cd /home/ubuntu/bitcoin_source/bitcoin-23.0/src;./bitcoin-cli getrawmempool true"
var memPoolMap sync.Map
var getBestBlockHashShell = "cd /home/ubuntu/bitcoin_source/bitcoin-23.0/src;./bitcoin-cli getbestblockhash"
var bestBlockHash string
var BestBlockHeight string
var wg sync.WaitGroup
var db *gorm.DB

func main() {
	fmt.Println("***************************start***************************")
	db = getMysqlCoon()
	wg.Add(3)
	go queryMemoryPool()
	go queryTheInfoOfBeshBlock()
	go log()
	wg.Wait()
}

func getBestBlockHash() string {
	b, _ := exec.Command("bash", "-c", getBestBlockHashShell).CombinedOutput()
	bestBlockHash = string(b)
	return bestBlockHash
}

func getMysqlCoon() *gorm.DB {
	db, err := gorm.Open("mysql", "root:root@(127.0.0.1:3306)/btc?charset=utf8mb4&parseTime=True&loc=Local")
	if err != nil {
		panic(err)
	}
	return db
}

func log() {
	for {
		//检查超过两个块但仍未确认的交易
		number := 0
		memPoolMap.Range(func(key, value interface{}) bool {
			number++
			tx := value.(entity.Txinfo)
			if tx.Confirmations == false {
				//计算确认k个块BestBlockHeight -t.Height
				BestBlockHeightInt, _ := strconv.Atoi(BestBlockHeight)
				HeightInt, _ := strconv.Atoi(tx.Height)
				tx.HowManyBlocks = BestBlockHeightInt - HeightInt
				if tx.HowManyBlocks > 2 {
					timeStr := time.Now().Format("2006-01-02 15:04:05")
					fmt.Println(timeStr + "----------------" + "txid: " + tx.Txid + "起始高度: " + tx.Height + "当前高度: " + BestBlockHeight + " 已超过两个块未确认" + "差距为：" + strconv.Itoa(tx.HowManyBlocks))
				}
			}
			return true
		})
		timeStr := time.Now().Format("2006-01-02 15:04:05")
		fmt.Println(timeStr + "----------------" + "当前内存池大小: " + strconv.Itoa(number))
		time.Sleep(60 * time.Second)
	}
}
func queryTheInfoOfBeshBlock() {
	for {
		s := getBestBlockHash()
		getbestblockhashInfoShell := "cd /home/ubuntu/bitcoin_source/bitcoin-23.0/src;./bitcoin-cli getblock " + "\"" + s + "" + "\""
		getbestblockhashInfoShell = strings.Replace(getbestblockhashInfoShell, "\n", "", -1)
		c := exec.Command("bash", "-c", getbestblockhashInfoShell)
		output, _ := c.CombinedOutput()
		anyMap := make(map[string]interface{}, 0)
		b := string(output)
		if err := json.Unmarshal([]byte(b), &anyMap); err != nil {
			continue
		}
		height := anyMap["height"].(float64)
		tx := anyMap["tx"]
		txArray, ok := tx.([]interface{})
		BestBlockHeight = strconv.FormatFloat(height, 'f', -1, 32)
		if ok {
			for _, v := range txArray {
				txid := v.(string)
				if t, ok := memPoolMap.Load(txid); ok {
					t := t.(entity.Txinfo)
					t.Confirmations = true
					t.BestBlockHeight = strconv.FormatFloat(height, 'f', -1, 32)
					//计算确认k个块BestBlockHeight -t.Height
					BestBlockHeightInt, _ := strconv.Atoi(BestBlockHeight)
					HeightInt, _ := strconv.Atoi(t.Height)
					t.HowManyBlocks = BestBlockHeightInt - HeightInt
					d := db.Create(&t)
					if d.Error != nil {
						fmt.Println(d.Error)
					}
					//清理内存池
					memPoolMap.Delete(txid)
				}
			}
		}
		time.Sleep(15 * time.Second)
	}
}

// queryMemoryPool  get txid, baseFee, height which can be stored in a map
func queryMemoryPool() error {
	//15s query once
	for {
		c := exec.Command("bash", "-c", getrawmempoolShell)
		var baseFee string
		output, _ := c.CombinedOutput()
		anyMap := make(map[string]interface{}, 0)
		if err := json.Unmarshal([]byte(output), &anyMap); err != nil {
			continue
		}
		for key, value := range anyMap {
			v, ok := value.(map[string]interface{})
			if ok {
				height := v["height"].(float64)
				txid := key
				feeMap := v["fees"]

				v, ok := feeMap.(map[string]interface{})
				if ok {
					if v["base"] != nil {
						baseFee = strconv.FormatFloat(v["base"].(float64), 'f', -1, 32)

					}
				}
				var Txinfo = &entity.Txinfo{}
				Txinfo.Txid = txid
				Txinfo.BaseFee = baseFee
				Txinfo.Height = strconv.FormatFloat(height, 'f', -1, 32)
				memPoolMap.LoadOrStore(txid, *Txinfo)
			}
		}
		time.Sleep(15 * time.Second)
	}
}
