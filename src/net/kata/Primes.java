package net.kata;

import java.util.stream.IntStream;

public class Primes
{
    public static IntStream stream()
    {
        return new PrimesGenerator();
    }
}
