package com.westleyapp.jdl_schema;

import ch.qos.logback.classic.Level;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(Option.builder("v").longOpt("verbose")
                .argName("verbose")
                .required(false)
                .build());
        options.addOption(Option.builder("d").longOpt("database")
                .argName("database")
                .required(true)
                .hasArg()
                .desc("Database Url, like jdbc:mysql://127.0.0.1:3306/schema")
                .build());
        options.addOption(Option.builder("u").longOpt("username")
                .argName("username")
                .required(true)
                .hasArg()
                .build());
        options.addOption(Option.builder("p").longOpt("password")
                .argName("password")
                .required(true)
                .hasArg()
                .build());
        options.addOption(Option.builder("o").longOpt("output")
                .argName("output")
                .hasArg()
                .required(false)
                .build());
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption("v")) {
                ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com")).setLevel(Level.DEBUG);
            }
            String databaseUrl = commandLine.getOptionValue("d");
            String username = commandLine.getOptionValue("u");
            String password = commandLine.getOptionValue("p");
            String output = commandLine.hasOption("o")
                    ? commandLine.getOptionValue("o")
                    : getOutputNameFromUrl(databaseUrl);
            FileOutputStream os = new FileOutputStream(output);
            JdlSchema jdlSchema = new JdlSchema(
                    databaseUrl,
                    username,
                    password,
                    os);
            LOG.debug("Parsing schema: {}", databaseUrl);
            jdlSchema.run();
        } catch (ParseException e) {
            HelpFormatter helper = new HelpFormatter();
            helper.printHelp("Usage:", options);
            System.exit(0);
        }
    }

    private static String getOutputNameFromUrl(final String databaseUrl) throws ParseException {
        Pattern pattern = Pattern.compile("^jdbc:[^:]+://[^/]+/([^?]+)");
        Matcher matcher = pattern.matcher(databaseUrl);
        if (matcher.find()) {
            return matcher.group(1) + ".jdl";
        }
        throw new ParseException("Invalid databaseUrl");
    }
}
