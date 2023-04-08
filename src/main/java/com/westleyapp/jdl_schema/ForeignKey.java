package com.westleyapp.jdl_schema;

public class ForeignKey {
    private final String foreignTableName;
    private final String foreignColumnName;

    private final String localColumnName;

    public ForeignKey(final String localColumnName, final String foreignTableName, final String foreignColumnName) {
        this.localColumnName = localColumnName;
        this.foreignTableName = foreignTableName;
        this.foreignColumnName = foreignColumnName;
    }

    public String getLocalColumnName() {
        return localColumnName;
    }

    public String getForeignTableName() {
        return foreignTableName;
    }

    public String getForeignColumnName() {
        return foreignColumnName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ForeignKey{");
        sb.append("foreignTableName='").append(foreignTableName).append('\'');
        sb.append(", foreignColumnName='").append(foreignColumnName).append('\'');
        sb.append(", localColumnName='").append(localColumnName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
