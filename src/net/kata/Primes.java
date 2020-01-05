package net.kata;

import sun.security.util.BitArray;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

public class Primes
{
    public static void main(String[] args)
    {
	    System.out.println("Hello");
    }

    public static IntStream stream()
    {
        Iterator<Integer> primes = new Primes().getIterator();
        return IntStream.iterate(primes.next(), p -> primes.next());
    }

    private Iterator<Integer> getIterator()
    {
        return new PrimesIterator();
    }

    class PrimesIterator implements Iterator<Integer>
    {
        private PrimitiveIterator.OfInt _primesInFirstSieve;
        private int _index;
        private int _lastPrime;
        private int _sieves;
        private int _elemsInSieve;
        private PrimitiveIterator.OfInt _primesInCurrentSieve;
        private int _currentSieve;
        private BitArray _candidates;
        private ArrayList<Integer> _firstSieve = new ArrayList<>();

        public PrimesIterator()
        {
            long limit = (long)Math.floor(Math.sqrt(Integer.MAX_VALUE) + 1);

            _elemsInSieve = (limit >= Integer.MAX_VALUE ? (int)limit / 2 : (int)limit) + 1;
            _sieves = Integer.MAX_VALUE / _elemsInSieve + (Integer.MAX_VALUE % _elemsInSieve > 0 ? 1 : 0);
            _primesInFirstSieve = ErathosteneSieve(_elemsInSieve).iterator();
            _index = 0;

            _currentSieve = 1;
            _candidates = new BitArray(_elemsInSieve);

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
            if (_primesInFirstSieve.hasNext())
            {
                _lastPrime = _primesInFirstSieve.next();
                _firstSieve.add(_lastPrime);
                ++_index;
            }
            else
            {
                if (_primesInCurrentSieve == null || !_primesInCurrentSieve.hasNext())
                {
                    NewSieve();

                    int start = _currentSieve * _elemsInSieve;
                    int stop = start + _elemsInSieve > Integer.MAX_VALUE ? Integer.MAX_VALUE : start + _elemsInSieve;

                    for (int i = 0; i < _firstSieve.size(); ++i)
                    {
                        int p = _firstSieve.get(i);
                        long from = start / p * p;
                        if (from < start)
                            from += p;

                        for (long j = from; j < stop; j += p)
                            _candidates.set((int)(j - start), false);
                    }

                    _primesInCurrentSieve = IntStream.range(start, stop-1).filter(n -> _candidates.get(n-start)).iterator();
                    ++_currentSieve;
                }

                _lastPrime = _primesInCurrentSieve.next();
                ++_index;
            }

            return _lastPrime;
        }

        private void NewSieve()
        {
            for (int i = 0; i < _candidates.length(); ++i)
                _candidates.set(i, true);
        }

        private IntStream ErathosteneSieve(int upTo)
        {
            BitSet primes = new BitSet(upTo + 1);
            for (int i = 0; i <= upTo; ++i)
                primes.set(i, true);

            for (int p = 2; p * p <= upTo; ++p)
                if (primes.get(p))
                    for (int i = p * 2; i <= upTo; i += p)
                        primes.set(i, false);

            return primes.stream().skip(2);
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
