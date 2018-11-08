package com.david.bank;

import java.util.Timer;
import java.util.TimerTask;

/**
 * wrapper of TimerTask and Timer and provided a job-like interface
 */
public abstract class TimerJob extends TimerTask {
	private Timer timer = new Timer(true);

	public void scheduleAtFixedRate(long rateMS) {
		timer.scheduleAtFixedRate(this, 100, rateMS);
	}

	public void stop() {
		timer.cancel();
	}

}
