package external.geoscript

import geoscript.geom.*
import geoscript.viewer.Viewer

v = new Viewer()
poly = Geometry.fromWKT("POLYGON ((0 0, 10 0, 10 10, 0 10, 0 0))")
line = new LineString([2,2],[3,4],[5,7],[6,8])
//v.draw(poly)
//v.draw(line)
v.draw([poly, line.buffer(1), new Point(5,5).buffer(1)])
