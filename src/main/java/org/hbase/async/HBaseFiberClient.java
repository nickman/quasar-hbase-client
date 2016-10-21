package org.hbase.async;

import org.hbase.async.HBaseClient;



/**
 * 
 * Thread-safe wrapper for client asynchbase library.
 * You need just one instance for each cluster/quorum.
 * 
 * @author fabio
 *
 */
public class HBaseFiberClient {
	//private final static Logger logger = LoggerFactory.getLogger(HbaseClient.class);
	
	private HBaseClient hbClient = null;
	
	public HBaseFiberClient(String quorum) {
		hbClient = new HBaseClient(quorum);
	}


	public HBaseClient getHbClient() {
		return hbClient;
	}
	
	public void close() {
		hbClient.shutdown();
	}
	

	public FiberGetRequestBuilder newGetRequest() {
		return new FiberGetRequestBuilder(hbClient);
	}


	public FiberMultiGetRequest newMGetRequest(String table, String... keys) {
		return new FiberMultiGetRequest(hbClient, table, keys);
	}
	
	public FiberScanRequest newScanRequest(String table) {
		return new FiberScanRequest(hbClient, table);
	}
	
	
}
