package magma.tools.benchmark.view.bench.keepawaychallenge;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import magma.tools.benchmark.model.IModelReadOnly;
import magma.tools.benchmark.model.ITeamResult;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.view.bench.BenchmarkTableView;

/**
 *
 * @author kdorer
 */
public class KeepAwayBenchmarkTableView extends BenchmarkTableView
{
	public static KeepAwayBenchmarkTableView getInstance(IModelReadOnly model, String startScriptFolder)
	{
		KeepAwayBenchmarkTableView view = new KeepAwayBenchmarkTableView(model, startScriptFolder);
		model.attach(view);
		return view;
	}

	private KeepAwayBenchmarkTableView(IModelReadOnly model, String startScriptFolder)
	{
		super(model, startScriptFolder);
	}

	@Override
	public List<TeamConfiguration> getTeamConfiguration()
	{
		List<TeamConfiguration> result = new ArrayList<>();
		int teamid = 0;
		String teamName;
		do {
			String teamPath = (String) table.getValueAt(teamid, KeepAwayBenchmarkTableModelExtension.COLUMN_PATH);
			teamName = (String) table.getValueAt(teamid, KeepAwayBenchmarkTableModelExtension.COLUMN_TEAMNAME);
			float dropHeight =
					(Float) table.getValueAt(teamid, KeepAwayBenchmarkTableModelExtension.COLUMN_DROP_HEIGHT);
			if (teamName != null && !teamName.isEmpty()) {
				TeamConfiguration config = new TeamConfiguration(teamName, teamPath, dropHeight);
				result.add(config);
				teamid++;
			}
		} while (teamName != null && !teamName.isEmpty() && teamid < table.getRowCount());

		return result;
	}

	@Override
	public void update(IModelReadOnly model)
	{
		List<ITeamResult> teamResults = model.getTeamResults();

		for (ITeamResult teamResult : teamResults) {
			int teamRow = getTeamRow(teamResult.getName());

			float averageScore = teamResult.getAverageScore();
			table.setValueAt(averageScore, teamRow, KeepAwayBenchmarkTableModelExtension.COLUMN_TIME);

			table.setValueAt("", teamRow, KeepAwayBenchmarkTableModelExtension.COLUMN_STATUS);
		}

		if (!model.isRunning()) {
			enableEditing();
		}
	}

	private int getTeamRow(String name)
	{
		for (int i = 0; i < table.getRowCount(); i++) {
			if (name.equals(table.getValueAt(i, KeepAwayBenchmarkTableModelExtension.COLUMN_TEAMNAME))) {
				return i;
			}
		}
		return -1;
	}

	@Override
	protected JTable createTeamTable(List<TeamConfiguration> config)
	{
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(true);
		DefaultTableModel tableModel = KeepAwayBenchmarkTableModelExtension.getInstance(config);
		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(102);
		table.getColumnModel().getColumn(1).setPreferredWidth(56);
		table.getColumnModel().getColumn(2).setPreferredWidth(56);
		table.getColumnModel().getColumn(3).setMinWidth(302);
		table.getColumnModel().getColumn(4).setPreferredWidth(85);
		table.setColumnSelectionAllowed(true);
		table.setAutoCreateRowSorter(true);

		BenchmarkTableCell benchmarkTableCell = new BenchmarkTableCell();
		table.getColumn("status").setCellRenderer(benchmarkTableCell);
		table.getColumn("status").setCellEditor(benchmarkTableCell);
		table.setRowHeight(30);
		table.addMouseListener(new TableMouseListener(KeepAwayBenchmarkTableModelExtension.COLUMN_STATUS,
				KeepAwayBenchmarkTableModelExtension.COLUMN_TEAMNAME));
		return table;
	}

	@Override
	public ResultStatus getStatus(int row)
	{
		return getLocalStatus(row, KeepAwayBenchmarkTableModelExtension.COLUMN_TEAMNAME);
	}
}
