package net.kata;

import java.util.stream.IntStream;

public class Primes
{
    private static long preCalcSleeve[] = new long[] { 0x6b74976b2cda59a4L, 0xb4b349e792cd2d9aL, 0x9a5b34d6e92ed659L, 0x5dacb3696693cf25L,
            0x279e4b34b669add2L, 0xd35ba4bb5966d2cdL, 0xcda59a4f3c96696cL, 0x2cd2d9a6b74976b2L, 0x92ed659b4b349e79L, 0x693cf259a5b34d6eL,
            0x669add25dacb3696L, 0x966d2cd279e4b34bL, 0xc96696cd35ba4bb5L, 0x74976b2cda59a4f3L, 0xb349e792cd2d9a6bL, 0x5b34d6e92ed659b4L,
            0xacb3696693cf259aL, 0x9e4b34b669add25dL, 0x5ba4bb5966d2cd27L, 0xa59a4f3c96696cd3L, 0xd2d9a6b74976b2cdL, 0xed659b4b349e792cL,
            0x3cf259a5b34d6e92L, 0x9add25dacb369669L, 0x6d2cd279e4b34b66L, 0x6696cd35ba4bb596L, 0x976b2cda59a4f3c9L, 0x49e792cd2d9a6b74L,
            0x34d6e92ed659b4b3L, 0xb3696693cf259a5bL, 0x4b34b669add25dacL, 0xa4bb5966d2cd279eL, 0x9a4f3c96696cd35bL, 0xd9a6b74976b2cda5L,
            0x659b4b349e792cd2L, 0xf259a5b34d6e92edL, 0xdd25dacb3696693cL, 0x2cd279e4b34b669aL, 0x96cd35ba4bb5966dL, 0x6b2cda59a4f3c966L,
            0xe792cd2d9a6b7497L, 0xd6e92ed659b4b349L, 0x696693cf259a5b34L, 0x34b669add25dacb3L, 0xbb5966d2cd279e4bL, 0x4f3c96696cd35ba4L,
            0xa6b74976b2cda59aL, 0x9b4b349e792cd2d9L, 0x59a5b34d6e92ed65L, 0x25dacb3696693cf2L, 0xd279e4b34b669addL, 0xcd35ba4bb5966d2cL,
            0x2cda59a4f3c96696L, 0x92cd2d9a6b74976bL, 0xe92ed659b4b349e7L, 0x6693cf259a5b34d6L, 0xb669add25dacb369L, 0x5966d2cd279e4b34L,
            0x3c96696cd35ba4bbL, 0xb74976b2cda59a4fL, 0x4b349e792cd2d9a6L, 0xa5b34d6e92ed659bL, 0xdacb3696693cf259L, 0x79e4b34b669add25L,
            0x35ba4bb5966d2cd2L, 0xda59a4f3c96696cdL, 0xcd2d9a6b74976b2cL, 0x2ed659b4b349e792L, 0x93cf259a5b34d6e9L, 0x69add25dacb36966L,
            0x66d2cd279e4b34b6L, 0x96696cd35ba4bb59L, 0x4976b2cda59a4f3cL, 0x349e792cd2d9a6b7L, 0xb34d6e92ed659b4bL, 0xcb3696693cf259a5L,
            0xe4b34b669add25daL, 0xba4bb5966d2cd279L, 0x59a4f3c96696cd35L, 0x2d9a6b74976b2cdaL, 0xd659b4b349e792cdL, 0xcf259a5b34d6e92eL,
            0xadd25dacb3696693L, 0xd2cd279e4b34b669L, 0x696cd35ba4bb5966L, 0x76b2cda59a4f3c96L, 0x9e792cd2d9a6b749L, 0x4d6e92ed659b4b34L,
            0x3696693cf259a5b3L, 0xb34b669add25dacbL, 0x4bb5966d2cd279e4L, 0xa4f3c96696cd35baL, 0x9a6b74976b2cda59L, 0x59b4b349e792cd2dL,
            0x259a5b34d6e92ed6L, 0xd25dacb3696693cfL, 0xcd279e4b34b669adL, 0x6cd35ba4bb5966d2L, 0xb2cda59a4f3c9669L, 0x792cd2d9a6b74976L,
            0x6e92ed659b4b349eL, 0x96693cf259a5b34dL, 0x4b669add25dacb36L, 0xb5966d2cd279e4b3L, 0xf3c96696cd35ba4bL };

    public static IntStream primesOf()
    {
        return IntStream.generate(() -> new Primes().nextPrime());
    }

    int max = 2 << 30 - 1;
    int seg_count = max - 10 >> 19;
    int wheel_offset = 0;
    int size = 4;
    long[] is_composite = new long[0x1000];

    int seg = 0;

    public Primes()
    {
        // wheel is 2-3-5-7
    }

    private int _current = 1;
    private int nextPrime()
    {
        if (_current == 1)
            return 2;

        if (_current == 2)
            return 3;

        if (_current == 3)
            return 5;

        if (_current == 4)
            return 7;

    }

    public int primesOfImpl()
    {
        for (; seg <= seg_count && first > 0; ++seg)
        {
            int seg_start = (seg << 19) + 11;
            int seg_end = (int) Math.min(max, (seg + 1L << 19) + 11L);
            int limit_i = (int) Math.ceil(Math.sqrt(seg_end));

            // wheel-factor away small primes
            for (int i = 0; i < is_composite.length; ++i)
            {
                is_composite[i] = preCalcSleeve[wheel_offset];
                wheel_offset = (wheel_offset + 1) % preCalcSleeve.length;
            }

            // pre-sieve primes we've already found
            for (int i = 4; i < size && results[i] <= limit_i; ++i)
            {
                // start at max of prime squared or minimum multiple of result in range that's not
                // also a multiple of 2
                long mark_start = seg_start / results[i] * results[i];
                if ((seg_start / results[i] & 0x1) == 1)
                {
                    if (mark_start < seg_start)
                        mark_start += 2 * results[i];
                }
                else
                    mark_start += results[i];

                for (long j = Math.max((long) results[i] * (long) results[i], mark_start); j < seg_end; j += results[i] << 1)
                    is_composite[(int) (j - seg_start >> 7)] |= 1L << ((j - seg_start & 0x7FL) >> 1);
            }

            // start sieving
            for (int i = seg_start; i <= limit_i && first > 0; i += 2)
            {
                if ((is_composite[i - seg_start >> 7] & 1L << ((i - seg_start & 0x7F) >> 1)) == 0)
                {
                    // i is prime, mark off all multiples
                    results[size++] = i;
                    first--;
                    // start at i squared
                    for (long j = i * i; j < seg_end; j += i << 1)
                        is_composite[(int) (j - seg_start >> 7)] |= 1L << ((j - seg_start & 0x7FL) >> 1);
                }
            }
            // gather primes past sqrt(n) and are in range
            for (long i = Math.max(limit_i + (limit_i - 1 & 0x1), seg_start); i < seg_end && first > 0; i += 2)
            {
                if ((is_composite[(int) (i - seg_start >> 7)] & 1L << ((i - seg_start & 0x7FL) >> 1)) == 0)
                {
                    results[size++] = (int) i;
                    first--;
                }
            }
        }

        return IntStream.of(results).skip(skip).limit(limit);
    }
}
