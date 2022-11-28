package com.westleyapp.jdl_schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JdlSchema {
    private static final Logger LOG = LoggerFactory.getLogger(JdlSchema.class);
    private final Connection connection;
    private final BufferedWriter writer;
    private static final List<String> EXCLUDED_TABLES =
            List.of("DATABASECHANGELOG", "DATABASECHANGELOGLOCK", "jhi_authority",
                    "jhi_user", "jhi_user_authority");

    public JdlSchema(final String databaseUrl, final String username, final String password, final String output)
            throws FileNotFoundException, SQLException {
        writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(output), StandardCharsets.UTF_8));
        connection = DriverManager.getConnection(databaseUrl, username, password);
    }

    public void run() throws SQLException, IOException {
        List<Table> tables = getTables(connection);
        printTables(tables);
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

    private void printTables(final List<Table> tables) throws IOException {
        Collections.sort(tables, (t1, t2) -> t1.getName().compareTo(t2.getName()));
        for(Table t : tables) {
            LOG.info("Table: {}", t);
            writer.write("entity " + t.getName() + " {\n");
            Collections.sort(t.getColumns(), (c1, c2) -> c1.getName().compareTo(c2.getName()));
            for(Column c : t.getColumns()) {
                writer.write("\t" + c.getName() + " " + Column.TYPE_MAP.get(c.getType()));
                if (c.getMaxlength() != null) {
                    writer.write(" maxlength(" + c.getMaxlength() + ")");
                }
                writer.newLine();
            }
            writer.write("}\n\n");
        }
    }
}
