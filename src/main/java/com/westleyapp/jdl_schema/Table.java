package com.westleyapp.jdl_schema;

import org.apache.commons.text.CaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
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
    private final String name;
    private final ColumnType type;
    private Integer maxlength;

    public Column(final String name, final String type, final int maxlength) {
        this.name = CaseUtils.toCamelCase(name, false, '_');
        this.type = TYPE_MAP.get(type);
        if (this.type == ColumnType.String) {
            this.maxlength = maxlength;
        }
    }

    public String getName() {
        return name;
    }

    public ColumnType getType() {
        return type;
    }

    public Integer getMaxlength() {
        return maxlength;
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

class ForeignKey {
    private final String tableName;
    private final String columnName;

    public ForeignKey(String tableName, String columnName) {
        this.tableName = tableName;
        this.columnName = columnName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public String toString() {
        String sb = "ForeignKey{" + "tableName='" + tableName + '\'' +
                ", columnName='" + columnName + '\'' +
                '}';
        return sb;
    }
}

public class Table {
    private static final Logger LOG = LoggerFactory.getLogger(Table.class);
    private final String name;
    private final List<Column> columns = new ArrayList<>();
    private final List<String> primaryKeys;
    private final List<ForeignKey> exportedKeys;

    public Table(final Connection connection, final String tableName) throws SQLException {
        this.name = CaseUtils.toCamelCase(tableName, true, '_');

        primaryKeys = getPrimaryKeys(connection, tableName);
        exportedKeys = getForeignKeys(connection, tableName);
        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + tableName + " where 1 = 2");
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); ++i) {
            String columnName = rsmd.getColumnName(i);
            if (columnName.equals("id") && primaryKeys.contains("id")) continue;
            String columnType = rsmd.getColumnTypeName(i);
            int columnWidth = rsmd.getPrecision(i);
            columns.add(new Column(columnName, columnType, columnWidth));
        }
    }

    public String getName() {
        return name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Table{");
        sb.append("name='").append(name).append('\'');
        sb.append(", columns=").append(columns);
        sb.append(", primaryKeys=").append(primaryKeys);
        sb.append(", exportedKeys=").append(exportedKeys);
        sb.append('}');
        return sb.toString();
    }

    private List<String> getPrimaryKeys(final Connection connection, final String tableName) throws SQLException {
        List<String> primaryKeys = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet rs = metaData.getPrimaryKeys(null, null, tableName);
        while (rs.next()) {
            primaryKeys.add(rs.getString(4));
        }
        return primaryKeys;
    }

    private List<ForeignKey> getForeignKeys(final Connection connection, final String tableName) throws SQLException {
        List<ForeignKey> foreignKeys = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet rs = metaData.getExportedKeys(null, null, tableName);
        while (rs.next()) {
            ForeignKey foreignKey = new ForeignKey(rs.getString(7), rs.getString(8));
            foreignKeys.add(foreignKey);
        }
        return foreignKeys;
    }
}
