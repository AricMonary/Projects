import java.util.Vector;

//BucketHashTable will implement the hashtable with separate chaining.
public class BucketHashTable<K, V> extends HashTableBase<K, V> {
	
	//bucket hash table uses its own data structure to hold items.  
    //we use another Vector to represent the "chain" in each bucket, which represents the first level slot of the table.
	private Vector<Vector<HashItem<K, V>>> _buckets;

	protected int getHash(K item)
	{
		return _hasher.getHash(item, _buckets.size());
	}
	
	//determines whether or not we need to resize
	//for separate chaining, you have the option of NEVER doing resizing
	//because you can always insert new elements.
	//To turn it off, just always return false
	protected boolean needsResize()
	{
		if (_number_of_elements > 0.5 * _buckets.size())
			return true;
		return false;
	}	
	
	@Override
	protected void resizeCheck() {		
		//resize if necessary
		if (needsResize())
		{
			_local_prime_index++;
			
			HasherBase<K> hasher = _hasher;
			BucketHashTable<K, V> new_hash = new BucketHashTable<K, V>(hasher, _primes[_local_prime_index]);
			
			for (Vector<HashItem<K, V>> item: _buckets)
			{
				for (HashItem<K, V> sub_item: item)
				{
					if (sub_item.isEmpty() == false)
					{
						//add to new hash table
						new_hash.addElement(sub_item.getKey(), sub_item.getValue());
					}
				}
			}
			
			_buckets = new_hash._buckets;
			//System.out.println("Resized Hashtable to size " + _primes[_local_prime_index] + ".");
		}
	}
	
	//helper function to initialize the buckets
	//this time we use a void function to set the _buckets directly
	private void initBuckets(int number_of_elements)
	{
		_buckets = new Vector<>(number_of_elements);
		//and fill it
		for(int i = 0; i < _buckets.capacity(); i ++)
		{
			//Note that, the Vector<> value in sub_item is null at this point. 
			Vector<HashItem<K,V>> sub_item = new Vector<>();
			_buckets.addElement(sub_item);
		}
	}
	
	//define the constructors
	public BucketHashTable(HasherBase<K> hasher)
	{
		//constructor chaining: 
		//we pass in the default value of 11 as number_of_elements to the constructor below
		this(hasher, 11);		
	}
	
	public BucketHashTable(HasherBase<K> hasher, int number_of_elements)
	{
		initBuckets(number_of_elements);
		_hasher = hasher;
		_local_prime_index = 0;
		_number_of_elements = 0;
		
		while (_primes[_local_prime_index] < number_of_elements)
		{
			_local_prime_index++;
		}
	}
	
	//copy constructor 
	public BucketHashTable(BucketHashTable<K, V> other)
	{
		_hasher = _hasher;
		_items = new Vector<>(other._items); //shallow copy - so strictly speaking this is not good enough. We might come back to this later
		_local_prime_index = other._local_prime_index;
		_number_of_elements = other._number_of_elements;
	}


	@Override
	public void addElement(K key, V value) 
	{
		resizeCheck();
		int hash = getHash(key);
		
		//find the bucket
		Vector<HashItem<K, V>> bucket = _buckets.elementAt(hash);
		
		// MA2 TODO
		int iteration = 0;
		while(iteration < (bucket.size()))
		{
			//if not lazily deleted, matching keys, and matching values
			if(!bucket.get(iteration).isEmpty() && bucket.get(iteration).getKey() == key && bucket.get(iteration).getValue() == value)
			{
				return;
			}
			//if lazily deleted, matching keys, and matching values
			else if(bucket.get(iteration).isEmpty() && bucket.get(iteration).getKey() == key && bucket.get(iteration).getValue() == value)
			{
				bucket.get(iteration).setIsEmpty(false);
				_number_of_elements++;
				return;
			}
			//if matching keys and not matching values
			else if(bucket.get(iteration).getKey() == key && bucket.get(iteration).getValue() != value)
			{
				bucket.elementAt(iteration).setIsEmpty(false);
				bucket.elementAt(iteration).setValue(value);
				_number_of_elements++;
				return;
			}
			iteration++;
		}
		//if the key is not already added and needs to be added to the bucket
		HashItem<K, V> item = new HashItem<K, V>(key, value, false);
		bucket.add(item);
		_number_of_elements++;
	}
	
	@Override
	public void removeElement(K key) 
	{
		int hash = getHash(key);
		
		//find the bucket
		Vector<HashItem<K, V>> bucket = _buckets.elementAt(hash);
				
		// MA2 TODO
		for(int iteration = 0; iteration < bucket.size(); iteration++)
		{
			//if the value is already lazily deleted
			if(bucket.get(iteration).isEmpty() && bucket.get(iteration).getKey() == key)
			{
				//System.out.println("The key " + key + " is already been lazily deleted.");
				return;
			}
			//if the key matches and is not already deleted
			else if(!bucket.get(iteration).isEmpty() && bucket.get(iteration).getKey() == key)
			{
				bucket.get(iteration).setIsEmpty(true);
				//System.out.println("The key " + key + " is has been lazily deleted.");
				_number_of_elements--;
				return;
			}
		}
		//if the key does not match the element in the bucket
		//System.out.println("The key " + key + " is not found for removal.");
		return;
	}

	@Override
	public boolean containsElement(K key) {
		int hash = getHash(key);
		Vector<HashItem<K, V>> bucket = _buckets.elementAt(hash);
		
		for(int iteration = 0; iteration < bucket.size(); iteration++)
		{
			//if the item is found but is lazily deleted
			if(bucket.get(iteration).isEmpty() && bucket.get(iteration).getKey() == key)
			{
				//System.out.println("The key " + key + " was found but it is lazily deleted.");
				return false;
			}
			//if the item is found and is not lazily deleted
			if(bucket.get(iteration).getKey().equals(key))
			{
				//System.out.println("The key " + key + " was found.");
				return true;
			}
		}
		//if there are no HashItems in the bucket
		//System.out.println("There are no items in the bucket that matches the key " + key + ".");
		return false;
	}

	@Override
	public V getElement(K key) {
		int hash = getHash(key);
		Vector<HashItem<K, V>> bucket = _buckets.elementAt(hash);
		
		for(int iteration = 0; iteration < bucket.size(); iteration++)
		{
			//if the item is found but is lazily deleted
			if(bucket.get(iteration).isEmpty() && bucket.get(iteration).getKey().equals(key))
			{
				//System.out.println("The value " + bucket.get(iteration).getValue() 
				//		+ " was found from key " + key + ", but it is lazily deleted.");
				return null;
			}
			//if the item is found and is not lazily deleted
			if(bucket.get(iteration).getKey().equals(key))
			{
				//System.out.println("The value " + bucket.get(iteration).getValue() 
				//		+ " was found from key " + key + ".");
				return bucket.get(iteration).getValue();
			}
		}
		//if there are no HashItems in the bucket
		//System.out.println("This key is not in the Hashtable.");
		return null;
	}
	
	public Vector<Vector<HashItem<K, V>>> getBuckets() {
		return this._buckets;
	}

}
