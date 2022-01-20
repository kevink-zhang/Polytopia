package org.cis120.polytopia.Tile;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Edge {
    public GameTile s1;
    public GameTile s2;
    public Point p1;
    public Point p2;

    public Set<Edge> adj = new HashSet<>();

    public final Color defc = new Color(20, 20, 20);// new Color(238, 238, 238);
    public Color c = null;

    public Edge(Point a, Point b, GameTile g1, GameTile g2) {
        p1 = a;
        p2 = b;
        s1 = g1;
        s2 = g2;
    }

    public void draw(Graphics g) {
        ((Graphics2D) g).setStroke(new BasicStroke(1));
        g.setColor(defc);
        // if(toggle){
        // ((Graphics2D) g).setStroke(new BasicStroke(3));
        // g.setColor(new Color(100,140,255));
        // }
        // else if (c!=null){
        // ((Graphics2D) g).setStroke(new BasicStroke(3));
        // g.setColor(c);
        // }

        // g.drawLine(p1.x, p1.y, p2.x, p2.y); DRAWS THE EDGES
        ((Graphics2D) g).setStroke(new BasicStroke(1));
    }

    public boolean similar(Point p, Point q) {
        return Math.abs(p1.x - p.x + p1.y - p.y) + Math.abs(p2.x - q.x + p2.y - q.y) < 3 ||
                Math.abs(p2.x - p.x + p2.y - p.y) + Math.abs(p1.x - q.x + p1.y - q.y) < 3;
    }

    /*
     * Returns closest point in an edge to this edge's endpoints
     */
    public Point closest(Edge e) {
        double d1 = Math.min(
                Point.distance(e.p1.x, e.p1.y, p1.x, p1.y),
                Point.distance(e.p1.x, e.p1.y, p2.x, p2.y)
        );
        double d2 = Math.min(
                Point.distance(e.p2.x, e.p2.y, p1.x, p1.y),
                Point.distance(e.p2.x, e.p2.y, p2.x, p2.y)
        );
        if (d1 < d2)
            return e.p1;
        else
            return e.p2;
    }

    public double dist(Edge e) {
        double d1 = Math.min(
                Point.distance(e.p1.x, e.p1.y, p1.x, p1.y),
                Point.distance(e.p1.x, e.p1.y, p2.x, p2.y)
        );
        double d2 = Math.min(
                Point.distance(e.p2.x, e.p2.y, p1.x, p1.y),
                Point.distance(e.p2.x, e.p2.y, p2.x, p2.y)
        );
        return Math.min(d1, d2);
    }

    public double dist(Point p) {
        return Math.min(Point.distance(p.x, p.y, p1.x, p1.y), Point.distance(p.x, p.y, p2.x, p2.y));
    }

    public GameTile other(GameTile gt) {
        if (gt == s1) {
            return s2;
        } else if (gt == s2) {
            return s1;
        } else {
            return null;
        }
    }

    public Point other(Point p) {
        if (Point.distance(p.x, p.y, p1.x, p1.y) < 3) {
            return p2;
        } else if (Point.distance(p.x, p.y, p2.x, p2.y) < 3) {
            return p1;
        } else {
            return null;
        }
    }
}
