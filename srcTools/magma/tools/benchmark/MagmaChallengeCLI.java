package magma.tools.benchmark;

import magma.tools.benchmark.controller.BenchmarkController;

public class MagmaChallengeCLI
{
	public static void main(String[] args)
	{
		BenchmarkController.run(args, UserInterface.CLI);
	}
}
