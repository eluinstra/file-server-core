package dev.luin.file.server.core.querydsl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.querydsl.sql.types.AbstractType;

public class PathType extends AbstractType<Path>
{
	public PathType(int type)
	{
		super(type);
	}

	@Override
	public Class<Path> getReturnedClass()
	{
		return Path.class;
	}

	@Override
	public Path getValue(ResultSet rs, int startIndex) throws SQLException
	{
		return Paths.get(rs.getString(startIndex));
	}

	@Override
	public void setValue(PreparedStatement st, int startIndex, Path value) throws SQLException
	{
		st.setString(startIndex,value.toString());
	}

}
