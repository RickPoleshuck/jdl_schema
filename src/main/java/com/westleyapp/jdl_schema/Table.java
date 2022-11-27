package com.westleyapp.jdl_schema;

import org.apache.commons.text.CaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

enum ColumnType {Instant, String, Integer, Boolean, Double}

class Column {
    private final static Map<String, ColumnType> TYPE_MAP = Map.of(
            "VARCHAR", ColumnType.String,
            "BIGINT", ColumnType.Integer,
            "SMALLINT", ColumnType.Integer,
            "INT", ColumnType.Integer,
            "BIT", ColumnType.Boolean,
            "TIMESTAMP", ColumnType.Instant,
            "DOUBLE", ColumnType.Double,
            "DATETIME", ColumnType.Instant,
            "DATE", ColumnType.Instant,
            "MEDIUMINT", ColumnType.Integer
    );
    private String name;
    private ColumnType type;
    private Integer maxlength;

    public String getName() {
        return name;
    }

    public ColumnType getType() {
        return type;
    }

    public Integer getMaxlength() {
        return maxlength;
    }

    public Column(final String name, final String type, final int maxlength) {
        this.name = CaseUtils.toCamelCase(name, false, '_');
        this.type = TYPE_MAP.get(type);
        if (this.type == ColumnType.String) {
            this.maxlength = maxlength;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Column{");
        sb.append("name='").append(name).append('\'');
        sb.append(", type=").append(type);
        if (maxlength != null) sb.append(", maxlength=").append(maxlength);
        sb.append('}');
        return sb.toString();
    }
}

public class Table {
    private static final Logger LOG = LoggerFactory.getLogger(Table.class);
    private final String name;
    private List<Column> columns = new ArrayList<>();

    public String getName() {
        return name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Table(final Connection connection, final String name) throws SQLException {
        this.name = CaseUtils.toCamelCase(name, true, '_');
        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + name + " where 1 = 2");
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); ++i) {
            String columnName = rsmd.getColumnName(i);
            if (columnName.equals("id")) continue;
            String columnType = rsmd.getColumnTypeName(i);
            int columnWidth = rsmd.getPrecision(i);
            columns.add(new Column(columnName, columnType, columnWidth));
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Table{");
        sb.append("name='").append(name).append('\'');
        sb.append(", columns=").append(columns);
        sb.append('}');
        return sb.toString();
    }
}
