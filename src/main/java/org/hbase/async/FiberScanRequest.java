package org.hbase.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hbase.async.GetRequest;
import org.hbase.async.HBaseClient;
import org.hbase.async.HBaseException;
import org.hbase.async.KeyValue;
import org.hbase.async.ScanFilter;
import org.hbase.async.Scanner;

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
public class FiberScanRequest extends FiberAsync< ArrayList<ArrayList<KeyValue>>, HBaseException > {
	private Scanner hbScanner;
	
	// maps family (the key) to qualifiers array (the value)
	private Map<String,String[]> hbColumsMap;
	
	protected FiberScanRequest(HBaseClient hbClient, String table) {
		this.hbScanner = hbClient.newScanner(table);
		this.hbColumsMap = new HashMap<String, String[]>();
	}
	
	@Override
	protected void requestAsync() {
		_setColumnsFilter();
		hbScanner.nextRows()
			.addCallback( new Callback<Void, ArrayList<ArrayList<KeyValue>>>() {
					@Override
					public Void call(ArrayList<ArrayList<KeyValue>> resList) throws Exception {
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
	 * Set maximum number of rows to scan other than default.
	 * 
	 * @param n
	 * @return this instance
	 */
	public FiberScanRequest setMaxNumRows(int n) {
		hbScanner.setMaxNumRows(n);
		return this;
	}
	
	
	/**
	 * Set filter for this scanner.
	 * 
	 * @param filter a ScanFilter instances (ex. 'KeyRegexpFilter')
	 * @return this instance
	 */
	public FiberScanRequest setFilter(ScanFilter filter) {
		hbScanner.setFilter(filter);
		return this;
	}
	
	/**
	 * 
	 * @param family - to specify column filter a family must be specified.
	 * @param qualifiers - gets only specified column qualifier. Can be null when means all qualifiers.
	 * @return
	 */
	public FiberScanRequest addColumnFamilyFilter(String family, String... qualifiers) {
		if ( family!=null )
			hbColumsMap.put(family, qualifiers);
		return this;
	}
	
	/**
	 * GET operation.
	 *  
	 * @return list of asynchbase key-value
	 * @throws SuspendExecution  never thrown, used only to instruments method with quasar fiber.
	 */
	public ArrayList<ArrayList<KeyValue>> nextRows() throws SuspendExecution, HBaseException {
		try {
			return this.run();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new ArrayList<ArrayList<KeyValue>>();
	}
	
	
	
	/*
	 * Utilities
	 */
	
	/**
	 * Sets columns (families and qualifiers) to scans.
	 */
	private  void _setColumnsFilter() {
		int numFam = hbColumsMap.size();
		if ( numFam==0 )
			return;
		
		byte[][] families = new byte[numFam][];
		byte[][][] qualifiers = new byte[numFam][][];
		
		int famIdx = 0;
		for ( String fam : hbColumsMap.keySet() ) {
			families[famIdx] = fam.getBytes();
			
			String[] quals = hbColumsMap.get(fam);
			if ( quals==null )
				qualifiers[famIdx] = null;
			else {
				qualifiers[famIdx] = new byte[quals.length][];
				for(int qualIdx = 0; qualIdx<quals.length ;++qualIdx)
					qualifiers[famIdx][qualIdx] = quals[qualIdx].getBytes();
			}
		}
		
		hbScanner.setFamilies(families, qualifiers);
	}
}
