package com.ciot.deliverywear.bean;


/**
 * 大端字节序
 *
 * @description: sros 传输主bean
 * @author: zhenglong.zhu
 * @update: 2018/11/27
 */
public class ProtocolBean {

    /**
     * 协议头
     * 4个字节
     */
    private final int head = 0xFFFEEEEF;
    /**
     * 发出请求为0，发出应答填1  1个字节
     */
    private byte qa;
    /**
     * 序列号 4个字节
     */
    private int seq;
    /**
     * 配件类型 2个字节
     */
    private short type;
    /**
     * 命令编号 2个字节
     */
    private short cmd;

    /**
     * 平台版本号 2个字节
     */
    private short ver;

    /**
     * 连接标识，如果报文为加密状态，0x00(明文协议)，0x01(des),0x02(3des)   2个字节
     */
    private short cflag;
    /**
     * 保留字段   2个字节
     */
    private short rflag;
    /**
     * json 长度   4个字节
     */
    private int len;
    /**
     * json   string类型，UTF-8
     */
    private Object body;

    public int getHead() {
        return head;
    }


    public byte getQa() {
        return qa;
    }

    public void setQa(byte qa) {
        this.qa = qa;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public short getCmd() {
        return cmd;
    }

    public void setCmd(short cmd) {
        this.cmd = cmd;
    }

    public short getVer() {
        return ver;
    }

    public void setVer(short ver) {
        this.ver = ver;
    }

    public short getCflag() {
        return cflag;
    }

    public void setCflag(short cflag) {
        this.cflag = cflag;
    }

    public short getRflag() {
        return rflag;
    }

    public void setRflag(short rflag) {
        this.rflag = rflag;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "ProtocolBean{" +
                "head=" + head +
                ", qa=" + qa +
                ", seq=" + seq +
                ", cmd=" + cmd +
                ", ver=" + ver +
                ", cflag=" + cflag +
                ", rflag=" + rflag +
                ", len=" + len +
                ", body=" + body +
                '}';
    }
}
