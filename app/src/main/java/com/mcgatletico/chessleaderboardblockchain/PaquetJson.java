package com.mcgatletico.chessleaderboardblockchain;

class PaquetJson extends Paquet {
    private String msg;

    public PaquetJson(String msg) {
        super(Paquet.JSON);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }


}
