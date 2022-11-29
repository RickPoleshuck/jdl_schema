package com.westleyapp.jdl_schema;

import java.util.Map;

public class Column {
    public final static Map<String, ColumnType> TYPE_MAP = Map.of(
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
    private final String type;

    public boolean isRelation() {
        return isRelation;
    }

    public void setRelation(boolean relation) {
        isRelation = relation;
    }

    private Integer maxlength;
    private boolean isRelation;

    public Column(final String name, final String type, final int maxlength) {
        this.name = name;
        this.type = type;
        if (TYPE_MAP.get(this.type) == ColumnType.String) {
            this.maxlength = maxlength;
        }
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
