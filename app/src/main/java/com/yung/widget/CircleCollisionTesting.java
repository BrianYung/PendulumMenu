package com.yung.widget;

import android.util.SparseArray;

/**
 * 碰撞检测
 * Created by Brian on 2016-10-17.
 */

public class CircleCollisionTesting {
    private SparseArray<CircleCollision> listc;
    private onCollisionListener monCollisionListener;

    public static CircleCollisionTesting getInstance() {
        return new CircleCollisionTesting();
    }

    private CircleCollisionTesting() {
    }

    public void setCollisionList(SparseArray<CircleCollision> listc) {
        this.listc = listc;
        startCollisionTesting();
    }

    /**
     * 开始检测
     */
    public void startCollisionTesting() {
        if (listc == null) return;
        for (int i = 0; i < listc.size(); i++) {
            //进行边界检测
            boolean isside = listc.get(i).getCenterX() <= listc.get(i).getRadius()
                    || listc.get(i).getCenterY() <= listc.get(i).getRadius()
                    || (listc.get(i).getConwidth() - listc.get(i).getCenterX()) <= listc.get(i).getRadius()
                    || (listc.get(i).getConheight() - listc.get(i).getCenterY()) <= listc.get(i).getRadius();
            if (monCollisionListener != null) {
                monCollisionListener.onCollision(i, isside);
            }
            for (int j = 0; j < listc.size(); j++) {
                if (i == j) continue;
                float x = listc.get(i).getCenterX() - listc.get(j).getCenterX();
                float y = listc.get(i).getCenterY() - listc.get(j).getCenterY();
                float c = listc.get(i).getRadius() + listc.get(j).getRadius();
                if (monCollisionListener != null) {
                    //进行碰撞的检测机制
                    monCollisionListener.onCollision(j, x * x + y * y <= c * c);//第二个参数即为是否进行了碰撞
                }
            }
        }
    }

    public void setonCollisionListener(onCollisionListener monCollisionListener) {
        this.monCollisionListener = monCollisionListener;
    }

    public interface onCollisionListener {
        /**
         * 碰撞回调
         *
         * @param index       修改索引
         * @param isCollision 是否碰撞
         */
        void onCollision(int index, boolean isCollision);
    }
}
