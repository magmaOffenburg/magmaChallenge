package magma.tools.benchmark.view.bench.passingchallenge;

import java.util.Collections;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import magma.tools.benchmark.model.TeamConfiguration;

public class PassingBenchmarkTableModelExtension extends DefaultTableModel
{
	private static final long serialVersionUID = 1L;

	static final int COLUMN_TEAMNAME = 0;

	static final int COLUMN_STATUS = 1;

	static final int COLUMN_TIME = 2;
	static final int COLUMN_BEST_TIME = 3;
	static final int COLUMN_2_BEST_TIME = 4;
	static final int COLUMN_3_BEST_TIME = 5;

	static final int COLUMN_PATH = 6;

	static final int COLUMN_DROP_HEIGHT = 7;

	private final Class<?>[] columnTypes;

	private final boolean[] editableColumns;

	public static PassingBenchmarkTableModelExtension getInstance(List<TeamConfiguration> config)
	{
		final int COLUMNS = 8;

		if (config == null) {
			TeamConfiguration singleTeam = new TeamConfiguration("magma", "/media/sf_RoboCup/Challenge", 0.4f);
			config = Collections.singletonList(singleTeam);
		}

		Object[][] content = new Object[config.size()][COLUMNS];
		int i = 0;
		for (TeamConfiguration team : config) {
			content[i][COLUMN_TEAMNAME] = team.getName();
			content[i][COLUMN_STATUS] = null;
			content[i][COLUMN_TIME] = null;
			content[i][COLUMN_BEST_TIME] = null;
			content[i][COLUMN_2_BEST_TIME] = null;
			content[i][COLUMN_3_BEST_TIME] = null;
			content[i][COLUMN_PATH] = team.getPath();
			content[i][COLUMN_DROP_HEIGHT] = team.getDropHeight();
			i++;
		}

		String[] headers = new String[] {
				"team", "status", "time", "best time", "second best time", "third best time", "path", "drop height"};

		return new PassingBenchmarkTableModelExtension(content, headers);
	}

	public PassingBenchmarkTableModelExtension(Object[][] data, Object[] columnNames)
	{
		super(data, columnNames);

		columnTypes = new Class[] {String.class, Object.class, Float.class, Float.class, Float.class, Float.class,
				String.class, Float.class};

		editableColumns = new boolean[] {true, false, false, false, false, false, true, true};
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
