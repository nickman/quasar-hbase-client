package org.hbase.async.quasar;

import java.util.ArrayList;

import org.hbase.async.GetRequest;
import org.hbase.async.HBaseClient;
import org.hbase.async.HBaseException;
import org.hbase.async.KeyValue;

import com.stumbleupon.async.Callback;

import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.SuspendExecution;

/**
 * Quasar-aware hbase GET operation.
 * 
 * NB: it's not thread-safe.
 * 
 * @author fabio
 *
 */
public class FiberGetRequest extends FiberAsync<ArrayList<KeyValue>, HBaseException> {
	// reference to outer defined hbaseclient (do not close it)
	private HBaseClient hbClient;
	
	private GetRequest hbGet;
	
	protected FiberGetRequest(HBaseClient hbClient, String table, String key) {
		this.hbClient = hbClient;
		this.hbGet = new GetRequest(table, key);
	}
	
	@Override
	protected void requestAsync() {
		
		hbClient.get(hbGet)
			.addCallback( new Callback<Void, ArrayList<KeyValue>>() {
					@Override
					public Void call(ArrayList<KeyValue> resList) throws Exception {
						asyncCompleted( resList );
						return null;	// void
					}
				}
			)
			.addErrback( new Callback<Void, Exception>() {
				@Override
				public Void call(Exception ex) throws Exception {
					asyncFailed( ex );
					return null; // void
				}
			});
	}
	
	/**
	 * 
	 * @param family - to specify column filter a family must be specified.
	 * @param qualifiers - gets only specified column qualifier. Can be null when means all qualifiers.
	 * @return
	 */
	public FiberGetRequest setColumnsFilter(String family, String... qualifiers) {
		
		if ( family!=null ) {
			hbGet.family(family);
			
			if ( qualifiers!=null ) {
				byte[][] quals = new byte[qualifiers.length][];
				int idx = 0;
				for (String q : qualifiers)
					quals[idx++] = q.getBytes();
				
				hbGet.qualifiers(quals);
			}
		}
		
		return this;
	}
	
	/**
	 * Fiber-blocking GET operation.
	 *  
	 * @return list of asynchbase key-value
	 * @throws SuspendExecution  never thrown, used only to instruments method with quasar fiber.
	 */
	public ArrayList<KeyValue> get() throws SuspendExecution, HBaseException {
		try {
			return this.run();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new ArrayList<KeyValue>();
	}
	

}
