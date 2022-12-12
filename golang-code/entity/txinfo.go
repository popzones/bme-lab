package entity

type Txinfo struct {
	id                    string
	Txid, BaseFee, Height string
	Confirmations         bool
	BestBlockHeight       string
	HowManyBlocks         int
}
