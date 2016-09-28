package com.airbnb.amaliujia.GilgameshSE.index;

import com.airbnb.amaliujia.GilgameshSE.constants.ApplicationMode;
import com.airbnb.amaliujia.GilgameshSE.constants.IndexingMode;

import java.util.HashMap;
import java.util.Map;


public class IndexerConfig {
    private Map<String, String> propoerties;

    public IndexerConfig() {
        propoerties = new HashMap<String, String>();
    }

    public void setPropoerties(String name, String value) {
        propoerties.put(name, value);
    }

    public ApplicationMode getAppMode() {
        String mode = propoerties.get("app_mode");
        if (mode == null) {
            return ApplicationMode.ERROR;
        }

        if (mode == "index") {
            return ApplicationMode.INDEXING;
        } else if (mode == "search") {
            return ApplicationMode.SEARCHING;
        } else {
            return ApplicationMode.ERROR;
        }
    }

    public IndexingMode getIndexMode() {
        String mode = propoerties.get("index_mode");
        if (mode == null) {
            return IndexingMode.ERROR;
        }

        if (mode == "create") {
            return IndexingMode.CREATE;
        } else if (mode == "update") {
            return IndexingMode.UPDATE;
        } else if (mode == "delete") {
            return IndexingMode.DELETE;
        } else {
            return IndexingMode.ERROR;
        }
    }

    public String getIndexPath() {
        String path = propoerties.get("index_path");
        if (path == null) {
            return "";
        }

        return path;
    }
}
