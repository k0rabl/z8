package org.zenframework.z8.server.db.sql.functions.date;

import java.util.Collection;

import org.zenframework.z8.server.base.table.value.IValue;
import org.zenframework.z8.server.db.DatabaseVendor;
import org.zenframework.z8.server.db.FieldType;
import org.zenframework.z8.server.db.sql.FormatOptions;
import org.zenframework.z8.server.db.sql.SqlConst;
import org.zenframework.z8.server.db.sql.SqlToken;
import org.zenframework.z8.server.db.sql.expressions.Mul;
import org.zenframework.z8.server.db.sql.expressions.Operation;
import org.zenframework.z8.server.db.sql.functions.numeric.Round;
import org.zenframework.z8.server.types.datespan;
import org.zenframework.z8.server.types.integer;

public class TotalHour extends SqlToken {
	private SqlToken span;

	public TotalHour(SqlToken span) {
		this.span = span;
	}

	@Override
	public void collectFields(Collection<IValue> fields) {
		span.collectFields(fields);
	}

	@Override
	public String format(DatabaseVendor vendor, FormatOptions options, boolean logicalContext) {
		return new Round(new Mul(span, Operation.Div, new SqlConst(new integer(datespan.TicksPerHour))), null).format(vendor, options);
	}

	@Override
	public FieldType type() {
		return FieldType.Integer;
	}
}
