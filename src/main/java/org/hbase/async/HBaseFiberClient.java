package org.hbase.async;

import java.util.ArrayList;
import java.util.Date;

import com.heliosapm.utils.jmx.JMXHelper;
import com.heliosapm.utils.lang.StringHelper;
import com.heliosapm.utils.time.SystemClock;

import co.paralleluniverse.common.monitoring.MonitorType;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;



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

	
	public static void log(Object obj) {
		System.out.println(obj);
	}
	
	public static void main(String[] args) {
		log("Test");
		JMXHelper.fireUpJMXMPServer(4923);
		go();
//		try {
//			if(!Fiber.isCurrentFiber()) {
//				fiberPool.newFiber(new SuspendableCallable<Void>(){
//					@Suspendable
//					public Void run() {
//						go();							
//						return null;
//					}				
//				}).start();
//			} else {
//				go();
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace(System.err);
//		}
		SystemClock.sleep(5000);
	}
	
	@Suspendable
	private static void go() {
		HBaseFiberClient fc = null;
		try {
			fc = new HBaseFiberClient("localhost:2181");
			log("Connected");
			for(int i = 0; i < 10000; i++) {
				final ArrayList<KeyValue> rowKeys = fc
					.newGetRequest()
					.table("tsdb-uid")
					.key("host")
					.getFiberHBaseRpc()
					.get();
				log("Count:" + rowKeys.size());
				int cnt = 1;
				for(KeyValue kv: rowKeys) {
					final StringBuilder b = new StringBuilder("KV#").append(cnt).append(" : ").append(new Date(kv.timestamp()));
					b.append("\n\tKey: [").append(StringHelper.bytesToHex(kv.key())).append("]");
					b.append("\n\tValue: [").append(StringHelper.bytesToHex(kv.value())).append("]");
					log(b);
				}
				try {
					Fiber.sleep(500);
				} catch (InterruptedException | SuspendExecution e) {
					e.printStackTrace(System.err);
				}				
			}
			
		} catch (Exception e) {			
			e.printStackTrace(System.err);
		} finally {
			if(fc!=null) try { fc.close(); } catch (Exception x) {/* No Op */}
		}
		
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
