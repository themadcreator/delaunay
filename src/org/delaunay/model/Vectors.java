package org.delaunay.model;

import java.awt.geom.Rectangle2D;

public class Vectors {
    public static Rectangle2D boundingBox(Iterable<? extends Vector> vs){
    	Rectangle2D rect = null;
    	for(Vector v : vs){
    		if(rect == null){
    			rect = new Rectangle2D.Double(v.x, v.y, 0, 0);
    		} else{
    			rect.add(v.x, v.y);
    		}
    	}
    	return rect;
    }
}
