package magma.tools.benchmark;

import magma.tools.benchmark.controller.BenchmarkController;

public class MagmaChallengeGUI
{
	public static void main(String[] args)
	{
		BenchmarkController.run(args, UserInterface.GUI);
	}
}
