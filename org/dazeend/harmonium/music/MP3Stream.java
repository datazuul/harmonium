package org.dazeend.harmonium.music;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import org.dazeend.harmonium.FactoryPreferences;
import org.dazeend.harmonium.Harmonium;

public class MP3Stream extends HMusic implements Playable
{
	private String _uri;
	
	public MP3Stream(String uri)
	{
		_uri = uri;
	}

	public long getDuration()
	{
		return -1;
	}

	public String getURI()
	{
		return _uri;
	}
	
	public List<Playable> getMembers(Harmonium app)
	{
		List<Playable> members = new ArrayList<Playable>(1);
		members.add(this);
		return members;
	}

	public String toString()
	{
		return _uri;
	}
	
	public String toStringTitleSortForm()
	{
		return _uri;
	}

	public String getDisplayArtistName()
	{
		return null;
	}

	public String getAlbumArtistName()
	{
		return null;
	}

	public String getAlbumName()
	{
		return null;
	}

	public int getReleaseYear()
	{
		return 0;
	}

	public Image getAlbumArt(FactoryPreferences prefs)
	{
		return null;
	}

	public String getArtHashKey()
	{
		return _uri;
	}

	public Image getScaledAlbumArt(FactoryPreferences prefs, int width, int height)
	{
		return null;
	}

	public boolean hasAlbumArt(FactoryPreferences prefs)
	{
		return false;
	}

	public String getContentType()
	{
		return "audio/mpeg";
	}
}
