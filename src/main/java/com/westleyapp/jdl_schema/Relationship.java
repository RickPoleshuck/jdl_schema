package com.westleyapp.jdl_schema;


public class Relationship {
    private final String tableName;
    private final String columnName;
    private final String foreignTableName;
    private final String foreignColumnName;
    private RelationshipType relationshipType = RelationshipType.OneToOne;

    public Relationship(String tableName, String columnName, String foreignTableName, String foreignColumnName) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.foreignTableName = foreignTableName;
        this.foreignColumnName = foreignColumnName;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
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
