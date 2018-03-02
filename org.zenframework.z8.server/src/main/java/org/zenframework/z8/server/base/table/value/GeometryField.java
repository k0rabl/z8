package org.zenframework.z8.server.base.table.value;

import org.zenframework.z8.server.db.DatabaseVendor;
import org.zenframework.z8.server.db.FieldType;
import org.zenframework.z8.server.db.sql.SqlField;
import org.zenframework.z8.server.engine.ApplicationServer;
import org.zenframework.z8.server.runtime.IObject;
import org.zenframework.z8.server.types.geometry;
import org.zenframework.z8.server.types.primary;
import org.zenframework.z8.server.types.string;
import org.zenframework.z8.server.types.sql.sql_geometry;

public class GeometryField extends Field {
	public static class CLASS<T extends GeometryField> extends Field.CLASS<T> {
		public CLASS(IObject container) {
			super(container);
			setJavaClass(GeometryField.class);
			setSystem(true);
		}

		@Override
		public Object newObject(IObject container) {
			return new GeometryField(container);
		}
	}

	public GeometryField(IObject container) {
		super(container);
		setDefault(new geometry());
		aggregation = Aggregation.Max;
	}

	public geometry z8_getDefault() {
		return (geometry)super.getDefault();
	}

	@Override
	public primary getDefault() {
		return (ApplicationServer.events() && !changed()) ? z8_getDefault() : super.getDefault();
	}

	@Override
	public FieldType type() {
		return FieldType.Geometry;
	}

	@Override
	public String sqlType(DatabaseVendor vendor) {
		String name = type().vendorType(vendor);

		if(vendor == DatabaseVendor.Postgres)
			return name + "(Geometry, " + geometry.DefaultSRS.get() + ")";

		return name;
	}

	public sql_geometry sql_geometry() {
		return new sql_geometry(new SqlField(this));
	}

	@Override
	public primary get() {
		return z8_get();
	}

	public geometry z8_get() {
		return (geometry)internalGet();
	}

	@Override
	public primary parse(String value) {
		return new geometry(value);
	}

	@Override
	public String wrapForSelect(String value, DatabaseVendor vendor) {
		return "st_asGeoJson(" + value + ")";
	}

	@Override
	public String wrapForInsert(String value, DatabaseVendor vendor) {
		return geometry.toDbGeometry(value);
	}

	public GeometryField.CLASS<? extends GeometryField> operatorAssign(geometry value) {
		set(value);
		return (GeometryField.CLASS<?>)this.getCLASS();
	}

	public GeometryField.CLASS<? extends GeometryField> operatorAssign(string value) {
		set(new geometry(value));
		return (GeometryField.CLASS<?>)this.getCLASS();
	}
}