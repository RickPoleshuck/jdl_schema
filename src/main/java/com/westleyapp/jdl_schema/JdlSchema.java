package com.westleyapp.jdl_schema;

import org.apache.commons.text.CaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class JdlSchema {
    private static final Logger LOG = LoggerFactory.getLogger(JdlSchema.class);
    private final Connection connection;
    private final BufferedWriter writer;
    private static final List<String> EXCLUDED_TABLES =
            List.of("DATABASECHANGELOG", "DATABASECHANGELOGLOCK", "jhi_authority",
                    "jhi_user", "jhi_user_authority");

    public JdlSchema(final String databaseUrl, final String username, final String password, final OutputStream output)
            throws FileNotFoundException, SQLException {
        writer = new BufferedWriter(new OutputStreamWriter(
                output, StandardCharsets.UTF_8));
        connection = DriverManager.getConnection(databaseUrl, username, password);
    }

    public void run() throws SQLException, IOException {
        List<Table> tables = getTables(connection);
        List<Relationship> relationships = getRelationShips(tables);
        printTables(tables);
        printPagination(tables);
        writer.flush();
    }

    private List<Table> getTables(final Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String[] types = {"TABLE"};
        //Retrieving the columns in the database
        ResultSet rs = metaData.getTables(null, null, "%", types);
        List<Table> tables = new ArrayList<>();
        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            if (EXCLUDED_TABLES.contains(tableName)) {
                continue;
            }
            Table table = new Table(connection, tableName);
            tables.add(table);
            LOG.debug("Found table: {}", table);
        }
        return tables;
    }

    private List<Relationship> getRelationShips(final List<Table> tables) {
        List<Relationship> result = new ArrayList<>();
        Map<String, Table> tableMap = new HashMap<>();
        for(Table t : tables) {
            tableMap.put(t.getTableName(), t);
        }

        for(Table t : tables) {
            for(ForeignKey fk : t.getForeignKeys()) {
                Table foreignTable = tableMap.get(fk.getForeignTableName());
                Optional<Column> foreignColumn = foreignTable.getColumns()
                        .stream()
                        .filter(c -> c.getName().equals(fk.getForeignColumnName()))
                        .findFirst();
                if (foreignColumn.isPresent()) {
                    foreignColumn.get().setRelation(true);
                } else {
                    LOG.warn("foreign column not found: {}", fk.getLocalColumnName());
                }
            }
        }

        return result;
    }
    private void printTables(final List<Table> tables) throws IOException {
        Collections.sort(tables, (t1, t2) -> t1.getTableName().compareTo(t2.getTableName()));
        for (Table t : tables) {
            LOG.info("Table: {}", t);
            writer.write("entity " +
                    CaseUtils.toCamelCase(t.getTableName(), true, '_')
                    + " {\n");
            Collections.sort(t.getColumns(), (c1, c2) -> c1.getName().compareTo(c2.getName()));
            for (Column c : t.getColumns()) {
                if (c.isRelation()) continue;
                writer.write("\t"
                        + CaseUtils.toCamelCase(c.getName(), false, '_')
                        + " " + Column.TYPE_MAP.get(c.getType()));
                if (c.getMaxlength() != null) {
                    writer.write(" maxlength(" + c.getMaxlength() + ")");
                }
                writer.newLine();
            }
            writer.write("}\n\n");
        }
    }

    private void printRelationships(final List<Table> tables) throws IOException {

    }

    private void printPagination(final List<Table> tables) throws IOException {
        for(Table t : tables) {
            writer.write("paginate "
            + CaseUtils.toCamelCase(t.getTableName(), true, '_')
                    + " with pagination\n");
        }
    }
}
