package fr.epita.assistants.myide.domain.entity;

import fr.epita.assistants.myide.domain.service.NodeServiceClass;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public enum AnyAspect implements Aspect{
    INSTANCE;

    @Override
    public Type getType() {return Mandatory.Aspects.ANY;}

    @Override
    public List<Feature> getFeatureList() {
        return Arrays.asList(AnyFeature.values());
        //return Aspect.super.getFeatureList();
    }

    public enum AnyFeature implements Feature {
        /**
         * Remove all nodes of trash files.
         * Trash files are listed, line by line,
         * in a ".myideignore" file at the root of the project.
         */
        CLEANUP {
            private boolean cleanNodes(Node node, List<String> trashFiles)
            {
                if (node.isFolder())
                {
                    boolean result = true;
                    for (Node elt : node.getChildren())
                    {
                        if (!cleanNodes(elt, trashFiles)) {
                            result = false;
                        }
                    }
                    return result;
                }
                if (trashFiles.contains(node.getPath().getFileName().toString())) {
                    return NodeServiceClass.INSTANCE.delete(node);
                }
                return true;
            }

            @Override
            public ExecutionReport execute(Project project, Object... params) {
                Node rootNode = project.getRootNode();
                List<String> trashFiles = new ArrayList<>();
                try {
                  File file = rootNode.getPath().resolve(".myideignore").toFile();
                  Scanner myReader = new Scanner(file);
                  while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    trashFiles.add(data);
                  }
                  myReader.close();
                } catch (Error | FileNotFoundException e) {
                  return () -> false;
                }
                boolean res = cleanNodes(rootNode, trashFiles);
                return () -> true;
            }

            @Override
            public Type type() {
                return Mandatory.Features.Any.CLEANUP;
            }
        },

        /**
         * Remove all trash files and create a zip archive.
         * Archive name must be the same as the project name (root node name).
         */
        DIST {

            private static void createZip(String zipFilePath, String sourceFolderPath) throws IOException {
                try (ArchiveOutputStream outputStream = new ZipArchiveOutputStream(new FileOutputStream(zipFilePath))) {
                    File sourceFolder = new File(sourceFolderPath);
                    addFilesToZip(outputStream, sourceFolder, "");
                }
            }

            private static void addFilesToZip(ArchiveOutputStream outputStream, File file, String parentPath) throws IOException {
                String entryName = parentPath + file.getName();
                if (file.isFile()) {
                    ZipArchiveEntry entry = new ZipArchiveEntry(file, entryName);
                    outputStream.putArchiveEntry(entry);

                    FileInputStream inputStream = new FileInputStream(file);
                    IOUtils.copy(inputStream, outputStream);
                    inputStream.close();

                    outputStream.closeArchiveEntry();
                } else if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null) {
                        for (File childFile : files) {
                            addFilesToZip(outputStream, childFile, entryName + "/");
                        }
                    }
                }
            }

            @Override
            public ExecutionReport execute(Project project, Object... params) {
                ExecutionReport result = CLEANUP.execute(project);
                String zipFilePath = project.getRootNode().getPath().resolve(project.getRootNode().getPath().getFileName() + ".zip").toString();
                String sourceFolderPath = project.getRootNode().getPath().toString();
                try {
                    createZip(zipFilePath, sourceFolderPath);
                } catch (IOException e) {
                    return () -> false;
                }
                return () -> true;
            }

            @Override
            public Type type() {
                return Mandatory.Features.Any.DIST;
            }
        },

        /**
         * Fulltext search over project files.
         */
        SEARCH {
            private void AddToDocument(Node node, IndexWriter indexWriter) throws IOException {
                if (node.isFile()) {
                    File file = node.getPath().toFile();
                    FileReader fileReader = new FileReader(file);
                    Document document = new Document();
                    document.add(new TextField("contents", fileReader));
                    document.add(new StringField("path", file.getPath(), Field.Store.YES));
                    document.add(new StringField("filename", file.getName(), Field.Store.YES));
                    indexWriter.addDocument(document);
                }
                else {
                    for (Node elt : node.getChildren()) {
                        AddToDocument(elt, indexWriter);
                    }
                }
            }

            private boolean searchFiles(Node rootNode, String text) throws IOException, ParseException {
                Directory indexDirectory = FSDirectory.open(rootNode.getPath());
                Analyzer analyzer = new StandardAnalyzer();
                IndexWriterConfig config = new IndexWriterConfig(analyzer);
                IndexWriter iwriter = new IndexWriter(indexDirectory, config);
                AddToDocument(rootNode, iwriter);
                iwriter.close();
                IndexReader indexReader = DirectoryReader.open(indexDirectory);
                IndexSearcher indexSearcher = new IndexSearcher(indexReader);
                QueryParser queryParser = new QueryParser("contents", analyzer);
                Query query = queryParser.parse(text.toLowerCase());
                ScoreDoc[] hits = indexSearcher.search(query, 10).scoreDocs;
                /*for (ScoreDoc hit : hits) {
                    int docId = hit.doc;
                    Document document = indexSearcher.doc(docId);
                    System.out.println("File: " + document.get("filename"));
                }*/
                indexReader.close();
                return hits.length != 0;
            }


            @Override
            public ExecutionReport execute(Project project, Object... params) {
                String text = (String) params[0];
                try {
                    boolean res = searchFiles(project.getRootNode(), text);
                    return () -> res;
                } catch (IOException | ParseException e) {
                    return () -> false;
                }
            }

            @Override
            public Type type() {
                return Mandatory.Features.Any.SEARCH;
            }
        }
    }

}