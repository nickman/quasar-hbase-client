package org.hbase.async;

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
	
//	/**
//	 * Constructor.
//	 * <strong>These byte arrays will NOT be copied.</strong>
//	 * @param table The non-empty name of the table to use.
//	 * @param key The row key to get in that table.
//	 */
//	public FiberGetRequest(final byte[] table, final byte[] key) {
//		super(table, key);
//	}
//
//	/**
//	 * Constructor.
//	 * @param table The non-empty name of the table to use.
//	 * @param key The row key to get in that table.
//	 * <strong>This byte array will NOT be copied.</strong>
//	 */
//	public FiberGetRequest(final String table, final byte[] key) {
//		this(table.getBytes(), key);
//	}
//
//	/**
//	 * Constructor.
//	 * @param table The non-empty name of the table to use.
//	 * @param key The row key to get in that table.
//	 */
//	public FiberGetRequest(final String table, final String key) {
//		this(table.getBytes(), key.getBytes());
//	}
//
//	/**
//	 * Constructor.
//	 * <strong>These byte arrays will NOT be copied.</strong>
//	 * @param table The non-empty name of the table to use.
//	 * @param key The row key to get in that table.
//	 * @param family The column family.
//	 * @since 1.5
//	 */
//	public FiberGetRequest(final byte[] table,
//			final byte[] key,
//			final byte[] family) {
//		super(table, key);
//		this.family(family);
//	}
//
//	/**
//	 * Constructor.
//	 * @param table The non-empty name of the table to use.
//	 * @param key The row key to get in that table.
//	 * @param family The column family.
//	 * @since 1.5
//	 */
//	public FiberGetRequest(final String table,
//			final String key,
//			final String family) {
//		this(table, key);
//		this.family(family);
//	}
//
//	/**
//	 * Constructor.
//	 * <strong>These byte arrays will NOT be copied.</strong>
//	 * @param table The non-empty name of the table to use.
//	 * @param key The row key to get in that table.
//	 * @param family The column family.
//	 * @param qualifier The column qualifier.
//	 * @since 1.5
//	 */
//	public FiberGetRequest(final byte[] table,
//			final byte[] key,
//			final byte[] family,
//			final byte[] qualifier) {
//		super(table, key);
//		this.family(family);
//		this.qualifier(qualifier);
//	}
//
//	/**
//	 * Constructor.
//	 * @param table The non-empty name of the table to use.
//	 * @param key The row key to get in that table.
//	 * @param family The column family.
//	 * @param qualifier The column qualifier.
//	 * @since 1.5
//	 */
//	public FiberGetRequest(final String table,
//			final String key,
//			final String family,
//			final String qualifier) {
//		this(table, key);
//		this.family(family);
//		this.qualifier(qualifier);
//	}
//
//	/**
//	 * Private constructor to build an "exists" RPC.
//	 * @param unused Unused, simply used to help the compiler find this ctor.
//	 * @param table The non-empty name of the table to use.
//	 * @param key The row key to get in that table.
//	 */
//	private FiberGetRequest(final float unused,
//			final byte[] table,
//			final byte[] key) {
//		super(table, key);
//		this.versions |= EXIST_FLAG;
//	}
//
	
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
