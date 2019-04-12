package com.myapplication;

/**
 * 作者：create by comersss on 2019/1/29 09:50
 * 邮箱：904359289@qq.com
 */
public class PostBean {
    private String userid;//用户编号
    private String cardid;//银行卡号
    private String sendtime;//发送时间
    private String content;//短信内容
    private String sign;//签名


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
