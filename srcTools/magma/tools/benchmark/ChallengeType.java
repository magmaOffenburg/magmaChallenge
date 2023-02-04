package magma.tools.benchmark;

import magma.tools.benchmark.model.IModelReadOnly;
import magma.tools.benchmark.model.bench.BenchmarkMain;
import magma.tools.benchmark.model.bench.goaliechallenge.GoalieBenchmark;
import magma.tools.benchmark.model.bench.keepawaychallenge.KeepAwayBenchmark;
import magma.tools.benchmark.model.bench.kickchallenge.KickBenchmark;
import magma.tools.benchmark.model.bench.passingchallenge.PassingBenchmark;
import magma.tools.benchmark.model.bench.rollingballchallenge.RollingBallBenchmark;
import magma.tools.benchmark.model.bench.runchallenge.RunBenchmark;
import magma.tools.benchmark.view.bench.BenchmarkTableView;
import magma.tools.benchmark.view.bench.goaliechallenge.GoalieBenchmarkTableView;
import magma.tools.benchmark.view.bench.keepawaychallenge.KeepAwayBenchmarkTableView;
import magma.tools.benchmark.view.bench.kickchallenge.KickBenchmarkTableView;
import magma.tools.benchmark.view.bench.passingchallenge.PassingBenchmarkTableView;
import magma.tools.benchmark.view.bench.rollingballchallenge.RollingBallBenchmarkTableView;
import magma.tools.benchmark.view.bench.runchallenge.RunBenchmarkTableView;

public enum ChallengeType {
	RUN("Run", server -> new RunBenchmark(server, false), RunBenchmarkTableView::getInstance),

	KICK("Kick", KickBenchmark::new, KickBenchmarkTableView::getInstance),

	KEEP_AWAY("Keep Away", KeepAwayBenchmark::new, KeepAwayBenchmarkTableView::getInstance),

	GAZEBO_RUN("Gazebo Run", server -> new RunBenchmark(server, true), RunBenchmarkTableView::getInstance),

	PASSING("Passing", PassingBenchmark::new, PassingBenchmarkTableView::getInstance),

	GOALIE("Goalie", GoalieBenchmark::new, GoalieBenchmarkTableView::getInstance),

	ROLLING_BALL("Rolling Ball", RollingBallBenchmark::new, RollingBallBenchmarkTableView::getInstance);
	
	public static final ChallengeType DEFAULT = ROLLING_BALL;

	public final String name;

	public final String startScriptArgument;

	public final BenchmarkMainConstructor benchmarkMainConstructor;

	public final BenchmarkTableViewConstructor benchmarkTableViewConstructor;

	ChallengeType(String name, BenchmarkMainConstructor benchmarkMainConstructor,
			BenchmarkTableViewConstructor benchmarkTableViewConstructor)
	{
		this.name = name + " Challenge";
		startScriptArgument = this.name.replace(" ", "");
		this.benchmarkMainConstructor = benchmarkMainConstructor;
		this.benchmarkTableViewConstructor = benchmarkTableViewConstructor;
	}

	@Override
	public String toString()
	{
		return name;
	}

	@FunctionalInterface
	public interface BenchmarkMainConstructor {
		BenchmarkMain create(String roboVizServer);
	}

	@FunctionalInterface
	public interface BenchmarkTableViewConstructor {
		BenchmarkTableView create(IModelReadOnly model, String startScriptFolder);
	}
}
