package com.howtodoinjava.demo.lucene.file;
 
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.*; 

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.util.Date;
import java.util.Scanner; 

public class CreateIndex
{
    public static void main(String[] args)
    {
    	System.out.println("Enter 1 to create main index, 2 to create initial auxillary index, 3 to merge indexes \n");
    	
    	Scanner sc = new Scanner(System.in);
    	
    	int inp = sc.nextInt();
    	
    	
    	String docsPath="", indexPath="";
    	
    	if (inp == 1) {
    		docsPath = "all_tweets";
    		indexPath = "main_index";
    	}
    	else if (inp == 2) {
    		docsPath = "new_tweets";
    		indexPath = "auxillary_index";
    	}
    	else if (inp == 3) {
    		System.out.println("merging");
    		mergeIndexes("main_index", "auxillary_index", "merged_index");
    		System.exit(0);
    	}
    	else {
    		System.out.println("Invalid command");
    		System.exit(0);
    	}
 
        //Input Path Variable
        final Path docDir = Paths.get(docsPath);
 
        try
        {
            //org.apache.lucene.store.Directory instance
            Directory dir = FSDirectory.open( Paths.get(indexPath) );
             
            //analyzer with the default stop words
            Analyzer analyzer = new StandardAnalyzer();
             
            //IndexWriter Configuration
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(OpenMode.CREATE);
             
            //IndexWriter writes new index files to the directory
            IndexWriter writer = new IndexWriter(dir, iwc);
             
            //Its recursive method to iterate all files and directories
            indexDocs(writer, docDir);
 
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    static void joinIndexes(String main_index, String auxillary_index, String index_path) {
    	try {
    		Date start = new Date(); 
    		
	    	Directory dir = FSDirectory.open( Paths.get(index_path) );
	    	Directory dir_main = FSDirectory.open( Paths.get(main_index) );
	    	Directory dir_aux = FSDirectory.open( Paths.get(auxillary_index) );
	    	
	        
	        Analyzer analyzer = new StandardAnalyzer();
	        
	        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
	        iwc.setOpenMode(OpenMode.CREATE);
	        IndexWriter writer = new IndexWriter(dir, iwc);
	        
	        Directory[] INDEXES_DIR = {dir_main, dir_aux}; 
	        
	        Directory indexes[] = new Directory[INDEXES_DIR.length];
	        
	        for (int i = 0; i < INDEXES_DIR.length; i++) {
                System.out.println("Adding: " + INDEXES_DIR[i]);
                indexes[i] = INDEXES_DIR[i];
                System.out.println(indexes[i]);
            }
	        
	        System.out.print("Merging added indexes...");
            writer.addIndexes(indexes);
            System.out.println("done");
	    	
            System.out.print("Optimizing index...");
//            writer.optimize();
            writer.close();
            System.out.println("done");
            
	    	//writer.setMergeFactor(1000);
	    	//writer.setRAMBufferSizeMB(50);
            Date end = new Date();
            writer.close();
            System.out.println("It took: "+((end.getTime() - start.getTime()) / 1000) + "\"");
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    static int count_of_docs = 0;
    static void numberOfDocumentsInDirectory(Path path) throws IOException
    {
    	//Directory?
    	
        if (Files.isDirectory(path))
        {	
            //Iterate directory
            Files.walkFileTree(path, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    count_of_docs += 1;    
                    
                    String file_name = file.toString().split("/")[1];
                    String destination = "all_tweets/" + file_name;
                    System.out.printf("%s %s %s\n", file_name, destination, file.toString());
                    
                    try {
	                    Path temp = Files.move (Paths.get(file.toString()),  Paths.get(destination));
	                    if(temp != null) 
	                    { 
	                        System.out.println("File renamed and moved successfully"); 
	                    } 
	                    else
	                    { 
	                        System.out.println("Failed to move the file"); 
	                    }
                    }
                    catch (IOException e) {
                		e.printStackTrace();
                	}
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        else
        {
            //Index this file
        	
            
            /*
             * increment number of tweets indexed
             * 
             */
        }
    }
    
    static void moveDocuments(Path path) throws IOException {
    	Files.walkFileTree(path, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                count_of_docs += 1;    
                
                String file_name = file.toString().split("/")[1];
                String destination = "all_tweets/" + file_name;
                System.out.printf("%s %s %s\n", file_name, destination, file.toString());
                
                try {
                    Path temp = Files.move (Paths.get(file.toString()),  Paths.get(destination));
                    if(temp != null) 
                    { 
                        System.out.println("File renamed and moved successfully"); 
                    } 
                    else
                    { 
                        System.out.println("Failed to move the file"); 
                    }
                }
                catch (IOException e) {
            		e.printStackTrace();
            	}
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    
    static void mergeIndexes(String main_index, String auxillary_index, String index_path) {
    	
    	System.out.println("Press 1 for checking index size and merging and 2 for manual merge(Not Recommended)");
    	
    	Scanner sc = new Scanner(System.in);
    	int inp = sc.nextInt();
    	
    	
    	
    	if (inp == 1) {
    		
    		final Path docDir = Paths.get("new_tweets");
    		try {
				numberOfDocumentsInDirectory(docDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
    		
    		System.out.print(count_of_docs);
    		if(count_of_docs > 30) {
    			try {
    				joinIndexes(main_index, auxillary_index, index_path);
					moveDocuments(docDir);	
//					renamePathInIndex();			
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	}
    	else {
    		joinIndexes(main_index, auxillary_index, index_path);
    	}
    	
    	
    }
    
    static void renamePathInIndex() throws IOException {
    	Directory dir;
    	dir = FSDirectory.open(Paths.get("merged_index"));
    	
    }
     
    static void indexDocs(final IndexWriter writer, Path path) throws IOException
    {
        //Directory?
        if (Files.isDirectory(path))
        {
            //Iterate directory
            Files.walkFileTree(path, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    try
                    {
                        //Index this file
                        indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        else
        {
            //Index this file
            indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
            
            /*
             * increment number of tweets indexed
             * 
             */
        }
    }
 
    static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException
    {
        try (InputStream stream = Files.newInputStream(file))
        {
        	
            //Create lucene Document
            Document doc = new Document();
            
            /*
             * Preprocess Files.readAllBytes, add fields accordingly
             */
             
            String[] path = file.toString().split("/");
            int length_of_path = path.length;
            
            doc.add(new StringField("path", path[length_of_path-1], Field.Store.YES));
            doc.add(new LongPoint("modified", lastModified));
            doc.add(new TextField("contents", new String(Files.readAllBytes(file)), Store.YES));
             
            // Files.readAllBytes contains file contents i think
            
            System.out.println("indexed");
        	System.out.println(doc);
        	System.out.println("indexed");
            
            
            //Updates a document by first deleting the document(s)
            //containing <code>term</code> and then adding the new
            //document.  The delete and then add are atomic as seen
            //by a reader on the same index
            writer.updateDocument(new Term("path", path[length_of_path-1]), doc);
        }
    }
}