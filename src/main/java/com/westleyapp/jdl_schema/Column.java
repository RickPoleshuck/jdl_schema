package com.westleyapp.jdl_schema;

import java.util.Map;

public class Column {
    public final static Map<String, ColumnType> TYPE_MAP = Map.ofEntries(
            Map.entry("VARCHAR", ColumnType.String),
            Map.entry("BIGINT", ColumnType.Integer),
            Map.entry("SMALLINT", ColumnType.Integer),
            Map.entry("INT", ColumnType.Integer),
            Map.entry("BIT", ColumnType.Boolean),
            Map.entry("TIMESTAMP", ColumnType.Instant),
            Map.entry("DOUBLE", ColumnType.Double),
            Map.entry("DATETIME", ColumnType.Instant),
            Map.entry("DATE", ColumnType.Instant),
            Map.entry("MEDIUMINT", ColumnType.Integer),
            Map.entry("BLOB", ColumnType.Blob),
            Map.entry("LONGBLOB", ColumnType.Blob)
    );
    private final String name;
    private final String type;
    private Integer maxlength;
    private boolean isRelation;

    public Column(final String name, final String type, final int maxlength) {
        this.name = name;
        this.type = type;
        if (TYPE_MAP.get(this.type) == ColumnType.String) {
            this.maxlength = maxlength;
        }
    }

    public boolean isRelation() {
        return isRelation;
    }

    public void setRelation(boolean relation) {
        isRelation = relation;
    }

    public String getName() {
        return name;
    }

    public String getType() {
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
