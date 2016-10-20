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

/**
 * <p>Title: FiberGetRequestBuilder</p>
 * <p>Description: RPC builder for a GetRequest</p> 
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.hbase.async.FiberGetRequestBuilder</code></p>
 */

public class FiberGetRequestBuilder extends FiberHBaseRPCBuilder<FiberGetRequestBuilder, GetRequest> {

	
	/** The optional row lock for the get request to be built */
	RowLock rowLock = null;
	
	
	/**
	 * {@inheritDoc}
	 * @see org.hbase.async.FiberHBaseRPCBuilder#build()
	 */
	@Override
	public GetRequest build() {
		if(table==null) throw new IllegalStateException("The table specifier is null");
		if(key==null) throw new IllegalStateException("The key specifier is null");
		final GetRequest g = new GetRequest(table, key);
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
		g.setFailfast(true);
		
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

}
