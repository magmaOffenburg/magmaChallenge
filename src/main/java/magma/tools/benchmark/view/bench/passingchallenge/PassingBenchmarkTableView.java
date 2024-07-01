package magma.tools.benchmark.view.bench.passingchallenge;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import magma.tools.benchmark.model.IModelReadOnly;
import magma.tools.benchmark.model.ITeamResult;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.model.bench.passingchallenge.PassingBenchmarkTeamResult;
import magma.tools.benchmark.view.bench.BenchmarkTableView;

public class PassingBenchmarkTableView extends BenchmarkTableView
{
	public static PassingBenchmarkTableView getInstance(IModelReadOnly model, String startScriptFolder)
	{
		PassingBenchmarkTableView view = new PassingBenchmarkTableView(model, startScriptFolder);
		model.attach(view);
		return view;
	}

	private PassingBenchmarkTableView(IModelReadOnly model, String startScriptFolder)
	{
		super(model, startScriptFolder);
	}

	@Override
	public void update(IModelReadOnly arg0)
	{
		List<ITeamResult> teamResults = model.getTeamResults();

		for (ITeamResult teamResult : teamResults) {
			PassingBenchmarkTeamResult passResult = (PassingBenchmarkTeamResult) teamResult;
			int teamRow = getTeamRow(passResult.getName());

			double averageScore = passResult.getScore();
			table.setValueAt(averageScore, teamRow, PassingBenchmarkTableModelExtension.COLUMN_TIME);

			table.setValueAt(passResult.getScore(0), teamRow, PassingBenchmarkTableModelExtension.COLUMN_BEST_TIME);
			table.setValueAt(passResult.getScore(1), teamRow, PassingBenchmarkTableModelExtension.COLUMN_2_BEST_TIME);
			table.setValueAt(passResult.getScore(2), teamRow, PassingBenchmarkTableModelExtension.COLUMN_3_BEST_TIME);

			table.setValueAt("", teamRow, PassingBenchmarkTableModelExtension.COLUMN_STATUS);
		}

		if (!model.isRunning()) {
			enableEditing();
		}
	}

	private int getTeamRow(String name)
	{
		for (int i = 0; i < table.getRowCount(); i++) {
			if (name.equals(table.getValueAt(i, PassingBenchmarkTableModelExtension.COLUMN_TEAMNAME))) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public List<TeamConfiguration> getTeamConfiguration()
	{
		List<TeamConfiguration> result = new ArrayList<>();
		int teamid = 0;
		String teamName;
		do {
			String teamPath = (String) table.getValueAt(teamid, PassingBenchmarkTableModelExtension.COLUMN_PATH);
			teamName = (String) table.getValueAt(teamid, PassingBenchmarkTableModelExtension.COLUMN_TEAMNAME);
			float dropHeight = (Float) table.getValueAt(teamid, PassingBenchmarkTableModelExtension.COLUMN_DROP_HEIGHT);
			if (teamName != null && !teamName.isEmpty()) {
				TeamConfiguration config = new TeamConfiguration(teamName, teamPath, dropHeight);
				result.add(config);
				teamid++;
			}
		} while (teamName != null && !teamName.isEmpty() && teamid < table.getRowCount());

		return result;
	}

	@Override
	protected JTable createTeamTable(List<TeamConfiguration> config)
	{
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(true);
		DefaultTableModel tableModel = PassingBenchmarkTableModelExtension.getInstance(config);
		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(102);
		table.getColumnModel().getColumn(1).setPreferredWidth(56);
		table.getColumnModel().getColumn(2).setPreferredWidth(86);
		table.getColumnModel().getColumn(3).setPreferredWidth(56);
		table.getColumnModel().getColumn(4).setPreferredWidth(56);
		table.getColumnModel().getColumn(5).setPreferredWidth(56);
		table.getColumnModel().getColumn(6).setMinWidth(302);
		table.getColumnModel().getColumn(7).setPreferredWidth(85);
		table.setColumnSelectionAllowed(true);
		table.setAutoCreateRowSorter(true);

		BenchmarkTableCell benchmarkTableCell = new BenchmarkTableCell();
		table.getColumn("status").setCellRenderer(benchmarkTableCell);
		table.getColumn("status").setCellEditor(benchmarkTableCell);
		table.setRowHeight(30);
		table.addMouseListener(new TableMouseListener(PassingBenchmarkTableModelExtension.COLUMN_STATUS,
				PassingBenchmarkTableModelExtension.COLUMN_TEAMNAME));
		return table;
	}

	@Override
	public ResultStatus getStatus(int row)
	{
		return getLocalStatus(row, PassingBenchmarkTableModelExtension.COLUMN_TEAMNAME);
	}
}
