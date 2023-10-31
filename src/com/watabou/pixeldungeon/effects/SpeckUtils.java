package com.watabou.pixeldungeon.effects;

import com.watabou.utils.PointF;

public class SpeckUtils {
    public static Speck createSpeck(float x0, float y0, int mx, int my) {
        Speck speck = new Speck();
        speck.color(COLOR);

        float x1 = x0 + mx * SIZE;
        float y1 = y0 + my * SIZE;

        PointF p = new PointF().polar(Random.Float(2 * PointF.PI), 8);
        x0 += p.x;
        y0 += p.y;

        float dx = x1 - x0;
        float dy = y1 - y0;

        speck.x = x0;
        speck.y = y0;
        speck.speed.set(dx, dy);
        speck.acc.set(-dx / 4, -dy / 4);

        speck.left = speck.lifespan = 2f;

        return speck;
    }
}