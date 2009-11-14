/*
 * Copyright 2008 Charles Perry
 *
 * This file is part of Harmonium, the TiVo music player.
 *
 * Harmonium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Harmonium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with Harmonium.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
 
package org.dazeend.harmonium.screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dazeend.harmonium.Harmonium;
import org.dazeend.harmonium.music.Disc;
import org.dazeend.harmonium.music.Playable;
import org.dazeend.harmonium.music.PlaylistEligible;

import com.tivo.hme.bananas.BView;


/**
 * @author Charles Perry (harmonium@DazeEnd.org)
 *
 */
public class BrowseDiscScreen extends HAlbumInfoListScreen {
	
	private Disc disc;
	
	public BrowseDiscScreen(Harmonium app, final Disc thisDisc) {
		super(app, thisDisc, thisDisc.toString());

		this.disc = thisDisc;
		
		List<Playable> tracks = new ArrayList<Playable>();
		tracks.addAll( thisDisc.getTrackList() );
		Collections.sort(tracks, this.app.getPreferences().getDiscTrackComparator());
		for (Playable track : tracks)
			addToList(track);
	}
	
	public boolean handleAction(BView view, Object action) {
        if(action.equals("right") || action.equals("select")) {
        	Playable track = (Playable)getListSelection();

    		if (this.disc.getTrackList().size() > 1)
    			this.app.push(new TrackScreen(this.app, track, this.disc), TRANSITION_LEFT);
    		else
    			this.app.push(new TrackScreen(this.app, track), TRANSITION_LEFT);

            return true;
        }  
        return super.handleAction(view, action);
    }
	
	/* (non-Javadoc)
	 * Handles key presses from TiVo remote control.
	 */
	@Override
	public boolean handleKeyPress(int key, long rawcode) {

		this.app.checkKeyPressToResetInactivityTimer(key);
		
		switch(key) {
		case KEY_PLAY:
			
			List<PlaylistEligible> playlist = new ArrayList<PlaylistEligible>();
			playlist.add( this.disc );
			boolean shuffleMode = this.app.getPreferences().getTrackDefaultShuffleMode();
			boolean repeatMode = this.app.getPreferences().getTrackDefaultRepeatMode();
			this.app.getDiscJockey().play(playlist, shuffleMode, repeatMode, (Playable)getListSelection());
			return true;
		}
		
		return super.handleKeyPress(key, rawcode);

	}
}
