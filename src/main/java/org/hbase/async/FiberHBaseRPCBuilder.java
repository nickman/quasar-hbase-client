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

import org.hbase.async.KeyValue;

/**
 * <p>Title: FiberHBaseRPCBuilder</p>
 * <p>Description: Base rpc builder. Contains the fields, getters and setters for the most common RPC arguments</p> 
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.hbase.async.FiberHBaseRPCBuilder</code></p>
 * @param <T> The underlying builder type
 * @param <V> The type of HBaseRpc the builder builds
 */
@SuppressWarnings("unchecked")
public abstract class FiberHBaseRPCBuilder<T extends FiberHBaseRPCBuilder<T, V>, V extends HBaseRpc> {
	/**
	 * The table for which this RPC is.
	 * {@code null} if this RPC isn't for a particular table.
	 */
	byte[] table = null;  

	/**
	 * The row key for which this RPC is.
	 * {@code null} if this RPC isn't for a particular row key.
	 */
	byte[] key = null;  

	/**
	 * The region for which this RPC is.
	 * {@code null} if this RPC isn't for a single specific region.
	 */	  
	byte[] region_name = null;
    
	/** The stop key to build a regionInfo */
    byte[] stop_key = null;
    
	/** The row qualifier */
    byte[] qualifier = null;

	/** The multi row qualifiers */
    byte[][] qualifiers = null;
    
	/** The family type bytes */
	byte[] family = null;
	
	/**
	 * If true, this RPC should fail-fast as soon as we know we have a problem.
	 */
	boolean failfast = false;

    
    /**
     * Builds and validates the HBaseRpc 
     * @return the built HBaseRpc 
     */
    public abstract V build();
	
    
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
	 * Returns the region name bytes
	 * @return the region_name bytes
	 */
	public byte[] getRegionName() {
		return region_name;
	}
	
	/**
	 * Returns the stop key bytes
	 * @return the stop_key bytes
	 */
	public byte[] getStopKey() {
		return stop_key;
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
	 * Sets the region name bytes
	 * @param region_name the region name bytes to set
	 * @return this builder
	 */
	public T regionName(final byte[] region_name) {
		this.region_name = region_name;
		return (T) this;
	}
	
	/**
	 * Sets the region name bytes
	 * @param region the region name bytes to set
	 * @return this builder
	 */	
	public T regionName(final String region) {
		final byte[] region_name = region.getBytes();
		this.region_name = region_name;
		return (T) this;
	}
	
	
	/**
	 * Sets the stop key bytes
	 * @param stop_key the stop key bytes to set
	 * @return this builder
	 */
	public T stopKey(final byte[] stop_key) {
		this.stop_key = stop_key;
		return (T) this;
	}
    
	/**
	 * Sets the stop key bytes
	 * @param stopKey the stop key to set
	 * @return this builder
	 */
	public T stopKey(final String stopKey) {
		final byte[] stop_key = stopKey.getBytes();
		this.stop_key = stop_key;
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
	

}
