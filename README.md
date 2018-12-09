# Tweets-Search-Engine
<pre>
 CreateIndex.java   (Developer)  
  3 modes
  - Create Main Index
  - Create Auxillary Index
  - Merge Indexes
    - Based on size
    - Merge Anyway
 
 QueryOnIndex.java  (User)
  User gives query.
 
 
</pre>

## How To Run The Code

<pre>
  1.) Run crawler main.py using command "python3 main.py".
  2.) Setup Apache Lucene in Eclipse
  3.) Make sure you have "all_tweets", "new_tweets" and "main_index" directories in your project directory.
  4.) If you don't have "main_index", run CreateIndex.java in mode 1.
  5.) Create Auxillary index by running CreateIndex.java in mode 2.
  6.) Merge Indexes using Mode 3. Here you have option to merge using size constraint i.e merge if documents in 
      auxillary index exceed a certain theshold(100) or merge anyway.
  7.) Run QueryOnIndex.java and enter query.
    
</pre>
