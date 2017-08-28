package magma.tools.benchmark.view.bench.keepawaychallenge;

import java.util.List;
import javax.swing.table.DefaultTableModel;
import magma.tools.benchmark.model.TeamConfiguration;

class KeepAwayBenchmarkTableModelExtension extends DefaultTableModel
{
	static final int COLUMN_TEAMNAME = 0;

	static final int COLUMN_STATUS = 1;

	static final int COLUMN_TIME = 2;

	static final int COLUMN_PATH = 3;

	static final int COLUMN_DROP_HEIGHT = 4;

	private final Class<?>[] columnTypes;

	private final boolean[] editableColumns;

	public static KeepAwayBenchmarkTableModelExtension getInstance(List<TeamConfiguration> config)
	{
		final int COLUMNS = 5;

		Object[][] content = new Object[config.size()][COLUMNS];
		int i = 0;
		for (TeamConfiguration team : config) {
			content[i][COLUMN_TEAMNAME] = team.getName();
			content[i][COLUMN_STATUS] = null;
			content[i][COLUMN_TIME] = null;
			content[i][COLUMN_PATH] = team.getPath();
			content[i][COLUMN_DROP_HEIGHT] = team.getDropHeight();
			i++;
		}

		String[] headers = new String[] {"team", "status", "time", "path", "drop height"};

		return new KeepAwayBenchmarkTableModelExtension(content, headers);
	}

	private KeepAwayBenchmarkTableModelExtension(Object[][] data, Object[] columnNames)
	{
		super(data, columnNames);

		columnTypes = new Class[] {String.class, Object.class, Float.class, String.class, Float.class};

		editableColumns = new boolean[] {true, false, false, true, true};
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return columnTypes[columnIndex];
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return editableColumns[column];
	}
}
