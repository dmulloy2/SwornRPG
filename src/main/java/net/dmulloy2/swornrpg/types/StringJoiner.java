/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.swornrpg.types;

/**
 * @author dmulloy2
 */

public class StringJoiner
{
	private String glue;
	private StringBuilder builder;

	public StringJoiner(String glue)
	{
		this.glue = glue;
		this.builder = new StringBuilder();
	}

	public final StringJoiner append(final String string)
	{
		builder.append(string + glue);
		return this;
	}

	public final StringJoiner appendAll(final Iterable<String> strings)
	{
		for (String string : strings)
		{
			append(string);
		}

		return this;
	}

	public final StringJoiner appendAll(final String... strings)
	{
		for (String string : strings)
		{
			append(string);
		}

		return this;
	}

	public final StringJoiner trim()
	{
		if (builder.lastIndexOf(glue) >= 0)
		{
			builder.delete(builder.lastIndexOf(glue), builder.length());
		}

		return this;
	}

	public final StringJoiner newString()
	{
		this.builder = new StringBuilder();
		return this;
	}

	public final StringJoiner setGlue(final String glue)
	{
		this.glue = glue;
		return this;
	}

	public final String toUntrimmedString()
	{
		return builder.toString();
	}

	@Override
	public final String toString()
	{
		trim();
		return builder.toString();
	}
}