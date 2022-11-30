package com.westleyapp.jdl_schema;

enum RelationshipType { OneToOne, OneToMany, ManyToOne, ManyToMany }
public class Relationship {
    private final String tableName;
    private final String columnName;
    private final String foreignTableName;
    private final String foreignColumnName;
    private final RelationshipType relationshipType = RelationshipType.OneToOne;

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public Relationship(String tableName, String columnName, String foreignTableName, String foreignColumnName) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.foreignTableName = foreignTableName;
        this.foreignColumnName = foreignColumnName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getForeignTableName() {
        return foreignTableName;
    }

    public String getForeignColumnName() {
        return foreignColumnName;
    }
}
