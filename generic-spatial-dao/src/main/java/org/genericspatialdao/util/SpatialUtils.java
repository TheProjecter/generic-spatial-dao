package org.genericspatialdao.util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.genericspatialdao.exception.SpatialException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTReader;

/**
 * 
 * @author Joao Savio C. Longo - joaosavio@gmail.com
 * 
 */
public final class SpatialUtils {

	private static final int MAX_LATITUDE = 90;
	private static final int MIN_LATITUDE = -90;
	private static final int MAX_LONGITUDE = 180;
	private static final int MIN_LONGITUDE = -180;
	private static final String INVALID_GEOMETRY = "Invalid geometry: ";
	private static final String ERROR = "Error: ";
	private static final String RESULT = "Result: ";
	private static final Logger LOG = Logger.getLogger(SpatialUtils.class);

	private static PrecisionModel precisionModel = new PrecisionModel();

	private SpatialUtils() {

	}

	//
	// POINT
	//

	public static Point createPoint(String wkt, int srid) {
		return (Point) createGeometry(wkt, srid);
	}

	public static Point createPoint(Coordinate coordinate, int srid) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating point from coordinate " + coordinate
					+ " and SRID " + srid);
		}
		Point point = new GeometryFactory(precisionModel, srid)
				.createPoint(coordinate);
		checkGeometry(point);
		if (LOG.isDebugEnabled()) {
			LOG.debug(RESULT + point);
		}
		return point;
	}

	public static Point createPoint(double x, double y, int srid) {
		return createPoint(createCoordinate(x, y), srid);
	}

	public static Point generateLongLatPoint(int srid) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Generating long/lat point with SRID " + srid);
		}
		Coordinate coordinate = new Coordinate(randomDouble(MIN_LONGITUDE,
				MAX_LONGITUDE), randomDouble(MIN_LATITUDE, MAX_LATITUDE));
		Point generatedPoint = createPoint(coordinate, srid);
		if (LOG.isDebugEnabled()) {
			LOG.debug(generatedPoint);
		}
		return generatedPoint;
	}

	public static List<Point> generateLongLatPoints(int number, int srid) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Generating " + number + "long/lat points with SRID "
					+ srid);
		}
		List<Point> list = new ArrayList<Point>();
		Coordinate[] coordinates = new Coordinate[number];
		for (int i = 0; i < number; i++) {
			coordinates[i] = new Coordinate(randomDouble(MIN_LONGITUDE,
					MAX_LONGITUDE), randomDouble(MIN_LATITUDE, MAX_LATITUDE));
			Point point = createPoint(coordinates[i], srid);
			list.add(point);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(list);
		}
		return list;
	}

	//
	// MULTI-POINT
	//

	public static MultiPoint createMultiPoint(String wkt, int srid) {
		return (MultiPoint) createGeometry(wkt, srid);
	}

	public static MultiPoint createMultiPoint(List<Point> list) {
		Point[] geometries = getPointArrayFromList(list);
		return createMultiPoint(geometries);
	}

	public static MultiPoint createMultiPoint(Point[] geometries) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating multi-point from points "
					+ Arrays.toString(geometries));
		}
		checkSRIDs(geometries);
		MultiPoint multiPoint;
		try {
			multiPoint = new GeometryFactory(precisionModel,
					geometries[0].getSRID()).createMultiPoint(geometries);
		} catch (Exception e) {
			String message = ERROR + e.getMessage();
			LOG.error(message);
			throw new SpatialException(message, e);
		}
		checkGeometry(multiPoint);
		if (LOG.isDebugEnabled()) {
			LOG.debug(RESULT + multiPoint);
		}
		return multiPoint;
	}

	//
	// LINE-STRING
	//

	public static LineString createLineString(String wkt, int srid) {
		return (LineString) createGeometry(wkt, srid);
	}

	public static LineString createLineString(Coordinate[] coordinates, int srid) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating line from coordinates "
					+ Arrays.toString(coordinates) + " and SRID " + srid);
		}
		LineString line;
		try {
			line = new GeometryFactory(precisionModel, srid)
					.createLineString(coordinates);
		} catch (Exception e) {
			String message = ERROR + e.getMessage();
			LOG.error(message);
			throw new SpatialException(message, e);
		}
		checkGeometry(line);
		if (LOG.isDebugEnabled()) {
			LOG.debug(RESULT + line);
		}
		return line;
	}

	public static MultiLineString createMultiLineString(String wkt, int srid) {
		return (MultiLineString) createGeometry(wkt, srid);
	}

	public static MultiLineString createMultiLineString(List<LineString> list) {
		LineString[] geometries = getLineStringArrayFromList(list);
		return createMultiLineString(geometries);
	}

	public static MultiLineString createMultiLineString(LineString[] geometries) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating multi-line from lines "
					+ Arrays.toString(geometries));
		}
		checkSRIDs(geometries);
		MultiLineString geometry;
		try {
			geometry = new GeometryFactory(precisionModel,
					geometries[0].getSRID()).createMultiLineString(geometries);
		} catch (Exception e) {
			String message = ERROR + e.getMessage();
			LOG.error(message);
			throw new SpatialException(message, e);
		}
		checkGeometry(geometry);
		if (LOG.isDebugEnabled()) {
			LOG.debug(RESULT + geometry);
		}
		return geometry;
	}

	//
	// POLYGON
	//

	public static Polygon createPolygon(String wkt, int srid) {
		return (Polygon) createGeometry(wkt, srid);
	}

	//
	// MULTI-POLYGON
	//

	public static MultiPolygon createMultiPolygon(String wkt, int srid) {
		return (MultiPolygon) createGeometry(wkt, srid);
	}

	public static MultiPolygon createMultiPolygon(List<Polygon> list) {
		Polygon[] geometries = getPolygonArrayFromList(list);
		return createMultiPolygon(geometries);
	}

	public static MultiPolygon createMultiPolygon(Polygon[] geometries) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating multi-polygon from polygons "
					+ Arrays.toString(geometries));
		}
		checkSRIDs(geometries);
		MultiPolygon geometry;
		try {
			geometry = new GeometryFactory(precisionModel,
					geometries[0].getSRID()).createMultiPolygon(geometries);

		} catch (Exception e) {
			String message = ERROR + e.getMessage();
			LOG.error(message);
			throw new SpatialException(message, e);
		}
		checkGeometry(geometry);
		if (LOG.isDebugEnabled()) {
			LOG.debug(RESULT + geometry);
		}
		return geometry;
	}

	//
	// GEOMETRY COLLECTION
	//

	public static GeometryCollection createGeometryCollection(
			List<Geometry> list) {
		Geometry[] geometries = getArrayFromList(list);
		return createGeometryCollection(geometries);
	}

	public static GeometryCollection createGeometryCollection(
			Geometry[] geometries) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating geometry collection from geometries "
					+ Arrays.toString(geometries));
		}
		checkSRIDs(geometries);
		GeometryCollection geometry;
		try {
			geometry = new GeometryFactory(precisionModel,
					geometries[0].getSRID())
					.createGeometryCollection(geometries);
		} catch (Exception e) {
			String message = ERROR + e.getMessage();
			LOG.error(message);
			throw new SpatialException(message, e);
		}
		checkGeometry(geometry);
		if (LOG.isDebugEnabled()) {
			LOG.debug(RESULT + geometry);
		}
		return geometry;
	}

	//
	// GEOMETRY
	//

	public static Geometry createGeometry(String wkt, int srid) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating geometry from wkt " + wkt + " and SRID " + srid);
		}
		Geometry geometry;
		try {
			geometry = (Geometry) new WKTReader(new GeometryFactory(
					precisionModel, srid)).read(wkt);
		} catch (Exception e) {
			String message = ERROR + e.getMessage();
			LOG.error(message);
			throw new SpatialException(message, e);
		}
		checkGeometry(geometry);
		if (LOG.isDebugEnabled()) {
			LOG.debug(RESULT + geometry);
		}
		return geometry;
	}

	//
	// OTHER METHODS
	//

	/**
	 * Be careful, this method can create invalid geometries depending on the
	 * case. In these cases, an exception will be thrown
	 * 
	 * @param geometry
	 * @param maxFractionDigits
	 * @return a rounded geometry
	 */
	public static Geometry getRoundedGeometry(Geometry geometry,
			int maxFractionDigits) {
		if (LOG.isInfoEnabled()) {
			LOG.info("Rounding geometry " + geometry + " to "
					+ maxFractionDigits + " fraction digits");
		}
		if (maxFractionDigits < 1) {
			LOG.warn("It is recommended to use maxFractionDigits > 1");
		}

		Geometry newGeometry = (Geometry) geometry.clone();

		NumberFormat nf = NumberFormat.getInstance();
		// set decimal places
		nf.setMaximumFractionDigits(maxFractionDigits);

		for (int i = 0; i < newGeometry.getCoordinates().length; i++) {
			Coordinate coordinate = newGeometry.getCoordinates()[i];
			double roundedX = Double.valueOf(nf.format(coordinate.x));
			double roundedY = Double.valueOf(nf.format(coordinate.y));
			Coordinate roundedCoordinate = new Coordinate(roundedX, roundedY);
			newGeometry.getCoordinates()[i].setCoordinate(roundedCoordinate);
		}
		newGeometry.geometryChanged();
		checkGeometry(newGeometry);
		if (LOG.isDebugEnabled()) {
			LOG.debug(RESULT + newGeometry);
		}
		return newGeometry;
	}

	public static Coordinate createCoordinate(double x, double y) {
		return new Coordinate(x, y);
	}

	public static void checkGeometry(String wkt) {
		Geometry geometry;
		try {
			geometry = (Geometry) new WKTReader(new GeometryFactory(
					precisionModel)).read(wkt);
		} catch (Exception e) {
			String message = INVALID_GEOMETRY + wkt;
			LOG.error(message);
			throw new SpatialException(message, e);
		}
		checkGeometry(geometry);
	}

	public static void checkGeometry(Geometry geometry) {
		if (geometry == null || geometry.isEmpty() || !geometry.isValid()) {
			String message = INVALID_GEOMETRY + geometry;
			LOG.error(message);
			throw new SpatialException(message);
		}
	}

	public static PrecisionModel getPrecisionModel() {
		return precisionModel;
	}

	public static void setPrecisionModel(PrecisionModel precisionModel) {
		SpatialUtils.precisionModel = precisionModel;
	}

	private static Point[] getPointArrayFromList(List<Point> list) {
		Point[] geometries = new Point[list.size()];
		for (int i = 0; i < geometries.length; i++) {
			geometries[i] = list.get(i);
		}
		return geometries;
	}

	private static LineString[] getLineStringArrayFromList(List<LineString> list) {
		LineString[] geometries = new LineString[list.size()];
		for (int i = 0; i < geometries.length; i++) {
			geometries[i] = list.get(i);
		}
		return geometries;
	}

	private static Polygon[] getPolygonArrayFromList(List<Polygon> list) {
		Polygon[] geometries = new Polygon[list.size()];
		for (int i = 0; i < geometries.length; i++) {
			geometries[i] = list.get(i);
		}
		return geometries;
	}

	private static Geometry[] getArrayFromList(List<Geometry> list) {
		Geometry[] geometries = new Geometry[list.size()];
		for (int i = 0; i < geometries.length; i++) {
			geometries[i] = list.get(i);
		}
		return geometries;
	}

	private static void checkSRIDs(Geometry[] geometries) {
		if (geometries == null || geometries.length == 0) {
			String message = "No geometries passed";
			LOG.error(message);
			throw new SpatialException(message);
		}
		int srid = geometries[0].getSRID();
		for (int i = 1; i < geometries.length; i++) {
			if (geometries[i].getSRID() != srid) {
				String message = "Different SRID found in geometry: "
						+ geometries[i].getSRID();
				LOG.error(message);
				throw new SpatialException(message);
			}
		}
	}

	private static double randomDouble(double low, double high) {
		return (double) (Math.min(low, high) + Math.random() * (high - low));
	}
}