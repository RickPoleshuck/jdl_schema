package com.westleyapp.jdl_schema;

public class ForeignKey {
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
