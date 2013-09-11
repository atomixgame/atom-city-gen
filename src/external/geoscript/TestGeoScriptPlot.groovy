package external.geoscript

import geoscript.geom.*
import geoscript.render.Plot

poly = new Point(0,0).buffer(1)
Plot.plot(poly)
Plot.plot(poly.simplify(0.05))
Plot.plot(poly.simplify(0.1))

