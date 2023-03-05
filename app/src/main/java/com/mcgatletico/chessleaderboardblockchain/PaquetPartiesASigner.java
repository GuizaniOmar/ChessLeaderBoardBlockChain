package com.mcgatletico.chessleaderboardblockchain;

class PaquetPartiesASigner extends Paquet {
    private String msg;

    public PaquetPartiesASigner(String msg) {
        super(PAQUETPartiesASIGNER);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }


}
