package bloom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

public class StringBloomFilterTest {
	
	@Test
	public void testFilter() {
		StringBloomFilter filter = new StringBloomFilter(100);
		
		assertFalse(filter.contains("a"));
		assertFalse(filter.contains("b"));
		assertTrue(filter.add("a"));
		assertFalse(filter.add("a"));
		assertTrue(filter.add("b"));
		assertFalse(filter.add("b"));
		assertTrue(filter.contains("a"));
		assertTrue(filter.contains("b"));
		assertFalse(filter.contains("c"));
		assertEquals(2, filter.getCount());
	}
	
	@Test
	public void stressTest() throws IOException {
		StringBloomFilter filter = new StringBloomFilter(5000000);
		BufferedReader reader = new BufferedReader(new FileReader("/usr/share/dict/words"));
		int realCount = 0;
		String line ;
		while ((line = reader.readLine()) != null) {
			realCount++;
			filter.add(line);
		}
		System.out.println(realCount + " " + filter.getCount());
	}
}
