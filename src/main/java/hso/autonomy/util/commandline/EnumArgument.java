/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */

package hso.autonomy.util.commandline;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumArgument<T extends Enum<T>> extends Argument<T>
{
	private final Class<T> enumType;

	public EnumArgument(String name, T defaultValue, String description, Class<T> enumType)
	{
		super(name, defaultValue, description);
		this.enumType = enumType;
	}

	@Override
	protected String getValueHelp()
	{
		return getValues();
	}

	private String getValues()
	{
		return String.join("|",
				Arrays.stream(enumType.getEnumConstants()).map(this::constantToString).collect(Collectors.toList()));
	}

	@Override
	protected String getDefaultHelp()
	{
		return String.format("(default: %s)", constantToString(defaultValue));
	}

	@Override
	protected T extractValue(String value)
	{
		for (T constant : enumType.getEnumConstants()) {
			if (constantToString(constant).equals(value)) {
				return constant;
			}
		}

		error("%s Available values are: <%s>", getInvalidArgString(value), getValues());
		return null;
	}

	private String constantToString(T constant)
	{
		if (constant == null) {
			return "null";
		}
		String s = Stream.of(constant.name().split("[^a-zA-Z0-9]"))
						   .map(v -> v.substring(0, 1).toUpperCase() + v.substring(1).toLowerCase())
						   .collect(Collectors.joining());
		return Character.toLowerCase(s.charAt(0)) + s.substring(1);
	}
}
