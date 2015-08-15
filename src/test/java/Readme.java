/**
 * Created by prince_shah on 8/14/15.
 */

import com.nearinfinity.bloomfilter.BloomFilter;
import com.nearinfinity.bloomfilter.ToBytes;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Readme {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        BloomFilter<String> bloomFilter = new BloomFilter<String>(0.001, 100, new ToBytes<String>() {
            private static final long serialVersionUID = -2257818636984044019L;
            @Override
            public byte[] toBytes(String key) {
                return key.getBytes();
            }
        });

        List<String> knownValues = new ArrayList<String>();

        for (int i = 0; i < 100; i++) {
            String key = UUID.randomUUID().toString();
            knownValues.add(key);
            bloomFilter.add(key);
        }

        for (String key : knownValues) {
            if (!bloomFilter.test(key)) {
                throw new RuntimeException("False Negative!");
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(baos);
        outputStream.writeObject(bloomFilter);
        outputStream.close();

        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        BloomFilter<String> newBloomFilter = (BloomFilter<String>) inputStream.readObject();
        inputStream.close();

        for (String key : knownValues) {
            if (!newBloomFilter.test(key)) {
                throw new RuntimeException("False Negative!");
            }
        }

        int falsePositive = 0;
        int sampleSize = 100000;
        for (int i = 0; i < sampleSize; i++) {
            if (newBloomFilter.test(UUID.randomUUID().toString())) {
                falsePositive++;
            }
        }

        System.out.println("[" +
                falsePositive +"] false positives out of [" +
                sampleSize + "] while using [" +
                newBloomFilter.getMemorySize() + "] bytes of memory");
    }
}
