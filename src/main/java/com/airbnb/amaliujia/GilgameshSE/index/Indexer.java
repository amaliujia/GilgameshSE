package com.airbnb.amaliujia.GilgameshSE.index;


import com.airbnb.amaliujia.GilgameshSE.Analysis.DocLenStoreSimilarity;
import com.airbnb.amaliujia.GilgameshSE.Analysis.EnglishAnalyzerConfigurable;
import org.apache.log4j.Logger;
import org.apache.lucene.benchmark.byTask.feeds.DocData;
import org.apache.lucene.benchmark.byTask.feeds.DocMaker;
import org.apache.lucene.benchmark.byTask.feeds.NoMoreDataException;
import org.apache.lucene.benchmark.byTask.feeds.TrecContentSource;
import org.apache.lucene.benchmark.byTask.utils.Config;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/*
 * @author amaliujia
 */
public class Indexer {
    static Logger log = Logger.getLogger(Indexer.class.getName());

    private IndexWriter indexWriter;
    private final String docPath;
    private final String indexPath;

    public Indexer(IndexerConfig config) {
        EnglishAnalyzerConfigurable analyzer = new EnglishAnalyzerConfigurable(Version.LUCENE_43);
        indexPath = config.getIndexPath();
        docPath = config.getDocPath();

        if (indexPath == null || docPath == null) {
            log.error("Index path or dodPath is empty");
            System.exit(1);
        }

        try {
            Directory dir = FSDirectory.open(new File(indexPath));
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_43, analyzer);

            indexWriterConfig.setSimilarity(new DocLenStoreSimilarity());


            // Only do create index now (will delete any existing index)
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            // Optional: for better indexing performance, if you
            // are indexing many documents, increase the RAM
            // buffer.  But if you do this, increase the max heap
            // size to the JVM (eg add -Xmx512m or -Xmx1g):
            //
            // iwc.setRAMBufferSizeMB(10240.0);

            indexWriter = new IndexWriter(dir, indexWriterConfig);
        } catch (IOException e) {
            log.error(String.format("Failed to create IndexWriter %s", e.getMessage()));
            e.printStackTrace();
        }
    }

    public void run() throws IOException {
        TrecContentSource fileSource = new TrecContentSource();
        Properties pr = new Properties();
        pr.setProperty("work.dir", (new File(docPath)).getAbsolutePath());

        pr.setProperty("docs.dir", (new File(docPath)).getAbsolutePath());
        pr.setProperty("content.source.forever", "false");
        pr.setProperty("content.source.log.step", "100");
        pr.setProperty("content.source.verbose", "true");
        Config cr = new Config(pr);
        fileSource.setConfig(cr);
        fileSource.resetInputs();

        index(indexWriter, fileSource);
    }

    private void index(IndexWriter writer, TrecContentSource trecContentSource) {
        FieldType storedTextField = new FieldType();
        storedTextField.setIndexed(true);
        storedTextField.setStoreTermVectors(true);
        storedTextField.setStoreTermVectorPositions(true);
        storedTextField.setTokenized(true);
        storedTextField.freeze();

        DocData d = new DocData();
        long count = 1;

        while (true) {
            try {
                d = trecContentSource.getNextDocData(d);

                // make a new, empty document
                Document doc = new Document();

                doc.add(new StringField("externalId", d.getName(), Field.Store.YES));
                String data = d.getDate();
                if (data != null) {
                    doc.add(new StringField("date", d.getDate(), Field.Store.YES));
                }

		        /*
		        *  Add the content fields.  Specify a Reader, so that the text is
		        *  tokenized, indexed, and stored.
		        */
                doc.add(new Field("title",
                        new BufferedReader(new StringReader(d.getTitle())),
                        storedTextField));

                doc.add(new Field("body",
                        new BufferedReader(new StringReader(d.getBody())),
                        storedTextField));

                if (d.getProps().getProperty("keywords") != null)
                    doc.add(new Field("keywords",
                            new BufferedReader(new StringReader(d.getProps().getProperty("keywords"))),
                            storedTextField));

                if (d.getProps().getProperty("inlink") != null)
                    doc.add(new Field("inlink",
                            new BufferedReader(new StringReader(d.getProps().getProperty("inlink"))),
                            storedTextField));

                if (d.getProps().getProperty("url") != null) {
                    String u = d.getProps().getProperty("url");

                    u = u.replace('.', ' ');
                    u = u.replace('_', ' ');

                    doc.add(new Field("url",
                            new BufferedReader(new StringReader(u)),
                            storedTextField));
                }

		        /*
		         *  Add the document to the index.
		         */
                writer.addDocument(doc);
                count++;
            } catch (NoMoreDataException e) {
                log.error(String.format("%s", e.getMessage()));
                break;
            } catch (IOException e) {
                log.error("Caught IOException: " + e.getMessage());
                break;
            }

            if ((count % 100) == 0)
                log.info(count + " documents...");
        }
    }
}
