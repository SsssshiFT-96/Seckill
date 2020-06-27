package com.zzj.miaosha.service;

import com.zzj.miaosha.dao.GoodsDao;
import com.zzj.miaosha.domain.Goods;
import com.zzj.miaosha.domain.MiaoShaGoods;
import com.zzj.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(Long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }


    public void reduceStock(GoodsVo goods) {
//        String name = Thread.currentThread().getName();
//        int q = goodsDao.aaa(goods.getId());
//        System.out.println("前 " +name+":"+q);


        MiaoShaGoods g = new MiaoShaGoods();
        g.setGoodsId(goods.getId());
        goodsDao.reduceStock(g);


//        int i = goodsDao.aaa(goods.getId());
//        System.out.println("后 " +name+":"+i);
    }
}
