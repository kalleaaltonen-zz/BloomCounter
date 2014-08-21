package bloom;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class StringBloomFilter {
	
	private final BitSet bits ;
	private final int size;
	private int count = 0;
	
	/**
	 * 
	 * @param size Size of the underlying bitset
	 */
	public StringBloomFilter(int size) {
		this.size = size;
		this.bits = new BitSet(size);
	}

	/**
	 * @param element
	 * @return true if element was added to the filter. false if addition fails because of a collision.
	 */
	public boolean add(String element) {
		List<Integer> hashes = getHashes(element);
		if(contains(hashes))
			return false;
		
		for(Integer hash: hashes) {
			bits.set(index(hash));
		}
		count++;
		return true;
	}
	
	/** 
	 * if any of the bitmap locations are false, the element is has not been added.
	 * @param element
	 * @return
	 */
	
	public boolean contains(String element) {
		return contains(getHashes(element));
	}
	
	private boolean contains(List<Integer> hashes) {
		for(Integer hash: hashes) 
			if(!bits.get(index(hash))) 
				return false;
		return true;
		
	}
	/**
	 * 
	 * @return How many items does this filter contain.
	 */
	public int getCount() {
		return count;
	}
	
	private int index(int hash) {
		if (hash < 0) 
			return hash % size + size;
		else
			return hash % size;
	}
	
	/**
	 * We get SHA-256 hash of the string and split it into 4 integers, which are used as the hashes.
	 * 
	 * @param element
	 * @return
	 */
	private List<Integer> getHashes(String element) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException ignored) {}

		try {
			md.update(element.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException ignored) {}
		
		ByteBuffer bb = ByteBuffer.wrap(md.digest());
		
		List<Integer> hashes = new ArrayList<Integer>();
		while(hashes.size() < 4) {
			try {
				hashes.add(bb.getInt());
			} catch (BufferUnderflowException e) {
				break;
			}
		}
		
		return hashes;
	}
	
	public static void usage() {
		System.out.println("foo <size> [input]");
	}
	
	public static void main(String args[]) {
		StringBloomFilter filter = null;
		if(args.length == 0) {
			usage();
			System.exit(1);
		}
		
		filter = new StringBloomFilter(Integer.parseInt(args[0]));
		
		BufferedReader br = null;
		if(args.length > 1) {
			try {
				br = new BufferedReader(new FileReader(args[1]));
			} catch (FileNotFoundException e) {
				usage();
				System.err.println("Input file " + args[1] + " not found!");
			}
		} else {
			br = new BufferedReader(new InputStreamReader(System.in));
		}
		
		int realCount = 0;
		String line;
		try {
			while((line = br.readLine()) != null) {
				realCount++;
				filter.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Unique elements according to bloom filter: "+ filter.getCount() + " \nLines added: " + realCount);
	}
}
