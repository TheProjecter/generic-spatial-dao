package org.genericspatialdao.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.genericspatialdao.exception.SpatialException;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class SpatialUtilsTest {

	private static final int SRID = 4326;

	@Test
	public void createPointFromWKTTest() {
		double x = TestUtils.randomDouble();
		double y = TestUtils.randomDouble();
		Point p = SpatialUtils.createPoint("POINT(" + x + " " + y + ")", SRID);
		assertEquals(false, p.isEmpty());
		assertEquals(true, p.isValid());
		assertEquals(SRID, p.getSRID());
	}

	@Test(expected = SpatialException.class)
	public void createPointFromWKTWrongTest() {
		SpatialUtils.createPoint("POINT()", SRID);
	}

	@Test(expected = SpatialException.class)
	public void createPointFromWKTWrong2Test() {
		SpatialUtils.createPoint("", SRID);
	}

	@Test
	public void createPointFromCoordinateTest() {
		double x = TestUtils.randomDouble();
		double y = TestUtils.randomDouble();
		Point p = SpatialUtils.createPoint(new Coordinate(x, y), SRID);
		assertEquals(false, p.isEmpty());
		assertEquals(true, p.isValid());
		assertEquals(SRID, p.getSRID());
	}

	@Test
	public void createPointFromXAndYTest() {
		double x = TestUtils.randomDouble();
		double y = TestUtils.randomDouble();
		Point p = SpatialUtils.createPoint(x, y, SRID);
		assertEquals(false, p.isEmpty());
		assertEquals(true, p.isValid());
		assertEquals(SRID, p.getSRID());
	}

	@Test
	public void createPolygonFromWKTTest() {
		Polygon p = SpatialUtils
				.createPolygon(
						"POLYGON((0 0,4 0,4 4,0 4,0 0),(1 1, 2 1, 2 2, 1 2,1 1))",
						SRID);
		assertEquals(false, p.isEmpty());
		assertEquals(true, p.isValid());
		assertEquals(SRID, p.getSRID());
	}

	@Test
	public void createLineStringFromWKTTest() {
		LineString ls = SpatialUtils.createLineString("LINESTRING(4 6,7 10))",
				SRID);
		assertEquals(false, ls.isEmpty());
		assertEquals(true, ls.isValid());
		assertEquals(SRID, ls.getSRID());
	}

	@Test
	public void createLineStringTest() {
		Coordinate[] coordinates = new Coordinate[2];
		coordinates[0] = SpatialUtils.createCoordinate(4, 6);
		coordinates[1] = SpatialUtils.createCoordinate(7, 10);
		LineString ls = SpatialUtils.createLineString(coordinates, SRID);
		assertEquals(false, ls.isEmpty());
		assertEquals(true, ls.isValid());
		assertEquals(SRID, ls.getSRID());
	}

	@Test(expected = ClassCastException.class)
	public void createLineStringFromWKTWrongTest() {
		LineString ls = SpatialUtils.createLineString("POINT(5 10)", SRID);
		assertEquals(false, ls.isEmpty());
		assertEquals(true, ls.isValid());
		assertEquals(SRID, ls.getSRID());
	}

	@Test
	public void createMultipolygonFromWKTTest() {
		MultiPolygon mp = SpatialUtils
				.createMultiPolygon(
						"MULTIPOLYGON ("
								+ "((-47.200183869063 -22.886767385861, -47.197780609786 -22.886681555173, -47.197780609786 -22.889986036679, -47.200183869063 -22.889986036679, -47.200183869063 -22.886767385861)),"
								+ "((-47.195119858443 -22.886767385861, -47.192029953658 -22.886767385861, -47.192029953658 -22.889943121335, -47.195119858443 -22.889943121335, -47.195119858443 -22.886767385861)),"
								+ "((-47.186236382186 -22.886767385861, -47.189712525069 -22.886767385861, -47.189755440413 -22.889986036679, -47.186150551497 -22.890028952023, -47.186236382186 -22.886767385861)),"
								+ "((-47.200183869063 -22.892475126645, -47.19782352513 -22.892432211301, -47.197780609786 -22.895393370053, -47.200226784407 -22.895393370053, -47.200183869063 -22.892475126645)),"
								+ "((-47.195162773787 -22.892475126645, -47.192072869002 -22.892432211301, -47.192072869002 -22.895436285397, -47.195205689131 -22.895479200742, -47.195162773787 -22.892475126645)),"
								+ "((-47.18962669438 -22.892518041989, -47.186150551497 -22.892475126645, -47.186107636153 -22.895436285397, -47.189669609725 -22.895436285397, -47.18962669438 -22.892518041989))"
								+ ")", SRID);
		assertEquals(false, mp.isEmpty());
		assertEquals(true, mp.isValid());
		assertEquals(SRID, mp.getSRID());
	}

	@Test
	public void createMultiPolygonTest() {
		SpatialUtils
				.createMultiPolygon(
						"MULTIPOLYGON (((40 40, 20 45, 45 30, 40 40)),((20 35, 45 20, 30 5, 10 10, 10 30, 20 35),(30 20, 20 25, 20 15, 30 20)))",
						SRID);
	}

	@Test
	public void createMultiPolygon2Test() {
		List<Polygon> list = new ArrayList<Polygon>();
		list.add(SpatialUtils
				.createPolygon(
						"POLYGON((0 0,4 0,4 4,0 4,0 0),(1 1, 2 1, 2 2, 1 2,1 1))",
						SRID));
		list.add(SpatialUtils
				.createPolygon(
						"POLYGON((10 10,14 10,14 14,10 14,10 10),(11 11, 12 11, 12 12, 11 12,11 11))",
						SRID));
		SpatialUtils.createMultiPolygon(list);
	}

	@Test
	public void createGeometryFromWKTTest() {
		Geometry g = SpatialUtils
				.createGeometry(
						"POLYGON((0 0,4 0,4 4,0 4,0 0),(1 1, 2 1, 2 2, 1 2,1 1))",
						SRID);
		assertEquals(false, g.isEmpty());
		assertEquals(true, g.isValid());
		assertEquals(SRID, g.getSRID());
	}

	@Test(expected = ClassCastException.class)
	public void createPolygonFromWKTWrongTest() {
		Polygon p = SpatialUtils.createPolygon("POINT(0 0)", SRID);
		assertEquals(false, p.isEmpty());
		assertEquals(true, p.isValid());
		assertEquals(SRID, p.getSRID());
	}

	@Test
	public void generateLongLatPointTest() {
		Point p = SpatialUtils.generateLongLatPoint(SRID);
		assertEquals(false, p.isEmpty());
		assertEquals(true, p.isValid());
		assertEquals(SRID, p.getSRID());
	}

	@Test
	public void generateLongLatPointsTest() {
		List<Point> list = SpatialUtils.generateLongLatPoints(10, SRID);
		for (Point p : list) {
			assertEquals(false, p.isEmpty());
			assertEquals(true, p.isValid());
			assertEquals(SRID, p.getSRID());
		}
	}

	@Test
	public void roundGeometryTest() {
		Polygon polygon = SpatialUtils
				.createPolygon(
						"POLYGON ((-23.6001133922035 -11.447739600371, -23.6001133922035 -11.4433407775865, -23.5930538180765 -11.4433407775865, -23.5930538180765 -11.447739600371, -23.6001133922035 -11.447739600371))",
						SRID);
		Polygon expected = SpatialUtils
				.createPolygon(
						"POLYGON ((-23.6001 -11.4477, -23.6001 -11.4433, -23.5931 -11.4433, -23.5931 -11.4477, -23.6001 -11.4477))",
						SRID);
		assertEquals(expected, SpatialUtils.getRoundedGeometry(polygon, 4));
	}

	@Test
	public void createMultiPointTest() {
		SpatialUtils.createMultiPoint(
				"MULTIPOINT ((10 40), (40 30), (20 20), (30 10))", SRID);
		SpatialUtils.createMultiPoint(
				"MULTIPOINT (10 40, 40 30, 20 20, 30 10)", SRID);
	}

	@Test
	public void createMultiPoint2Test() {
		List<Point> list = SpatialUtils.generateLongLatPoints(10, SRID);
		SpatialUtils.createMultiPoint(list);
	}

	@Test
	public void createMultiLineStringTest() {
		SpatialUtils
				.createMultiLineString(
						"MULTILINESTRING ((10 10, 20 20, 10 40),(40 40, 30 30, 40 20, 30 10))",
						SRID);
	}

	@Test
	public void createMultiLineString2Test() {
		LineString[] geometries = new LineString[2];
		geometries[0] = SpatialUtils.createLineString(
				"LINESTRING (30 10, 10 30, 40 40)", SRID);
		geometries[1] = SpatialUtils.createLineString(
				"LINESTRING (35 15, 15 35, 45 45)", SRID);

		SpatialUtils.createMultiLineString(geometries);
	}

	@Test(expected = ClassCastException.class)
	public void createMultiLineStringWrongTest() {
		SpatialUtils.createMultiLineString(
				"MULTIPOINT ((10 40), (40 30), (20 20), (30 10))", SRID);
	}

	@Test
	public void createGeometryCollectionTest() {
		List<Geometry> list = new ArrayList<Geometry>();
		list.add(SpatialUtils.generateLongLatPoint(SRID));
		list.add(SpatialUtils.generateLongLatPoint(SRID));
		list.add(SpatialUtils
				.createMultiLineString(
						"MULTILINESTRING ((10 10, 20 20, 10 40),(40 40, 30 30, 40 20, 30 10))",
						SRID));
		list.add(SpatialUtils.generateLongLatPoint(SRID));

		GeometryCollection gc = SpatialUtils.createGeometryCollection(list);
		assertEquals(SRID, gc.getSRID());
		assertFalse(gc.isEmpty());
	}

	@Test
	public void checkGeometryTest() {
		SpatialUtils
				.checkGeometry("MULTIPOINT ((10 10), (40 30), (20 20), (30 10))");
	}

	@Test(expected = SpatialException.class)
	public void checkGeometryWrongTest() {
		SpatialUtils
				.checkGeometry("MULTIPOINT ((10 a), (40 30), (20 20), (30 10))");
	}
}