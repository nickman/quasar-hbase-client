# *quasar-hbase-client:0.1.0* 

Hbase client for Quasar library (http://docs.paralleluniverse.co/quasar/).
It is a wrapper for [asynchbase](https://github.com/OpenTSDB/asynchbase) library


Install artifact in maven local repository :
```
mvn install
```
Now 'hbase-client' library can be use in your Quasar project :
```
	<dependency>
		<groupId>co.paralleluniverse.quasar</groupId>
		<artifactId>quasar-hbase-client</artifactId>
		<version>0.1.0</version>
	</dependency>
```


## Features 
Basic query operations (GET and SCAN) are supported.

## TODO list 
Add INSERT and UPDATE operations.

## Code examples
GET from table by id :
```java
 final String QUORUM_LIST = "localhost:2181";
 final FiberHBaseClient hbClient = new FiberHBaseClient(QUORUM_LIST);
 ...
 String table = ...;
 String rowId = ...;
 ArrayList<KeyValue> rowElems = hbClient.newGetRequest(table, rowId).get();
 
 // print results
 for (KeyValue kv : rowElems) {
	String family = new String(kv.family());
	String qualifier = new String(kv.qualifier());
	String value = new String(kv.value());
	System.out.println("*** family ["+family+"] qualifier ["+qualifier+"] value ["+value+"]");
 }
```
