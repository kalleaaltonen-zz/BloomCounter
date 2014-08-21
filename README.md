Bloom Counter
=============

Use bloom filters (http://en.wikipedia.org/wiki/Bloom_filter) to get approximate unique counts of elements in large data sets.
 
Generate some test data (10 million random lines):
```bash
hexdump /dev/random | head -n 10000000 > testfile.txt
```

Build the project:

```bash
ant build
```

Simplest usage is to read from STDIN and give size of the filter as parameter:

```bash
cat testfile.txt | java -cp bin bloom.StringBloomFilter 100000000
```

First positional parameter is the size of the underlying bitmap used by the bloom filter. In this case 100 million bits, or 12.5 million bytes. This must be chosen depending on the expected number of unique elements. As number of unique elements rises, the probability of collision rises. 

