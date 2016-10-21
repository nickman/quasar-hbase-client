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

import com.stumbleupon.async.Callback;
import com.stumbleupon.async.Deferred;

import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.SuspendExecution;

/**
 * <p>Title: FiberHBaseRPC</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.hbase.async.FiberHBaseRPC</code></p>
 * @param <R> The return type of the RPC
 * @param <T> The HBaseRPC type this fiber wrapper wraps
 * 
 */

public abstract class FiberHBaseRPC<R, T extends HBaseRpc> extends FiberAsync<R, HBaseException> {

	/** The asynchbase client that will execute the built rpc */
	protected final HBaseClient hbClient;
	/** The fiber wrapped hbase rpc */
	protected T hbaseRpc = null;

	
	
	/**
	 * Creates a new FiberHBaseRPC
	 * @param hbClient The asynchbase client that will execute the built rpc
	 * @param hbaseRpc The fiber wrapped hbase rpc
	 */
	public FiberHBaseRPC(final HBaseClient hbClient, final T hbaseRpc) {
		this.hbClient = hbClient;
		this.hbaseRpc = hbaseRpc;
	}
	
	/**
	 * Invokes the hbase rpc specific op against the asynchbase client
	 * @param hbaseRpc The hbase rpc to execute
	 * @return a deferred result
	 */
	protected abstract Deferred<R> invoke(T hbaseRpc);

	/**
	 * {@inheritDoc}
	 * @see co.paralleluniverse.fibers.FiberAsync#requestAsync()
	 */
	@Override
	protected void requestAsync() {
		invoke(hbaseRpc)
			.addCallback( new Callback<Void, R>() {
					@Override
					public Void call(R resList) throws Exception {
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
	


	public R get() throws SuspendExecution, HBaseException {
		try {
			return this.run();
		} catch (InterruptedException iex) {
			throw new RuntimeException("Fiber was interrupted", iex);
		}
	}

	/**
	 * Returns the underlying HBaseRpc
	 * @return the underlying HBaseRpc
	 */
	public T getHbaseRpc() {
		return hbaseRpc;
	}

	public void setHbaseRpc(final T hbaseRpc) {
		this.hbaseRpc = hbaseRpc;
	}
	
	

}
