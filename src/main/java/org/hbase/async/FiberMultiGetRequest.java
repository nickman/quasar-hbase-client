package org.hbase.async;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.hbase.async.FiberGetRequestBuilder.FiberGetRequest;
import org.hbase.async.GetRequest;
import org.hbase.async.HBaseClient;
import org.hbase.async.HBaseException;
import org.hbase.async.KeyValue;

import com.stumbleupon.async.Callback;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.SuspendExecution;

/**
 * Quasar-aware hbase multi-GET operation.
 * 
 * NB: it's not thread-safe.
 * 
 * @author fabio
 *
 */
public class FiberMultiGetRequest {
	// reference to outer defined hbaseclient (do not close it)
	private final HBaseClient hbClient;
	
	private final String table;
	private final String[] keys;
	
	private String family;
	private String[] qualifiers;
	
	protected FiberMultiGetRequest(HBaseClient hbClient, String table, String... keys) {
		this.hbClient = hbClient;
		this.table = table;
		this.keys = keys;
	}
	

	
	/**
	 * Set column filter for results.
	 * 
	 * @param family - to specify column filter a family must be specified.
	 * @param qualifiers - gets only specified column qualifier. Can be null when means all qualifiers.
	 * @return
	 */
	public FiberMultiGetRequest setColumnsFilter(String family, String... qualifiers) {
		this.family = family;
		this.qualifiers = qualifiers;
		
		return this;
	}
	
	/**
	 * GET operation.
	 *  
	 * @return list of asynchbase key-value
	 * @throws SuspendExecution  never thrown, used only to instruments method with quasar fiber.
	 */
	public ArrayList<ArrayList<KeyValue>> get() throws SuspendExecution, HBaseException {
		
		Fiber<ArrayList<KeyValue>>[] singleGetArray = new Fiber[keys.length];
		
		// fiber creation loop
		for (int i=0; i<keys.length ;++i) {
			final int idx = i;
			singleGetArray[idx] = new Fiber<ArrayList<KeyValue>>() {
				/* (non-Javadoc)
				 * @see co.paralleluniverse.fibers.Fiber#run()
				 */
				@Override
				protected ArrayList<KeyValue> run() throws SuspendExecution, InterruptedException {
					FiberGetRequest getReq = new FiberGetRequest(hbClient, null);
//					if ( family!=null )
//						getReq.setColumnsFilter(family, qualifiers);
					return getReq.get();
				}
			}.start();
		}
		
		// join loop
		ArrayList<ArrayList<KeyValue>> mgetRes = new ArrayList<ArrayList<KeyValue>>();
		for (int i=0; i<keys.length ;++i) {
			ArrayList<KeyValue> getRes = null;
			try {
				getRes = singleGetArray[i].get();
			} catch (ExecutionException | InterruptedException e) {
				e.printStackTrace();
			}
			mgetRes.add(getRes);
		}
		
		return mgetRes;
	}
	

}
