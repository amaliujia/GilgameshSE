package com.airbnb.amaliujia.GilgameshSE;

import com.airbnb.amaliujia.GilgameshSE.constants.ApplicationMode;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.apache.lucene.benchmark.byTask.feeds.TrecContentSource;

public class GilgameshSEApplication {
    static Logger log = Logger.getLogger(GilgameshSEApplication.class.getName());

    public static void main(String[] args) {
        Options options = new Options();

        Option input = new Option("i", "index", true, "index path");
        options.addOption(input);
        input = new Option("f", "file", true, "file path");
        options.addOption(input);
        input = new Option("m", "mode", true, "application mode");
        input.setRequired(true);
        options.addOption(input);
        input = new Option("c", "in", true, "application mode");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            log.error(e.getMessage());
            formatter.printHelp("command line options ", options);
            System.exit(1);
            return;
        }

        String appMode = cmd.getOptionValue("mode");
        try {
            if (ApplicationMode.INDEXING == ApplicationMode.valueOf(appMode)) {

            }
        } catch (IllegalArgumentException e) {
            log.error(String.format("Application received wrong mode %s, err %s", appMode, e.getMessage()));
        }


        return;
    }
}
