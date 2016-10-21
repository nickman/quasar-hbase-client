/**
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 */
package org.hbase.async;

import java.util.ArrayList;

import com.stumbleupon.async.Callback;
import com.stumbleupon.async.Deferred;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * <p>Title: FiberGetRequestBuilder</p>
 * <p>Description: RPC builder for a GetRequest</p> 
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.hbase.async.FiberGetRequestBuilder</code></p>
 */

public class FiberGetRequestBuilder extends FiberHBaseRPCBuilder<FiberGetRequestBuilder, ArrayList<KeyValue>, GetRequest, FiberGetRequestBuilder.FiberGetRequest> {

	
	/** The optional row lock for the get request to be built */
	RowLock rowLock = null;
	
	
	
	
	/**
	 * Creates a new FiberGetRequestBuilder
	 * @param hbClient The asynchbase client that will execute the built rpc
	 */
	public FiberGetRequestBuilder(final HBaseClient hbClient) {
		super(hbClient);
	}
	
	
	
	@Override
	public GetRequest buildRpc() {
		if(table==null) throw new IllegalStateException("The table specifier is null");
		if(key==null) throw new IllegalStateException("The key specifier is null");
		final GetRequest g = new GetRequest(table, key);
		super.apply(g);
		apply(g);
		return g;
	}
	
	@Override
	public FiberGetRequest buildAsyncRpc() {
		final GetRequest g = buildRpc();
		final FiberGetRequest f = new FiberGetRequest(hbClient, g);
		return f;
	}

	
	@Override
	public FiberGetRequestBuilder reset() {
		rowLock = null;
		return super.reset();
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.hbase.async.FiberHBaseRPCBuilder#apply(org.hbase.async.HBaseRpc)
	 */
	@Override
	GetRequest apply(final GetRequest g) {
		if(qualifier!=null) {
			g.qualifier(qualifier);
		}
		if(qualifiers!=null) {
			g.qualifiers(qualifiers);
		}
		if(family!=null) {
			g.family(family);
		}
		if(rowLock!=null) {
			g.withRowLock(rowLock);
		}
		return g;
	}
	
	/**
	 * Returns the row lock
	 * @return the row lock or null if one was not set
	 */
	public RowLock getRowLock() {
		return rowLock;
	}
	
	/**
	 * Indicates if this builder has a row lock
	 * @return true if this builder has a row lock, false otherwise
	 */
	public boolean hasRowLock() {
		return rowLock!=null;
	}
	
	/**
	 * Sets a row lock
	 * @param rowLock The row lock to set
	 * @return this builder
	 */
	public FiberGetRequestBuilder rowLock(final RowLock rowLock) {
		this.rowLock = rowLock;
		return this;
	}
	
	/**
	 * Builds and sets a row lock
	 * @param region_name The region name
	 * @param lockid the lock id
	 * @return this builder
	 */
	public FiberGetRequestBuilder rowLock(final byte[] region_name, final long lockid) {		
		this.rowLock = new RowLock(region_name, lockid);
		return this;
	}
	
	/**
	 * <p>Title: FiberGetRequest</p>
	 * <p>Description: A quasar fiber driven wrapper for a {@link GetRequest}</p> 
	 * @author fabio (of https://github.com/fsantagostinobietti/quasar-hbase-client)
	 * @author Whitehead (nwhitehead AT heliosdev DOT org)
	 * <p><code>org.hbase.async.FiberGetRequest</code></p>
	 */
	public static class FiberGetRequest extends FiberHBaseRPC<ArrayList<KeyValue>, GetRequest> {
		/**  */
		private static final long serialVersionUID = 4856609815364019935L;
		
		/**
		 * Creates a new FiberGetRequest
		 * @param hbClient The asynchbase client to execute with
		 * @param hbGet The get request to execute
		 */
		FiberGetRequest(final HBaseClient hbClient, final GetRequest hbGet) {
			super(hbClient, hbGet);
		}
		
		@Override
		protected Deferred<ArrayList<KeyValue>> invoke(final GetRequest hbaseRpc) {
			return hbClient.get(hbaseRpc);
		}
		

	}
	

}
