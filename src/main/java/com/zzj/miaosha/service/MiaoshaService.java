package com.zzj.miaosha.service;

import com.zzj.miaosha.domain.*;
import com.zzj.miaosha.redis.MiaoshaKey;
import com.zzj.miaosha.redis.RedisService;
import com.zzj.miaosha.util.MD5Util;
import com.zzj.miaosha.util.UUIDUtil;
import com.zzj.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class MiaoshaService {

    //在service中不提倡引入别的dao，但能引用别的service
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MiaoShaUser miaoShaUser, GoodsVo goods) {
        //减库存、下订单、写入秒杀订单，这三种应该同时成功同时失败，所以使用事务
        boolean success = goodsService.reduceStock(goods);

//        System.out.println("减了一次库存");
        if(success){
            return orderService.createOrder(miaoShaUser, goods);
        }else{//没有库存意味着秒杀失败，然后这里在缓存做一个标记，等客户端轮询时会用到
            setGoodsOver(goods.getId());
            return null;
        }

    }


    //返回秒杀结果
    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoShaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        if(order != null){//秒杀成功
            return  order.getOrderId();
        }else{//不成功判断是否是库存为0
            boolean isOver = getGoodsOver(goodsId);
            if(isOver){
                return -1;
            }else{
                return 0;
            }
        }

    }


    private void setGoodsOver(Long goodsId) {

        redisService.set(MiaoshaKey.isGoodsOver,""+goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        //判断缓存中是否存在isGoodsOver的key，存在就是买完了。
        return redisService.exists(MiaoshaKey.isGoodsOver,""+goodsId);
    }

    public String createMiaoshaPath(MiaoShaUser miaoShaUser, long goodsId) {
        if(miaoShaUser == null || goodsId <= 0){
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(MiaoshaKey.getMiaoshaPath, ""+miaoShaUser.getId()+"_"+goodsId, str);
        return str;
    }

    public boolean checkPath(MiaoShaUser miaoShaUser, long goodsId, String path) {
        if(miaoShaUser == null || path == null){
            return false;
        }
        String pathOld = redisService.get(MiaoshaKey.getMiaoshaPath,
                "" + miaoShaUser.getId() + "_" + goodsId, String.class);
        return path.equals(pathOld);

    }


    public BufferedImage createVerifyCode(MiaoShaUser miaoShaUser, long goodsId) {
        if(miaoShaUser == null || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, miaoShaUser.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     *做加减乘来获得验证码
     */
    private static char[] ops = new char[]{'+','-','*'};
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = "" + num1 + op1 + num2 + op2 + num3;
        return exp;

    }

    /**
     * 验证验证码
     */
    public boolean checkVerifyCode(MiaoShaUser miaoShaUser, long goodsId, int verifyCode) {
        if(miaoShaUser == null || goodsId <=0) {
            return false;
        }
        Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCode,
                miaoShaUser.getId() + "," + goodsId, Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0){
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode,
                miaoShaUser.getId() + "," + goodsId);
        return true;

    }
}
