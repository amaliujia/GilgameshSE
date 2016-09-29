package com.airbnb.amaliujia.GilgameshSE;

import com.airbnb.amaliujia.GilgameshSE.constants.ApplicationMode;
import com.airbnb.amaliujia.GilgameshSE.index.Indexer;
import com.airbnb.amaliujia.GilgameshSE.index.IndexerConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.apache.lucene.benchmark.byTask.feeds.TrecContentSource;

public class GilgameshSEApplication {
    static Logger log = Logger.getLogger(GilgameshSEApplication.class.getName());

    public static void main(String[] args) {
        Options options = new Options();

        Option input = new Option("i", "index", true, "index path");
        input.setRequired(true);
        options.addOption(input);
        input = new Option("f", "file", true, "file path");
        options.addOption(input);
        input = new Option("m", "mode", true, "application mode");
        input.setRequired(true);
        options.addOption(input);
        input = new Option("c", "in", true, "index mode");
        options.addOption(input);

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
              Map<String, String> properties = new HashMap<String, String>();
              properties.put("app_mode", "index");
              properties.put("index_mode", "create");
              properties.put("index_path", cmd.getOptionValue("index"));
              properties.put("doc_path", cmd.getOptionValue("file"));
              properties.put("lower_case", "true");
              properties.put("stopwords", "true");
              properties.put("stem", "porter");

              IndexerConfig config = new IndexerConfig(properties);
              Indexer indexer = new Indexer(config);
              indexer.run();
            }
        } catch (IllegalArgumentException e) {
          log.error(String.format("Application received wrong mode %s, err %s", appMode, e.getMessage()));
        } catch (IOException e) {
          log.error(String.format("IO Exception %s", e.getMessage()));
        }
      return;
    }
}
