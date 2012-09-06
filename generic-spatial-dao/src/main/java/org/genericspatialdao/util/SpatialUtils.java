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
		Point point = new GeometryFactory().createPoint(coordinate);
		if (point != null) {
			point.setSRID(srid);
		}
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
		try {
			MultiPoint multiPoint = new GeometryFactory()
					.createMultiPoint(geometries);
			checkSRIDs(geometries);
			if (multiPoint != null) {
				multiPoint.setSRID(geometries[0].getSRID());
			}
			checkGeometry(multiPoint);
			if (LOG.isDebugEnabled()) {
				LOG.debug(RESULT + multiPoint);
			}
			return multiPoint;
		} catch (Exception e) {
			String message = ERROR + e.getMessage();
			LOG.error(message);
			throw new SpatialException(message, e);
		}
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
		LineString line = new GeometryFactory().createLineString(coordinates);
		if (line != null) {
			line.setSRID(srid);
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
		try {
			MultiLineString geometry = new GeometryFactory()
					.createMultiLineString(geometries);
			checkSRIDs(geometries);
			if (geometry != null) {
				geometry.setSRID(geometries[0].getSRID());
			}
			checkGeometry(geometry);
			if (LOG.isDebugEnabled()) {
				LOG.debug(RESULT + geometry);
			}
			return geometry;
		} catch (Exception e) {
			String message = ERROR + e.getMessage();
			LOG.error(message);
			throw new SpatialException(message, e);
		}
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
		try {
			MultiPolygon geometry = new GeometryFactory()
					.createMultiPolygon(geometries);
			checkSRIDs(geometries);
			if (geometry != null) {
				geometry.setSRID(geometries[0].getSRID());
			}
			checkGeometry(geometry);
			if (LOG.isDebugEnabled()) {
				LOG.debug(RESULT + geometry);
			}
			return geometry;
		} catch (Exception e) {
			String message = ERROR + e.getMessage();
			LOG.error(message);
			throw new SpatialException(message, e);
		}
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
		try {
			GeometryCollection geometry = new GeometryFactory()
					.createGeometryCollection(geometries);
			checkSRIDs(geometries);
			if (geometry != null) {
				geometry.setSRID(geometries[0].getSRID());
			}
			checkGeometry(geometry);
			if (LOG.isDebugEnabled()) {
				LOG.debug(RESULT + geometry);
			}
			return geometry;
		} catch (Exception e) {
			String message = ERROR + e.getMessage();
			LOG.error(message);
			throw new SpatialException(message, e);
		}
	}

	//
	// GEOMETRY
	//

	public static Geometry createGeometry(String wkt, int srid) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating geometry from wkt " + wkt + " and SRID " + srid);
		}
		try {
			Geometry geometry = (Geometry) new WKTReader().read(wkt);
			if (geometry != null) {
				geometry.setSRID(srid);
			}
			checkGeometry(geometry);
			if (LOG.isDebugEnabled()) {
				LOG.debug(RESULT + geometry);
			}
			return geometry;
		} catch (Exception e) {
			String message = ERROR + e.getMessage();
			LOG.error(message);
			throw new SpatialException(message, e);
		}
	}

	//
	// OTHER METHODS
	//

	public static Geometry changeScale(Geometry geometry, double factor) {
		if (LOG.isInfoEnabled()) {
			LOG.info("Changing scale using factor " + factor);
		}
		Geometry newGeometry = (Geometry) geometry.clone();

		for (int i = 0; i < newGeometry.getCoordinates().length; i++) {
			Coordinate finalCoordinate = new Coordinate(
					newGeometry.getCoordinates()[i].x * factor,
					newGeometry.getCoordinates()[i].y * factor);
			newGeometry.getCoordinates()[i].setCoordinate(finalCoordinate);
		}
		newGeometry.geometryChanged();
		checkGeometry(newGeometry);
		if (LOG.isDebugEnabled()) {
			LOG.debug(RESULT + newGeometry);
		}
		return newGeometry;
	}

	public static Geometry changeScaleCentroidBased(Geometry geometry,
			double factor) {
		if (LOG.isInfoEnabled()) {
			LOG.info("Changing scale centroid based using factor " + factor);
		}
		Geometry newGeometry = (Geometry) geometry.clone();

		Point centroid = newGeometry.getCentroid();
		for (int i = 0; i < newGeometry.getCoordinates().length; i++) {
			Coordinate translatedCoordinate = new Coordinate(
					newGeometry.getCoordinates()[i].x - centroid.getX(),
					newGeometry.getCoordinates()[i].y - centroid.getY());
			Coordinate translatedResizedCoordinated = new Coordinate(
					translatedCoordinate.x * factor, translatedCoordinate.y
							* factor);
			Coordinate finalCoordinate = new Coordinate(
					translatedResizedCoordinated.x + centroid.getX(),
					translatedResizedCoordinated.y + centroid.getY());
			newGeometry.getCoordinates()[i].setCoordinate(finalCoordinate);
		}
		newGeometry.geometryChanged();
		checkGeometry(newGeometry);
		if (LOG.isDebugEnabled()) {
			LOG.debug(RESULT + newGeometry);
		}
		return newGeometry;
	}

	/**
	 * Be careful, this method can create invalid geometries depending on the
	 * case. In these cases, an exception will be throw
	 * 
	 * @param geometry
	 * @param maxFractionDigits
	 * @return
	 */
	public static Geometry roundGeometry(Geometry geometry,
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

	public static void checkGeometry(Geometry geometry) {
		if (geometry == null || geometry.isEmpty() || !geometry.isValid()) {
			String message = INVALID_GEOMETRY + geometry;
			LOG.error(message);
			throw new SpatialException(message);
		}
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
		if (srid == 0) {
			String message = "Missing SRID in geometry: " + geometries[0];
			LOG.error(message);
			throw new SpatialException(message);
		}
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
