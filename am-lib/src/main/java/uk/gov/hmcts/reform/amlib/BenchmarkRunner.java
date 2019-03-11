package uk.gov.hmcts.reform.amlib;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;

public class BenchmarkRunner {
    public static void main(String[] args) throws IOException, RunnerException {
        Options opt = new OptionsBuilder()
            .include(".*")
            .warmupIterations(10)
            .measurementIterations(100)
            .forks(1)
            .build();

        new Runner(opt).run();
    }
}
