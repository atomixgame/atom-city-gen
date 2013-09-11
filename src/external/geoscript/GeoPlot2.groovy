package external.geoscript

import geoscript.geom.*
import static geoscript.render.Plot.plot;

// displacement
poly = new Point(0,0).buffer(1)
plot([poly, poly.translate(0.75,0)])

// scale + shear
poly = new Polygon([[[0,0],[1,0],[1,1],[0,1],[0,0]]])
plot([poly, poly.scale(2,2).shear(1,0)])

// rotation
poly = new Polygon([[[-5,-2],[5,-2],[5,2],[-5,2],[-5,-2]]])
poly = poly.union(poly.rotate(Math.toRadians(90)))
plot([poly, poly.rotate(Math.toRadians(45))])
