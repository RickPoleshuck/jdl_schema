package com.westleyapp.jdl_schema;

import org.apache.commons.text.CaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;




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
