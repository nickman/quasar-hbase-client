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

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * <p>Title: FiberHBaseRPCBuilder</p>
 * <p>Description: Base rpc builder. Contains the fields, getters and setters for the most common RPC arguments</p> 
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.hbase.async.FiberHBaseRPCBuilder</code></p>
 * @param <T> The underlying builder type
 * @param <R> The return type of the RPC
 * @param <H> The type of HBaseRpc wrapped by the FiberHBaseRPC
 * @param <V> The type of FiberHBaseRpc the builder builds
 * 
 */
@SuppressWarnings("unchecked")
public abstract class FiberHBaseRPCBuilder<T extends FiberHBaseRPCBuilder<T, R, H, V>, R, H extends HBaseRpc, V extends FiberHBaseRPC<R, H>> {
	
	/** The table for which this RPC is. {@code null} if this RPC isn't for a particular table. */
	byte[] table = null;  
	/** The row key for which this RPC is. {@code null} if this RPC isn't for a particular row key. */
	byte[] key = null;  
	/** The row qualifier */
    byte[] qualifier = null;
	/** The multi row qualifiers */
    byte[][] qualifiers = null;
	/** The family type bytes */
	byte[] family = null;
	/** If true, the RPC to be built should fail-fast as soon as we know we have a problem. */
	boolean failfast = false;
	/** If true, the RPC to be built is a probe which checks if the destination region is online. */
	boolean probe = false;
	/** Whether or not the RPC to be built is a probe that is suspended by an NSRE */
	private boolean suspended_probe = false;
	/** Indicates if the rpc to be built should be traced. (Not implemented ?) */
	private boolean trace_rpc = false;
	/** The timeout of the rpc to be built in millis. Defaults to -1 */
	int rpcTimeout = -1;
	/** The region the rpc to be built will go to */
	RegionInfo region = null;
	
	/** The asynchbase client that will execute the built rpc */
	protected final HBaseClient hbClient;
	
	/** The most recently built FiberHBaseRpc */
	protected volatile V asyncRpc = null;
	

	

    
    /**
     * Creates a new FiberHBaseRPCBuilder
     * @param hbClient The asynchbase client that will execute the built rpc
     */
    FiberHBaseRPCBuilder(final HBaseClient hbClient) {		
		this.hbClient = hbClient;
	}

	/**
     * Builds and validates the fiber async rpc, overwriting the existing one if present 
     * @return the built fiber async rpc 
     */
    public abstract V buildAsyncRpc();
    
    /**
     * Rebuilds the underlying fiber async rpc 
     * @return this builder
     */
    public T rebuild() {
    	asyncRpc = buildAsyncRpc();
    	// TODO:
    	return (T)this;
    }
    
    /**
     * Returns the async fiber rpc instance, building it if necessary
     * @return the async fiber rpc instance
     */
    public V getFiberHBaseRpc() {
    	if(asyncRpc==null) buildAsyncRpc();    	
    	return asyncRpc;
    }
    
    /**
     * Returns the async fiber rpc instance's underlying HBaseRpc, building the async fiber rpc if necessary
     * @return the async fiber rpc instance's underlying HBaseRpc
     */
    public H getHBaseRpc() {
    	if(asyncRpc==null) buildAsyncRpc();
    	return asyncRpc.getHbaseRpc();
    }
    
    
    /**
     * Builds the HBase rpc underlying the async fiber rpc
     * @return the HBase rpc underlying the async fiber rpc
     */
    public abstract H buildRpc();
    
    
    /**
     * If the async rpc is null, builds it, then executes the rpc and returns the result
     * @return thge result of the async rpc invocation
     * @throws HBaseException thrown if the actual HBaseRpc fails
     * @throws SuspendExecution Will not be thrown
     */
    public R execute() throws HBaseException, SuspendExecution {
    	if(asyncRpc==null) {
    		buildAsyncRpc();
    	}
    	return asyncRpc.get();
    }
    
    /**
     * Resets this builder
     * @return this builder
     */
    public T reset() {
    	asyncRpc = null;
    	table = null;  
    	key = null;  
        qualifier = null;
        qualifiers = null;
    	family = null;
    	failfast = false;
    	probe = false;
    	suspended_probe = false;
    	trace_rpc = false;
    	rpcTimeout = -1;
    	region = null;
    	return (T)this;
    }
	
    /**
     * Applies the common options to the passed HBaseRpc
     * @param h an HBaseRpc to configure
     * @return the base options configured HBaseRpc
     */
    H apply(final H h) {
    	h.setFailfast(true);
    	h.setProbe(probe);
    	h.setSuspendedProbe(suspended_probe);    	
    	h.setTimeout(rpcTimeout);
    	h.setTraceRPC(trace_rpc);
    	if(region!=null) {
    		if (table == null) {
    			throw new IllegalStateException("Can't use setRegion if no table was given.");
    	    }    		
    		h.setRegion(region);    		
    	}
    	return h;
    }
    
    /**
     * Indicates if the built rpc should fail fast
     * @return true if the built rpc should fail fast, false if retries should be attempted
     */
    public boolean isFailFast() {
    	return failfast;
    }
	
	/**
	 * Returns the table bytes
	 * @return the table bytes
	 */
	public byte[] getTable() {
		return table;
	}

	/**
	 * Returns the family type bytes
	 * @return the family type bytes
	 */
	public byte[] getFamily() {
		return family;
	}
	
	
	/**
	 * Returns the key bytes
	 * @return the key bytes
	 */
	public byte[] getKey() {
		return key;
	}

	/**
	 * Returns the qualifier bytes
	 * @return the qualifier bytes
	 */
	public byte[] getQualifier() {
		return qualifier;
	}
	
	/**
	 * Returns the multi qualifier bytes
	 * @return the multi qualifier bytes
	 */
	public byte[][] getQualifiers() {
		return qualifiers;
	}
	
	/**
	 * Indicates if the RPC to be built is a probe which checks if the destination region is online.
	 * @return true if the RPC to be built is a probe which checks if the destination region is online, false otherwise
	 */
	public boolean isProbe() {
		return probe;
	}

	/**
	 * Indicates if the RPC to be built is a probe that is suspended by an NSRE
	 * @return true if the RPC to be built is a probe that is suspended by an NSRE
	 */
	boolean isSuspendedProbe() {
		return suspended_probe;
	}

	
	
	/**
	 * Sets the table bytes
	 * @param table the table bytes to set
	 * @return this builder
	 */
	public T table(final byte[] table) {
		KeyValue.checkTable(table);
		this.table = table;
		return (T) this;
	}
	
	/**
	 * Sets the table bytes
	 * @param tableName the table name
	 * @return this builder
	 */
	public T table(final String tableName) {
		final byte[] table = tableName.getBytes();
		KeyValue.checkTable(table);
		this.table = table;
		return (T) this;
	}
	
	
	/**
	 * Sets the key bytes
	 * @param key the key bytes to set
	 * @return this builder
	 */
	public T key(final byte[] key) {
		KeyValue.checkKey(key);
		this.key = key;
		return (T) this;
	}
	
	/**
	 * Sets the key bytes
	 * @param keyName the key bytes to set
	 * @return this builder
	 */
	public T key(final String keyName) {
		final byte[] key = keyName.getBytes();
		KeyValue.checkKey(key);
		this.key = key;
		return (T) this;
	}
	
	/**
	 * Sets the qualifier bytes
	 * @param qualifier the qualifier bytes to set
	 * @return this builder
	 */
	public T qualifier(final byte[] qualifier) {
		KeyValue.checkQualifier(qualifier);
		this.qualifier = qualifier;
		return (T) this;
	}
	
	/**
	 * Sets the qualifier bytes
	 * @param qualifierName the qualifierName bytes to set
	 * @return this builder
	 */
	public T qualifier(final String qualifierName) {
		final byte[] qualifier = qualifierName.getBytes();
		KeyValue.checkQualifier(qualifier);
		this.qualifier = qualifier;
		return (T) this;
	}
	
	/**
	 * Sets the multi qualifier bytes
	 * @param qualifiers the qualifiers bytes to set
	 * @return this builder
	 */
	public T qualifiers(final byte[]... qualifiers) {
		for(final byte[] b: qualifiers) {
			KeyValue.checkQualifier(b);
		}		
		this.qualifiers = qualifiers;
		return (T) this;
	}
	
	/**
	 * Sets the family type bytes
	 * @param family the family type bytes to set
	 * @return this builder
	 */
	public T family(final byte[] family) {
		KeyValue.checkFamily(family);
		this.family = family;
		return (T)this;
	}
	
	/**
	 * Sets the family type name
	 * @param familyName the family type namr to set
	 * @return this builder
	 */
	public T family(final String familyName) {
		final byte[] family = familyName.getBytes();
		KeyValue.checkFamily(family);
		this.family = family;
		return (T)this;
	}
	
	/**
	 * Sets whether or not the rpc to be built should fail fast 
	 * @param failfast true to enable failfast, false otherwise
	 * @return this builder
	 */
	public T failFast(final boolean failfast) {
		this.failfast = failfast;
		return (T)this;
	}
	
	/**
	 * Enables or diables the region probe for the rpc to be built
	 * @param probe true to enable, false otherwise
	 * @return this builder
	 */
	public T probe(final boolean probe) {
		this.probe = probe;
		return (T)this;
	}

	/**
	 * Specifies if the RPC to be built is a probe that is suspended by an NSRE
	 * @param suspended_probe true if the RPC to be built is a probe that is suspended by an NSRE, false otherwise
	 * @return this builder
	 */
	T suspendedProbe(final boolean suspended_probe) {
		this.suspended_probe = suspended_probe;
		return (T)this;
	}

	
	/**
	 * Indicates if the rpc to be built should be traced. (Not implemented ?)
	 * @return true if the rpc to be built should be traced, false otherwise
	 */
	public boolean isTraceRPC() {
		return trace_rpc;
	}

	/**
	 * Specifies if the rpc to be built should be traced. (Not implemented ?)
	 * @param trace_rpc true to trace, false otherwise
	 * @return this builder
	 */
	public T traceRPC(final boolean trace_rpc) {
		this.trace_rpc = trace_rpc;
		return (T)this;
	}
	
	/**
	 * Returns the timeout of the rpc to be built in millis.
	 * A value of <b>0</b> makes the timeout infinite.
	 * The default is <b>-1</b> then "hbase.rpc.timeout" will be used by default.
	 * @return the timeout in millis
	 */
	public int getTimeout() {
		return rpcTimeout;
	}
	
	/**
	 * Sets the timeout of the rpc to be built in millis.
	 * @param timeout the timeout in millis. Set <b>0</b> for no timeout and <b>-1</b> for the "hbase.rpc.timeout" default 
	 * @return this builder
	 */
	public T timeout(final int timeout) {
		this.rpcTimeout = (timeout < -1) ? -1 : timeout;
		return (T)this;
	}

	/**
	 * Returns the region the built rpc will go to
	 * @return the region the built rpc will go to or null if not set
	 */
	RegionInfo getRegion() {
		return region;
	}
	
	/**
	 * Sets the region the built rpc will go to
	 * @param region the region
	 * @return this builder
	 */
	T region(final RegionInfo region) {
		this.region = region;
		return (T)this;
	}
	
}
