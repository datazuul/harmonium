package org.dazeend.harmonium;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.dazeend.harmonium.screens.NowPlayingScreen;
import org.dazeend.harmonium.screens.ScreenSaverScreen;

import com.tivo.hme.bananas.BScreen;
import com.tivo.hme.bananas.IBananas;

public final class InactivityHandler {
	
	// If no notable key is pressed for this many milliseconds, we go into the inactive state.
	private static final int INACTIVITY_MILLISECONDS = 20000;
	
	private Harmonium app;
	private Timer idleTimer = new Timer();
	private Date lastActivityDate;
	private boolean inactive = false;
	private ScreenSaverScreen screenSaverScreen;
	
	public InactivityHandler(Harmonium app) {
		this.app = app;
		lastActivityDate = new Date();
		idleTimer.schedule(new InactiveCheckTimerTask(this), 10000, 10000);
	}
	
	/**
	 * Call this any time a notable key (e.g. not volume) is pressed to reset the inactivity timer. 
	 */
	public void resetInactivityTimer(){
		synchronized (this) {

			if (this.app.isInSimulator())
				System.out.println("Resetting inactivity timer.");
			
			lastActivityDate = new Date();
			
			if (inactive) {
				if (this.app.isInSimulator()) {
					System.out.println("Setting inactivity state: ACTIVE.");
					System.out.flush();
				}
				inactive = false;
			}
		}
	}

	private void checkIfInactive() {
		synchronized (this) {
			
			if (inactive)
				return;
			
			Date rightNow = new Date();
			if (rightNow.getTime() - lastActivityDate.getTime() >= INACTIVITY_MILLISECONDS) {
				
				// If we've been inactive, but there's music playing and we're not on the Now Playing screen,
				// go to the Now Playing screen.  Next time we go inactive we'll enable the screen saver.
				if (this.app.getDiscJockey().isPlaying() && (this.app.getCurrentScreen().getClass() != NowPlayingScreen.class) 
						&& this.app.getDiscJockey().getNowPlayingScreen() != null) {
					
					resetInactivityTimer();
					this.app.push(this.app.getDiscJockey().getNowPlayingScreen(), IBananas.TRANSITION_LEFT);
					return;
				}
				
				setInactive();
			}
		}
	}

	/**
	 * Called when the inactivity timer elapses and via Harmonium class for HME's idle event.
	 */
	public void setInactive()	{
		synchronized (this) {
			if (inactive)
				return;
			inactive = true;

			if (this.app.isInSimulator()){
				System.out.println("Setting inactivity state: INACTIVE.");
				System.out.flush();
			}
			
			// Start the screen saver if it's enabled.
			if ( app.getPreferences().useScreenSaver() ) {
				BScreen currentScreen = app.getCurrentScreen();
				if (currentScreen != screenSaverScreen) {
					if (screenSaverScreen == null)
						screenSaverScreen = new ScreenSaverScreen(this.app);
					app.push(screenSaverScreen, IBananas.TRANSITION_FADE);
					app.flush();
				}
			}
		}
	}
	
	private class InactiveCheckTimerTask extends TimerTask {

		private InactivityHandler handler;
		
		public InactiveCheckTimerTask(InactivityHandler handler) {
			this.handler = handler;
		}
		
		@Override
		public void run() {
			handler.checkIfInactive();
		}
	}
}
