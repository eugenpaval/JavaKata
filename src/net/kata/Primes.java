package net.kata;

import java.util.BitSet;
import java.util.Iterator;
import java.util.stream.IntStream;

public class Primes
{
    public static void main(String[] args)
    {
	    System.out.println("Hello");
    }

    public static IntStream stream()
    {
        Iterator<Integer> iter = new Primes().getIterator();

        return IntStream.iterate(iter.next(), p -> iter.next());
    }

    private Iterator<Integer> getIterator()
    {
        return new PrimesIterator();
    }

    class PrimesIterator implements Iterator<Integer>
    {
        private int[] _primesInFirstSieve;
        private int _index;
        private int _lastPrime;
        private int _sieves;
        private int _elemsInSieve;
        private int[] _primesInCurrentSieve;
        private int _currentSieve;
        private int _nextPrimeInSieveIndex;
        private Boolean[] _candidates;

        public PrimesIterator()
        {
            long limit = (long)Math.floor(Math.sqrt(Integer.MAX_VALUE) + 1);

            _elemsInSieve = (limit >= Integer.MAX_VALUE ? (int)limit / 2 : (int)limit) + 1;
            _sieves = Integer.MAX_VALUE / _elemsInSieve + (Integer.MAX_VALUE % _elemsInSieve > 0 ? 1 : 0);
            _primesInFirstSieve = ErathosteneSieve(_elemsInSieve);
            _index = 0;

            _currentSieve = 1;
            _primesInCurrentSieve = new int[0];
            _nextPrimeInSieveIndex = 0;
            _candidates = new Boolean[_elemsInSieve];

            NewSieve();
        }

        @Override
        public boolean hasNext()
        {
            return _index < _sieves * _elemsInSieve;
        }

        @Override
        public Integer next()
        {
            if (_index < _primesInFirstSieve.length)
                _lastPrime = _primesInFirstSieve[_index++];
            else
            {
                if (_nextPrimeInSieveIndex >= _primesInCurrentSieve.length)
                {
                    int start = _currentSieve * _elemsInSieve;
                    int stop = start + _elemsInSieve > Integer.MAX_VALUE ? Integer.MAX_VALUE : start + _elemsInSieve;

                    for (int i = 0; i < _primesInFirstSieve.length; ++i)
                    {
                        int p = _primesInFirstSieve[i];
                        long from = start / p * p;
                        if (from < start)
                            from += p;

                        for (long j = from; j < stop; j += p)
                            _candidates[(int)(j - start)] = false;
                    }

                    _primesInCurrentSieve =  IntStream.range(0, _candidates.length).mapToObj(i -> new Tuple(i, _candidates[i])).filter(c -> c.getValue()).mapToInt(t -> t.getIndex() + start).toArray();

                    _nextPrimeInSieveIndex = 0;
                    ++_currentSieve;
                    NewSieve();
                }

                _lastPrime = _primesInCurrentSieve[_nextPrimeInSieveIndex++];
                ++_index;
            }

            return _lastPrime;
        }

        private void NewSieve()
        {
            for (int i = 0; i < _candidates.length; ++i)
                _candidates[i] = true;
        }

        private int[] ErathosteneSieve(int upTo)
        {
            BitSet primes = new BitSet(upTo + 1);
            for (int i = 0; i <= upTo; ++i)
                primes.set(i, true);

            for (int p = 2; p * p <= upTo; ++p)
                if (primes.get(p))
                    for (int i = p * 2; i <= upTo; i += p)
                        primes.set(i, false);

            return primes.stream().skip(2).toArray();
        }
    }

    class Tuple
    {
        private final int _index;
        private final boolean _value;

        public Tuple(int index, boolean value)
        {
            _index = index;
            _value = value;
        }

        public int getIndex()
        {
            return _index;
        }

        public boolean getValue()
        {
            return _value;
        }
    }
}
